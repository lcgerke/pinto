package pinto.command.terminal;

import java.util.ArrayDeque;

import pinto.Cache;
import pinto.command.ParameterizedCommand;
import pinto.data.AnyData;
import pinto.data.MessageData;
import pinto.time.Period;
import pinto.time.PeriodicRange;

public class Delete extends ParameterizedCommand<Object, AnyData, String, MessageData> {

	private final String code;
	private final Cache cache;
	
	public Delete(Cache cache, String[] arguments) {
		super("save", AnyData.class, MessageData.class, arguments);
		if(arguments.length < 1) {
			throw new IllegalArgumentException("del requires one argument.");
		}
		code = arguments[0];
		this.cache = cache;
		inputCount = Integer.MAX_VALUE;
	}
	

	@Override
	protected <P extends Period> ArrayDeque<MessageData> evaluate(PeriodicRange<P> range) {
		cache.deleteSaved(code);
		ArrayDeque<MessageData> out = new ArrayDeque<>();
		out.addFirst(new MessageData("Successfully saved."));
		return out;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}
	
	
	

}
