package jerklib;

import java.io.File;

import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;

import junit.framework.TestCase;


public class EventTesting extends TestCase
{
	private Session session;
	MockConnectionManager man;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		File data = new File("/home/mohadib/TESTRESULTS");
		man = new MockConnectionManager(data ,"kubrick.freenode.net");
		session = man.getSession();
	}
	
	public void testEventStuff()
	{
		session.addIRCEventListener(new Listener());
		man.start();
	}
	
	
	public class Listener implements IRCEventListener
	{
		public void receiveEvent(IRCEvent e)
		{
			System.out.println(e.getType() + " " + e.getRawEventData());
		}
	}
}
