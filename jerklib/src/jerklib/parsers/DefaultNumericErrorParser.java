package jerklib.parsers;

import jerklib.events.IRCEvent;
import jerklib.events.impl.NumericEventImpl;
import jerklib.tokens.EventToken;

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
