package tech.pinto.function.functions;

import java.util.LinkedList;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.DoubleStream.Builder;
import java.util.stream.Stream;

import tech.pinto.Indexer;
import tech.pinto.Column;
import tech.pinto.function.ComposableFunction;
import tech.pinto.time.Period;
import tech.pinto.time.PeriodicRange;
import tech.pinto.time.Periodicities;
import tech.pinto.time.Periodicity;
import tech.pinto.tools.CorrelationCollector;


public class RollingCorrelation extends ComposableFunction {

	
	public RollingCorrelation(String name, ComposableFunction previousFunction, Indexer indexer) {
		super(name, previousFunction, indexer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void apply(LinkedList<Column> stack) {
		int size = !stack.getFirst().isHeaderOnly() ? 1 :
			Integer.parseInt(stack.removeFirst().getHeader().replaceAll("\\s","").replaceAll("size=", ""));
		Optional<Periodicity<?>> windowFrequency = !stack.getFirst().isHeaderOnly() ? Optional.empty() :
			Optional.of(Periodicities.get(stack.removeFirst().getHeader().replaceAll("\\s","").replaceAll("freq=", "")));
		Column[] inputStack = stack.toArray(new Column[] {});
		stack.clear();
		stack.add(new Column(inputs -> toString(),
				inputArray -> range -> {
					Periodicity<Period> wf = (Periodicity<Period>) windowFrequency.orElse(range.periodicity());
					Period expandedWindowStart = wf.offset(wf.from(range.start().endDate()), -1 * (size - 1));
					Period windowEnd = wf.from(range.end().endDate());
					PeriodicRange<Period> expandedWindow = wf.range(expandedWindowStart, windowEnd, range.clearCache());
					
					
					List<double[]> inputs = Stream.of(inputArray).map(c -> c.getCells(expandedWindow))
										.map(DoubleStream::toArray).collect(Collectors.toList());
					
					Builder b = DoubleStream.builder();
					double[] input = new double[inputs.size()];
					for(Period p : range.values()) {
						long windowStartIndex = wf.distance(expandedWindowStart, wf.from(p.endDate())) - size + 1;
						CorrelationCollector cc = new CorrelationCollector(inputs.size());
						for(int i = (int) windowStartIndex; i < windowStartIndex + size; i++) {
							for(int j = 0; j < inputs.size(); j++) {
								input[j] = inputs.get(j)[i];
							}
							cc.add(input);
						}
						b.accept(cc.getAverage());
					}
					return b.build();
				}, inputStack));
	}
	
}
