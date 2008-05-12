package jerklib;

import jerklib.events.listeners.IRCEventListener;

public interface InternalEventParser extends IRCEventListener
{
	public void setInternalEventHandler(IRCEventListener listener);
	
	public IRCEventListener getInternalEventHandler();
}
