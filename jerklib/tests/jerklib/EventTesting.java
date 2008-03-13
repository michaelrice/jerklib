package jerklib;

import java.io.File;

import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.TaskImpl;

import junit.framework.TestCase;


public class EventTesting extends TestCase
{
	private Session session;
	MockConnectionManager man;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		man = new MockConnectionManager("kubrick.freenode.net");
		man.parse(new File("/home/mohadib/TESTRESULTS"));
		session = man.getSession();
	}
	
	
	int allEvents = 0;
	public void testEventStuff()
	{
		long time = System.currentTimeMillis();
		session.addIRCEventListener(new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				allEvents++;
			}
		});
		for(int i = 0 ; i <10000; i++)
		{
			man.start();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Sent 590,000 events - events relayed and parsed: " + allEvents + " seconds elapsed :" + time/1000);
		assertTrue(allEvents == 590000);
	}

	int conComplete = 0;
	public void testTaskFiltering()
	{
		long time = System.currentTimeMillis();
		session.onEvent(new TaskImpl("con_complete")
		{
			public void receiveEvent(IRCEvent e)
			{
				conComplete++;
			}
		} , Type.CONNECT_COMPLETE);
		
		for(int i = 0 ; i <10000; i++)
		{
			man.start();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Sent 590,000 events - 10,000 of wich are ConnectionCompletes  - ConComplete received: " + conComplete + " seconds elapsed :" + time/1000);
		assertTrue(conComplete == 10000);
		
	}
	
	
	int events = 0;
	public void testNonFilteredTasks()
	{
		long time = System.currentTimeMillis();
		session.onEvent(new TaskImpl("all")
		{
			public void receiveEvent(IRCEvent e)
			{
				events++;
			}
		});
		
		for(int i = 0 ; i <10000; i++)
		{
			man.start();
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Sent 590,000 events - ALL_EVENTS Task events received: " + events + " seconds elapsed :" + time/1000);
		assertTrue(events == 590000);
		
	}
	
}
