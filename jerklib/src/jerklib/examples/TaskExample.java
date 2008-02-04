package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;

public class TaskExample
{
	private ConnectionManager manager;
	
	public TaskExample()
	{
		/* ConnectionManager takes a Profile to use for new connections. The profile
		 * will contain the users  real name , nick , alt. nick 1 and alt. nick 2	*/
		manager = new ConnectionManager(new ProfileImpl("scripy", "scriptask", "scripy1asd", "scrippy2asd"));

		/* One instance of ConnectionManager can connect to many IRC networks.
		 * ConnectionManager#requestConnection(String) will return a Session object.
		 * The Session is the main way users will interact with this library. */
		Session session = manager.requestConnection("irc.freenode.net");
		
		/*
		 * Adds a task to the Session to only be notified of ChannelMessage events
		 */
		session.onEvent(new TaskImpl()
		{
			public void receiveEvent(IRCEvent e)
			{
				ChannelMsgEvent cme = (ChannelMsgEvent)e;
				System.out.println("<" + cme.getNick() + ">" + cme.getMessage());
			}
		} , Type.CHANNEL_MESSAGE);
		
		/*
		 * Adds a task to the session to be notified of the ConnectionComplete event
		 */
		session.onEvent(new TaskImpl()
		{
			public void receiveEvent(IRCEvent e)
			{
				e.getSession().joinChannel("#jerklib");
			}
		} , Type.CONNECT_COMPLETE);
		
		/*
		 * Adds a task to the session to be notifed of all events
		 */
		session.onEvent(new TaskImpl()
		{
			@Override
			public void receiveEvent(IRCEvent e)
			{
				System.out.println("An event was fired!");
			}
		});
	}
	
	public static void main(String[] args)
	{
		new TaskExample();
	}
}
