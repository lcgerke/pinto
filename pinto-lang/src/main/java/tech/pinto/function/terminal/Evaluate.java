package tech.pinto.function.terminal;

import java.time.LocalDate;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import tech.pinto.function.FunctionHelp;
import tech.pinto.Indexer;
import tech.pinto.Namespace;
import tech.pinto.PintoSyntaxException;
import tech.pinto.Column;
import tech.pinto.ColumnValues;
import tech.pinto.function.ComposableFunction;
import tech.pinto.function.TerminalFunction;
import tech.pinto.time.PeriodicRange;
import tech.pinto.time.Periodicities;
import tech.pinto.time.Periodicity;

public class Evaluate extends TerminalFunction {

	private LinkedList<Column> columns;
	private PeriodicRange<?> range;
	
	public Evaluate(String name, Namespace namespace, ComposableFunction previousFunction, Indexer indexer) {
		super(name, namespace, previousFunction, indexer);
	}

	@Override
	public LinkedList<ColumnValues> getColumnValues() throws PintoSyntaxException {
		if(columns == null) {
			columns = compose();
		}
		return columns.stream().map(f -> f.getValues(range))
					.collect(Collectors.toCollection(() -> new LinkedList<>()));
	}

	@Override
	public Optional<PeriodicRange<?>> getRange() throws PintoSyntaxException {
		if(columns == null) {
			columns = compose();
		}
		return Optional.of(range);
	}
	
	@Override
	protected LinkedList<Column> compose(LinkedList<Column> stack) {
		Periodicity<?> p =  Periodicities.get(getArgs().length > 2 ? getArgs()[2] : "B");
		LocalDate start = getArgs().length > 0 && !getArgs()[0].equals("") ? LocalDate.parse(getArgs()[0]) : 
						p.from(LocalDate.now()).endDate();
		LocalDate end = getArgs().length > 1 ? LocalDate.parse(getArgs()[1]) : 
						p.from(LocalDate.now()).endDate();
		range = p.range(start, end, false);
		return stack;
	}
	
	public static FunctionHelp getHelp(String name) {
		return  new FunctionHelp.Builder(name)
				.outputs("n")
				.description("Evaluates the preceding commands over the given date range.")
				.parameter("start date", "prior period", "yyyy-dd-mm")
				.parameter("end date", "prior period", "yyyy-dd-mm")
				.parameter("periodicity", "B", "{B,W-FRI,BM,BQ,BA}")
				.build();
	}

}
