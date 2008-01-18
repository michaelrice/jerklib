package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.ChannelListEvent;
import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;


public class Example implements IRCEventListener
{
	ConnectionManager manager;
	
	public Example()
	{
		
		manager = new ConnectionManager(new ProfileImpl("scripy" , "scripy" , "scripy1" , "scrippy2"));
		
		Session session = manager.requestConnection("irc.freenode.net");
		
		session.addIRCEventListener(this);
		session.setRejoinOnKick(true);
		session.setRejoinOnReconnect(true);
	}

	public void recieveEvent(IRCEvent e)
	{
		if(e.getType() == IRCEvent.Type.DEFAULT)
		{
			System.err.println(e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.READY_TO_JOIN)
		{
			e.getSession().channelList();
		}
		else if(e.getType() == IRCEvent.Type.CHANNEL_LIST_EVENT)
		{
			ChannelListEvent cle = (ChannelListEvent)e;
			System.out.println(cle.getChannelName() + " " + cle.getTopic());
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
