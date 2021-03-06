package tech.pinto.function;

import tech.pinto.Indexer;
import tech.pinto.Namespace;
import tech.pinto.Pinto;

@FunctionalInterface
public interface FunctionFactory {
	
	public ComposableFunction build(String name, Pinto pinto, Namespace namespace, ComposableFunction previousFunction,
								Indexer indexer);

}
