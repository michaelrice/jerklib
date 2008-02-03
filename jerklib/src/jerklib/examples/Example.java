package jerklib.examples;

import java.util.HashMap;
import java.util.Map;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.QuitEvent;
import jerklib.events.InviteEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;

/**
 * @author mohadib
 * A simple example that demonsrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
	private ConnectionManager manager;
	private Map<Type, IrcRunnable> stratMap = new HashMap<Type, IrcRunnable>();
	/**
	 * A simple example that demonstrates how to use JerkLib
	 */
	public Example()
	{
		initStratMap();
		
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
				manager.quit("Bye");
			}
		});
		
	}

	
	/*
	 * This method is for implementing  IRCEventListener.
	 * This method will be called anytime Jerklib parses and
	 * event from an IRC server
	 */
	public void recieveEvent(IRCEvent e)
	{
		//using a strategy pattern to handle the events
		//http://en.wikipedia.org/wiki/Strategy_pattern
		IrcRunnable r = stratMap.get(e.getType());
		if(r!=null)
		{
			r.run(e);
		}
		else
		{
			System.out.println(e.getRawEventData());
		}
	}

	private void initStratMap()
	{

    stratMap.put(Type.INVITE_EVENT, new IrcRunnable()
    {
    	@Override
    	public void run(IRCEvent e)
    	{
    		/*invited to a channel */
    		InviteEvent event = (InviteEvent)e;
    		System.out.print(event.getNick()+"!"+event.getUserName()+"@");
    		System.out.print(event.getHostName()+" invited us to "+event.getChannelName()+"\n");
    	}
    });

    stratMap.put(Type.CHANNEL_MESSAGE, new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				/* someone speaks in a channel */
				ChannelMsgEvent cme = (ChannelMsgEvent)e;
                System.out.println("<" + cme.getNick() + ">" + cme.getMessage());
			}
		});
		
		stratMap.put(Type.DEFAULT, new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				/* raw data is the raw text message received from an IRC server */
				System.out.println(e.getRawEventData());
			}
		});
		
		stratMap.put(Type.READY_TO_JOIN, new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				/* whois someone */
				e.getSession().whois("mohadib");

        /* join a channel */
				e.getSession().joinChannel("#jerklib");
			}
		});
		
		stratMap.put(Type.JOIN_COMPLETE, new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				JoinCompleteEvent jce = (JoinCompleteEvent)e;
				if(jce.getChannel().getName().equals("#jerklib"))
				{
					/* say hello and version number */
					jce.getChannel().say("Hello from Jerklib " + ConnectionManager.getVersion());
				}
			}
		});
		
		stratMap.put(Type.ERROR ,new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				/* some error occured */
				NumericErrorEvent ne = (NumericErrorEvent) e;
				System.out.println(ne.getErrorType() + " " + ne.getNumeric() + " " + ne.getErrorMsg());
			}
		});
		
		stratMap.put(Type.QUIT ,new IrcRunnable()
		{
			@Override
			public void run(IRCEvent e)
			{
				/* someone quit */
				QuitEvent qe = (QuitEvent)e;
				System.out.println("User quit:" + qe.getWho() + ":" + qe.getQuitMessage());
			}
		});
		
    }
	
	private interface IrcRunnable             
	{
		public void run(IRCEvent e);
	}
	
	public static void main(String[] args)
	{
		new Example();
	}

}
