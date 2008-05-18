package jerklib.parsers;

import jerklib.events.IRCEvent;
import jerklib.events.impl.MotdEventImpl;
import jerklib.tokens.EventToken;

public class MotdParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		return new MotdEventImpl
		(
			token.getData(), 
			event.getSession(), 
			token.getArguments().get(1), 
			token.getPrefix()
		);
	}
}
