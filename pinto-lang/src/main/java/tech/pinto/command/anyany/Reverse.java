package tech.pinto.command.anyany;

import tech.pinto.command.Command;
import tech.pinto.command.ParameterizedCommand;
import tech.pinto.data.AnyData;
import tech.pinto.data.Data;
import tech.pinto.time.Period;
import tech.pinto.time.PeriodicRange;

public class Reverse extends ParameterizedCommand {

	public Reverse(String...args) {
		super("rev", AnyData.class, AnyData.class, args);
		inputCount = args.length == 0 ? Integer.MAX_VALUE :
			Integer.parseInt(args[0]) == -1 ? Integer.MAX_VALUE : Integer.parseInt(args[0]);
		outputCount = inputCount;
	}
	
	@Override
	protected void determineOutputCount() {
		outputCount = inputStack.size();
	}

	@Override public Command getReference() {
		return inputStack.removeLast();
	}

	@Override
	public <P extends Period> Data<?> evaluate(PeriodicRange<P> range) {
		// never gets called bc it passes on references to inputs
		throw new UnsupportedOperationException();
	}

}
