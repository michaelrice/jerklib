package jerklib.parsers;

import jerklib.EventToken;
import jerklib.events.IRCEvent;
import jerklib.events.NumericErrorEvent;

public class NumericErrorParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new NumericErrorEvent
		(
				token.arg(0), 
				token.getRawEventData(), 
				event.getSession()
		); 
	}
}
