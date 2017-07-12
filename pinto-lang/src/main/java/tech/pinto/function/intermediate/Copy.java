package tech.pinto.function.intermediate;

import java.util.ArrayDeque;

import java.util.LinkedList;

import tech.pinto.function.FunctionHelp;
import tech.pinto.Column;
import tech.pinto.Indexer;
import tech.pinto.function.ComposableFunction;

public class Copy extends ComposableFunction {
	
	
	public Copy(String name, ComposableFunction previousFunction, Indexer indexer) {
		super(name, previousFunction, indexer);
	}

	@Override
	protected LinkedList<Column> compose(LinkedList<Column> stack) {
		int times = getArgs().length == 0 ? 2 : Integer.parseInt(getArgs()[0]);
		ArrayDeque<Column> temp = new ArrayDeque<>();
        stack.stream().forEach(temp::addFirst);
        for(int i = 0; i < times - 1; i++) {
        	temp.stream().map(Column::clone).forEach(stack::addFirst);
        }
		return stack;
	}
	
	public static FunctionHelp getHelp(String name) {
		return new FunctionHelp.Builder(name)
				.description("Copies stack inputs *m* times")
				.parameter("m","2",null)
				.outputs("n * m")
				.build();
	}
}
