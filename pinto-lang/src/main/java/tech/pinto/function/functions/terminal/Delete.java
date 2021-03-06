package tech.pinto.function.functions.terminal;

import tech.pinto.Indexer;
import tech.pinto.Namespace;
import tech.pinto.PintoSyntaxException;
import tech.pinto.Table;
import tech.pinto.function.FunctionHelp;
import tech.pinto.function.ComposableFunction;
import tech.pinto.function.TerminalFunction;
import tech.pinto.function.functions.HeaderLiteral;

public class Delete extends TerminalFunction {
	public static final FunctionHelp.Builder HELP_BUILDER = new FunctionHelp.Builder()
			.description("Deletes previously defined name");


	public Delete(String name, Namespace namespace, ComposableFunction previousFunction, Indexer indexer) {
		super(name, namespace, previousFunction, indexer);
	}

	@Override
	public Table getTable() throws PintoSyntaxException {
		if(!(previousFunction.isPresent() && previousFunction.get() instanceof HeaderLiteral)) {
			throw new PintoSyntaxException(name + " requires a string literal for the name.");
		}
		String nameToDelete = ((HeaderLiteral) previousFunction.get()).getValue();
		namespace.undefine(nameToDelete);
		return createTextColumn("Successfully deleted.");
	}	

}
