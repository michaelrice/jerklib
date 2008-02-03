package jerklib.tasks;

import jerklib.events.listeners.IRCEventListener;

public interface Task extends IRCEventListener
{
	public void cancel();
	public boolean isCanceled();
}
