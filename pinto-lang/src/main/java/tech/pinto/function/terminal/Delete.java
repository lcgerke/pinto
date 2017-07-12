package tech.pinto.function.terminal;

import java.util.LinkedList;

import tech.pinto.ColumnValues;
import tech.pinto.Indexer;
import tech.pinto.Namespace;
import tech.pinto.PintoSyntaxException;
import tech.pinto.function.FunctionHelp;
import tech.pinto.function.ComposableFunction;
import tech.pinto.function.TerminalFunction;
import tech.pinto.function.header.HeaderLiteral;

public class Delete extends TerminalFunction {


	public Delete(String name, Namespace namespace, ComposableFunction previousFunction, Indexer indexer) {
		super(name, namespace, previousFunction, indexer);
	}

	@Override
	public LinkedList<ColumnValues> getColumnValues() throws PintoSyntaxException {
		if(!(previousFunction.isPresent() && previousFunction.get() instanceof HeaderLiteral)) {
			throw new PintoSyntaxException(name + " requires a string literal for the name.");
		}
		String nameToDelete = ((HeaderLiteral) previousFunction.get()).getValue();
		namespace.undefine(nameToDelete);
		return createTextColumn("Successfully deleted.");
	}	

	public static FunctionHelp getHelp(String name) {
		return new FunctionHelp.Builder(name)
				.outputs("none")
				.description("Deletes previously defined command *name*.")
				.parameter("name")
				.build();
	}

}
