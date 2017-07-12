package tech.pinto.function.terminal;

import java.io.BufferedReader;





import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import tech.pinto.Indexer;
import tech.pinto.Namespace;
import tech.pinto.Pinto;
import tech.pinto.PintoSyntaxException;
import tech.pinto.ColumnValues;
import tech.pinto.function.FunctionHelp;
import tech.pinto.function.ComposableFunction;
import tech.pinto.function.TerminalFunction;

public class Execute extends TerminalFunction {

	private final Pinto pinto;
	
	public Execute(String name, Pinto pinto, Namespace namespace, ComposableFunction previousFunction, Indexer indexer) {
		super(name, namespace, previousFunction, indexer);
		this.pinto = pinto;
	}

	@Override
	public LinkedList<ColumnValues> getColumnValues() throws PintoSyntaxException {
		if(getArgs().length < 1) {
			throw new IllegalArgumentException("exec requires one argument.");
		}
		LinkedList<ColumnValues> output = new LinkedList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(getArgs()[0]))) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	for(TerminalFunction tf : pinto.execute(line)) {
		    		output.addAll(tf.getColumnValues());
		    	}
		    }
			
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Cannot find pinto file \"" + getArgs()[0] + "\" to execute.");
		} catch (IOException e1) {
			throw new IllegalArgumentException("IO error for pinto file \"" + getArgs()[0] + "\" in execute.");
		} catch (PintoSyntaxException e) {
			throw new IllegalArgumentException("Pinto syntax error in  file \"" + getArgs()[0] + "\".", e);
		} catch (Exception e) {
			throw new IllegalArgumentException("Pinto syntax error in  file \"" + getArgs()[0] + "\".", e);
		}	
		return output;
	}

	public static FunctionHelp getHelp(String name) {
		return new FunctionHelp.Builder(name)
				.outputs("varies")
				.description("Executes pinto program defined in *filename*.")
				.parameter("filename")
				.build();
	}

	
	
	

}
