package jerklib.tasks;

import jerklib.events.listeners.IRCEventListener;

/**
 * The interface used for task actions in JerkLib.
 * A task will be notified of the IRCEvent type it is
 * registered for. If no event type is registerd for this
 * action it will be notified of all IRCEvents.
 * 
 * Tasks will be notified of events until the
 * cancel() method is called. This task will not
 * run again after cancel() is called. If the task
 * is already running , it will finish , then never
 * run again.
 * 
 * example:
 * <pre>
 *  session.onEvent(new TaskImpl()
 *  {
 *    public void recieveEvent(IRCEvent e)
 *		{
 *			ChannelMsgEvent cme = (ChannelMsgEvent)e;
 *			System.out.println("<" + cme.getNick() + ">" + cme.getMessage());
 *		}
 *	} , Type.CHANNEL_MESSAGE);
 * </pre>
 * 
 * That will add a task to be notified of only CHANNEL_MESSAGE events.
 * 
 * @author mohadib
 *
 */
public interface Task extends IRCEventListener
{
	/**
	 * Causes task to no longer be notified of IRCEvents.
	 * If task is already running , it will finish and 
	 * never run again.
	 */
	public void cancel();
	
	/**
	 * @return true if cancel() has been called else false
	 */
	public boolean isCanceled();
}
