package jerklib.parsers;

import jerklib.events.IRCEvent;
import jerklib.events.impl.NickInUseEventImpl;
import jerklib.tokens.EventToken;

public class NickInUseParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new NickInUseEventImpl
		(
				token.getArguments().get(1),
				token.getData(), 
				event.getSession()
		); 
	}
}
