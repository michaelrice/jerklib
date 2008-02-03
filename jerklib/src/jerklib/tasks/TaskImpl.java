package jerklib.tasks;

import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;

public class TaskImpl implements Task
{
	private final IRCEventListener l;
	private boolean cancel;
	
	public TaskImpl(IRCEventListener l)
	{
		this.l = l;
	}
	
	@Override
	public void cancel()
	{
		cancel = true;
	}

	@Override
	public boolean isCanceled()
	{
		return cancel;
	}

	@Override
	public void recieveEvent(IRCEvent e)
	{
		l.recieveEvent(e);
	}
}
