package jerklib.tasks;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;


/**
 * This is a convenience implementation of the Task interface.
 * To use this class either pass in an IRCEventListener to do
 * the work or use the no arg constructor and override receiveEvent
 * 
 * @author mohadib
 *@see Task
 *@see Session#onEvent(Task)
 *@see Session#onEvent(Task, jerklib.events.IRCEvent.Type)
 */
public class TaskImpl implements Task
{
	private IRCEventListener l;
	private boolean cancel;
	
	
	/**
	 *receiveEvent must be overriden to use this ctor 
	 */
	public TaskImpl(){}
	
	/**
	 * @param l IRCEventListener to do the "work"
	 */
	public TaskImpl(IRCEventListener l)
	{
		this.l = l;
	}
	
	/* (non-Javadoc)
	 * @see jerklib.tasks.Task#cancel()
	 */
	@Override
	public void cancel()
	{
		cancel = true;
	}

	/* (non-Javadoc)
	 * @see jerklib.tasks.Task#isCanceled()
	 */
	@Override
	public boolean isCanceled()
	{
		return cancel;
	}

	/* (non-Javadoc)
	 * @see jerklib.events.listeners.IRCEventListener#recieveEvent(jerklib.events.IRCEvent)
	 */
	@Override
	public void recieveEvent(IRCEvent e)
	{
		l.recieveEvent(e);
	}
}
