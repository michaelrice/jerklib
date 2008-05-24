package jerklib.parsers;

import jerklib.EventToken;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickInUseEvent;

public class NickInUseParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new NickInUseEvent
		(
				token.arg(1),
				token.data(), 
				event.getSession()
		); 
	}
}
