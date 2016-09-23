package tech.pinto.command.doubledouble;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.DoubleStream.Builder;

import com.google.common.base.Joiner;

import tech.pinto.command.ParameterizedCommand;
import tech.pinto.data.DoubleData;
import tech.pinto.time.Period;
import tech.pinto.time.PeriodicRange;
import tech.pinto.time.Periodicities;
import tech.pinto.time.Periodicity;


public class RollingCorrelation extends ParameterizedCommand {

	private int size;
	private final Optional<Periodicity<?>> windowFrequency;
	
	public RollingCorrelation(String... args) {
		super("correl", DoubleData.class, DoubleData.class, args);
		if(args.length < 1) {
			throw new IllegalArgumentException("window requires at least one parameter (window size)");
		} else {
			try {
				size = Math.abs(Integer.parseInt(args[0].replaceAll("\\s+", "")));
			} catch(NumberFormatException nfe) {
				throw new IllegalArgumentException("Non-numeric argument \"" + args[0] + "\" for window"
						+ " size in rolling function args: \"" + Joiner.on(",").join(args) + "\"");
			}
		}
		if(args.length < 2) {
			windowFrequency =  Optional.empty();
		} else {
			Periodicity<?> p =	Periodicities.get(args[1].replaceAll("\\s+", ""));
			if(p == null) {
				throw new IllegalArgumentException("invalid periodicity code for window: \"" + args[1] + "\"");
			}
			windowFrequency =  Optional.of(p);
		}
		inputCount = args.length <= 2 || Integer.parseInt(args[2]) == -1 ? Integer.MAX_VALUE : Integer.parseInt(args[2]);
		outputCount = 1;
	}
	
	@Override
	public <P extends Period> DoubleData evaluate(PeriodicRange<P> range) {
		@SuppressWarnings("unchecked")
		Periodicity<Period> wf = (Periodicity<Period>) windowFrequency.orElse(range.periodicity());
		Period expandedWindowStart = wf.offset(wf.from(range.start().endDate()), -1 * (size - 1));
		Period windowEnd = wf.from(range.end().endDate());
		PeriodicRange<Period> expandedWindow = wf.range(expandedWindowStart, windowEnd, range.clearCache());
		
		
		List<double[]> inputs = inputStack.stream().map(c -> c.evaluate(expandedWindow))
							.map(d -> (DoubleData) d).map(DoubleData::getData).map(DoubleStream::toArray)
							.collect(Collectors.toList());
		
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
		return new DoubleData(range,toString(),b.build());
	}
	
}
