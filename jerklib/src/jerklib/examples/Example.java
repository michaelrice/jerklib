package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.WhoisEvent;
import jerklib.events.WhowasEvent;
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
			e.getSession().joinChannel("#jerklib" , "letmein");
		}
		else if(e.getType() == IRCEvent.Type.JOIN_COMPLETE)
		{
			j++;
			if(j == 1)
			{
				e.getSession().whois("mohadibggg");
				e.getSession().whowas("honda");
			}
		}
		else if(e.getType() == IRCEvent.Type.WHOWAS_EVENT)
		{
			WhowasEvent we = (WhowasEvent)e;
			System.out.println("WHOWAS " + we.getNick() + " " + we.getUserName() + " " + we.getRealName() + " " + we.getHostName());
		}
		else if(e.getType() == IRCEvent.Type.JOIN)
		{
			JoinEvent je = (JoinEvent)e;
			//je.getChannel().say(je.getWho() + " PLEASE TO BE WRITING MEH DOCS NOW! KTHKXBYE");
			if(je.getWho().toLowerCase().matches("r0bby.*"))
			{
				je.getChannel().say("GAYS ON MY INTERNETS?? :(");
			}
		}
		else if(e.getType() == IRCEvent.Type.WHOIS_EVENT)
		{
			WhoisEvent we = (WhoisEvent)e;
			System.out.println("GOT WHOIS EVENT " + we.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.ERROR)
		{
			NumericErrorEvent ne = (NumericErrorEvent)e;
			System.out.println(ne.getErrorType() + " " + ne.getNumeric() + " " + ne.getErrorMsg());
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
