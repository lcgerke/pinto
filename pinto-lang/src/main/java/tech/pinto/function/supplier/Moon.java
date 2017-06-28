package tech.pinto.function.supplier;

import java.util.LinkedList;

import tech.pinto.Column;
import tech.pinto.Indexer;
import tech.pinto.function.ComposableFunction;

public class Moon extends ComposableFunction {

	public Moon(String name, ComposableFunction previousFunction, Indexer indexer) {
		super(name, previousFunction, indexer);
	}

	@Override
	public LinkedList<Column> composeIndexed(LinkedList<Column> stack) {
		stack.addFirst(new Column(inputs -> toString(),
				inputs -> range -> range.dates().stream().mapToDouble(d -> new tech.pinto.tools.MoonPhase(d).getPhase())));
		return stack;
	}
}
