package jerklib;

import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.TaskImpl;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BogusTest
{
	private MockConnectionManager conMan;
	private Session session;
	private int all = 0;
	private int other = 0;
	
	@BeforeTest
	public void setUp()
	{
		conMan = new MockConnectionManager();
		session = conMan.requestConnection
		(
			"kubrick.freenode.net", 
			6667, 
			new ProfileImpl("testnick" , "testnick" , "testnick" , "testnick"), 
			"/home/mohadib/testdata", 
			"/home/mohadib/TEST_OUTPUT"
		);
		
		session.onEvent(new TaskImpl("all")
		{
			public void receiveEvent(IRCEvent e)
			{
				all++;
			}
		});

		
		session.addIRCEventListener(new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				other++;
			}
		});
		
		session.addChannel(new Channel("#jerklib-test" , session));
	}
	
	
	@Test
	public void testSpeed()
	{
		long current = System.currentTimeMillis();
		for(int i = 0; i < 10000; i++)
		{
			conMan.start(session);
		}
		System.out.println(other +":"+ all + " " + (System.currentTimeMillis() - current)/1000l);
	}
	
	
	
}
