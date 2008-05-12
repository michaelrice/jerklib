package jerklib.parsers;

import jerklib.EventToken;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NumericEventImpl;

public class DefaultNumericErrorParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new NumericEventImpl
		(
				token.concatTokens(7), 
				token.getData(), 
				Integer.parseInt(token.getCommand()), 
				event.getSession()
		); 
	}
}
