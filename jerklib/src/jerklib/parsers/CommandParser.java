package jerklib.parsers;

import jerklib.events.IRCEvent;
import jerklib.tokens.EventToken;

public interface CommandParser
{
	public IRCEvent createEvent(EventToken token , IRCEvent event);
}
