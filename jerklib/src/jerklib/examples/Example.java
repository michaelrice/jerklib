package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.ServerVersionEvent;
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
			e.getSession().getServerVersion();
		}
		else if(e.getType() == IRCEvent.Type.SERVER_VERSION_EVENT)
		{
			ServerVersionEvent se = (ServerVersionEvent)e;
			System.out.println
			("VERSION " + se.getVersion() + " " + se.getHostName() + " " + se.getComment());
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
