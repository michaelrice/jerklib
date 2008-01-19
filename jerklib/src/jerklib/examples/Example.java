package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.WhoisEvent;
import jerklib.events.listeners.IRCEventListener;


public class Example implements IRCEventListener
{
	ConnectionManager manager;
	int j = 0;
	
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
			e.getSession().joinChannel("#jerklib");
		}
		else if(e.getType() == IRCEvent.Type.JOIN_COMPLETE)
		{
			j++;
			if(j == 1)
			{
				e.getSession().whois("mohadibggg");
			}
		}
		else if(e.getType() == IRCEvent.Type.JOIN)
		{
			//JoinEvent je = (JoinEvent)e;
			//je.getChannel().say(je.getWho() + " PLEASE TO BE WRITING MEH DOCS NOW! KTHKXBYE");
		}
		else if(e.getType() == IRCEvent.Type.WHOIS_EVENT)
		{
			WhoisEvent we = (WhoisEvent)e;
			System.out.println("GOT WHOIS EVENT " + we.getRawEventData());
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
