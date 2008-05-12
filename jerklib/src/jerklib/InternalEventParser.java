package jerklib;

import jerklib.events.IRCEvent;

public interface InternalEventParser
{
	public IRCEvent receiveEvent(IRCEvent e);
}
