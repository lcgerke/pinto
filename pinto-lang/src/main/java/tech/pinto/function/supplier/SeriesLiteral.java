package tech.pinto.function.supplier;

import java.util.LinkedList;
import java.util.stream.DoubleStream;

import tech.pinto.Column;
import tech.pinto.Indexer;
import tech.pinto.function.ComposableFunction;
import tech.pinto.function.ParameterType;

public class SeriesLiteral extends ComposableFunction {

	private final double value;
	
	public SeriesLiteral(ComposableFunction previousFunction, Indexer indexer, double value) {
		super(Double.toString(value),previousFunction, indexer, ParameterType.no_arguments);
		this.value = value;
	}

	@Override
	protected LinkedList<Column> compose(LinkedList<Column> stack) {
		stack.addFirst(new Column(inputs -> Double.toString(value),
				inputs -> range -> DoubleStream.iterate(value, r -> value).limit(range.size())));
		return stack;
	}
}