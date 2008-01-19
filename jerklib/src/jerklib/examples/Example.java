package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.ServerVersionEvent;
import jerklib.events.listeners.IRCEventListener;


public class Example implements IRCEventListener
{
	private ConnectionManager manager;
	
	public Example()
	{
		
		manager = new ConnectionManager(new ProfileImpl("scripy" , "scripy" , "scripy1" , "scrippy2"));
		
		final Session session = manager.requestConnection("lug.boulder.co.us");
		
		session.addIRCEventListener(this);
		session.setRejoinOnKick(true);
		session.setRejoinOnReconnect(true);

        /*
         * Give your client a chance to gracefully exit.
         * It your responsibility to add this as it
         * is not included in the library itself.
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
	}

	public void recieveEvent(IRCEvent e)
	{
		if(e.getType() == IRCEvent.Type.DEFAULT)
		{
			System.err.println(e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.READY_TO_JOIN)
		{
			e.getSession().joinChannel("#test");
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
		else if(e.getType() == IRCEvent.Type.QUIT)
		{
			System.out.println(e.getRawEventData());
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
