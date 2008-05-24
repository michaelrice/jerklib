package jerklib.parsers;

import jerklib.EventToken;
import jerklib.events.IRCEvent;
import jerklib.events.MotdEvent;

public class MotdParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new MotdEvent
		(
			token.getRawEventData(), 
			event.getSession(), 
			token.arg(1), 
			token.prefix()
		);
	}
}
