package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.QuitEvent;
import jerklib.events.listeners.IRCEventListener;

/**
 * @author mohadib
 * A simple example that demonstrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
	private ConnectionManager manager;

	/**
	 * A simple example that demonstrates how to use JerkLib
	 */
	public Example()
	{
		/* ConnectionManager takes a Profile to use for new connections. The profile
		 * will contain the users nick , real name , alt. nick 1 and. alt nick 2	
		 */
		manager = new ConnectionManager(new ProfileImpl("scripy", "scripy", "scripy1", "scrippy2"));

		/*
		 * One instance of ConnectionManager can connect to many IRC networks.
		 * ConnectionManager#requestConnection(String) will return a Session object.
		 * The Session is the main way users will interact with this library and 
		 * IRC networks
		 */
		final Session session = manager.requestConnection("irc.freenode.net");

		/* JerkLib fires IRCEvents to notify users of the lib of incoming events
		 * from a connected IRC server.
		 */
		session.addIRCEventListener(this);
		
		/*
		 * Tells JerkLib to rejoin any channel kicked from
		 */
		session.setRejoinOnKick(true);
		
		/*
		 * Tells jerklib to rejoin any channels previously joined to
		 * in event of a reconnect.
		 */
		session.setRejoinOnReconnect(true);

		/*
		 * Gives JerkLib a chance to gracefully exit in event of a kill signal. 
		 */
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("Hook Called");
				session.close("Bye");
			}
		});
		
		/*
		 * Prints out JerkLib version
		 */
		System.out.println(ConnectionManager.getVersion());
	}

	
	/*
	 * This method is for implementing  IRCEventListener.
	 * This method will be called anytime Jerklib parses and
	 * event from an IRC server
	 */
	public void recieveEvent(IRCEvent e)
	{
		/* default event - not reconized or ignored by jerklib */
		if(e.getType() == IRCEvent.Type.DEFAULT)
		{
			/* raw data is the raw text message received from an IRC server */
			System.err.println(e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.READY_TO_JOIN)
		{
			/* whois someone */
			e.getSession().whois("mohadib");
			
			/* join a channel */
			e.getSession().joinChannel("#jerklib");
		}
		else if(e.getType() == IRCEvent.Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent)e;
			if(jce.getChannel().equals("#jerklib"))
			{
				/* say hello and version number */
				jce.getChannel().say("Hello from Jerklib " + ConnectionManager.getVersion());
			}
		}
		else if(e.getType() == IRCEvent.Type.CHANNEL_MESSAGE)
		{
			/* some speaks in a channel */
			ChannelMsgEvent cme = (ChannelMsgEvent)e;
			System.out.println("<" + cme.getNick() + ">" + cme.getMessage());
		}
		else if(e.getType() == IRCEvent.Type.ERROR)
		{
			/* some error occured */
			NumericErrorEvent ne = (NumericErrorEvent) e;
			System.out.println(ne.getErrorType() + " " + ne.getNumeric() + " " + ne.getErrorMsg());
		}
		else if(e.getType() == IRCEvent.Type.QUIT)
		{
			/* someone quit */
			QuitEvent qe = (QuitEvent)e;
			System.out.println("User quit:" + qe.getWho() + ":" + qe.getQuitMessage());
		}
	}

	public static void main(String[] args)
	{
		new Example();
	}

}
