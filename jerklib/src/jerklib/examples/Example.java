package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ModeEvent;
import jerklib.events.TopicEvent;
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
			//e.getSession().whois("mohadib");
			e.getSession().joinChannel("#jerklib");	
			/*e.getSession().joinChannel("#ubuntu");
			e.getSession().joinChannel("#debian");
			e.getSession().joinChannel("#perl");*/
		}
		else if(e.getType() == IRCEvent.Type.JOIN_COMPLETE)
		{
		}
		else if(e.getType() == IRCEvent.Type.TOPIC)
		{
			TopicEvent te = (TopicEvent)e;
			System.out.println("Topic for " + te.getChannel().getName() + " " + te.getTopic() + " set by " + te.getSetBy() + " set when:" + te.getSetWhen());
		}
		else if(e.getType() == IRCEvent.Type.CHANNEL_MESSAGE)
		{
			System.out.println("Good " +e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.PRIVATE_MESSAGE)
		{
			System.out.println("Good Prv " +e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.MODE_EVENT)
		{
			ModeEvent me = (ModeEvent)e;
			if(me.getUser().equals(e.getSession().getNick()) && me.getMode().equalsIgnoreCase("+o"))
			{
				me.getChannel().setTopic("A WINNAR IS ME");
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
