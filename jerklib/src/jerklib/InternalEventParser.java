package jerklib;

import jerklib.events.IRCEvent;

public interface InternalEventParser
{
	void parseEvent(IRCEvent event);
}
