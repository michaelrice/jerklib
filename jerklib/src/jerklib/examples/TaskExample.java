package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;

public class TaskExample
{
	public TaskExample()
	{
		ConnectionManager conman = new ConnectionManager(new Profile("scripy"));
		Session session = conman.requestConnection("irc.freenode.net");
		
		/* Add a Task to join a channel when the connection is complete 
		   This task will only ever be notified of ConnectionCompleteEvents */
		session.onEvent(new TaskImpl("join_channels")
		{
			public void receiveEvent(IRCEvent e)
			{
				e.getSession().join("#jerklib");
			}
		}, Type.CONNECT_COMPLETE);
		
		/* Add a Task to say hello */
		session.onEvent(new TaskImpl("hello")
		{
			public void receiveEvent(IRCEvent e)
			{
				JoinCompleteEvent jce = (JoinCompleteEvent)e;
				jce.getChannel().say("Hello from JerkLib!");
			}
		}, Type.JOIN_COMPLETE);
		
		
		/* Add a Task that will be notified of all events */
		session.onEvent(new TaskImpl("print")
		{
			public void receiveEvent(IRCEvent e)
			{
				System.out.println(e.getRawEventData());
			}
		});
	}
	
	public static void main(String[] args)
	{
		new TaskExample();
	}
	
}
