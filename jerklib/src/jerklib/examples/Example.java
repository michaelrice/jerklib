package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;

/**
 * @author mohadib
 * A simple example that demonsrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
	private ConnectionManager manager;

	public Example()
	{
		/* ConnectionManager takes a Profile to use for new connections. The profile
		 * will contain the users nick , real name , alt. nick 1 and. alt nick 2	
		 */
		manager = new ConnectionManager(new ProfileImpl("scripy", "scripyasas", "scripy1", "scrippy2"));

		/*
		 * One instance of ConnectionManager can connect to many IRC networks.
		 * ConnectionManager#requestConnection(String) will return a Session object.
		 * The Session is the main way users will interact with this library and 
		 * IRC networks
		 */
		Session session = manager.requestConnection("irc.freenode.net");

		/* JerkLib fires IRCEvents to notify users of the lib of incoming events
		 * from a connected IRC server.
		 */
		session.addIRCEventListener(this);
	}
	
	/*
	 * This method is for implementing an IRCEventListener.
	 * This method will be called anytime Jerklib parses an
	 * event from the Session its attached to. All events are 
	 * sent as IRCEvents. You can check its actually type and 
	 * cast it to a more specific type.
	 */
	public void receiveEvent(IRCEvent e)
	{
		if(e.getType() == Type.CHANNEL_MESSAGE)
		{
			/* someone speaks in a channel */
			MessageEvent cme = (MessageEvent)e;
			System.out.println(cme.getChannel().getName() + " <" + cme.getNick() + ">" + cme.getMessage());
        }
        //IF you want to catch both Channel and private messages
        if(e instanceof MessageEvent) {
            MessageEvent me = (MessageEvent)e;
            System.out.println(me.getChannel().getName() + " <" + me.getNick() + ">" + me.getMessage());
        }


        }
        else if(e.getType() == Type.CONNECT_COMPLETE)
		{
			/* connection to server is complete */
			e.getSession().joinChannel("#jerklib");
		}
		else if(e.getType() == Type.JOIN_COMPLETE)
		{
			JoinCompleteEvent jce = (JoinCompleteEvent)e;
			if(jce.getChannel().getName().equals("#jerklib"))
			{
				/* say hello and version number */
				jce.getChannel().say("Hello from Jerklib " + ConnectionManager.getVersion());
                jce.getChannel().notice("HAI 2u ");
                e.getSession().notice(jce.getChannel().getName(), "Hello from Jerklib "+ConnectionManager.getVersion());
            }
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
}
