package jerklib;

import jerklib.events.IRCEvent;
import jerklib.events.NickListEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertTrue;


/*
 * the data file for this test only has data from freenodes - hyperion-1.0.2b
 * need to run this with data from other ircds 
 * 
 */

public class NickCacheTest
{
	private MockConnectionManager conMan;
	private Session session;
	private NickListEvent nle;
	
	public void setUp()
	{
		conMan = new MockConnectionManager();
		session = conMan.requestConnection
		(
			"kubrick.freenode.net", 
			6667, 
			new ProfileImpl("testnick" , "testnick" , "testnick" , "testnick"), 
			"/home/mohadib/nickcachedata", 
			"/home/mohadib/TEST_OUTPUT"
		);
		
		session.onEvent(new TaskImpl("nick_list")
		{
			public void receiveEvent(IRCEvent e)
			{
				nle = (NickListEvent)e;
			}
		} , Type.NICK_LIST_EVENT);
		
		session.addChannel(new Channel("#ubuntu" , session));
		
		conMan.start(session);
	}
	
	public void testNickListEventGenerated()
	{
		assertTrue(nle != null);
	}
	
	public void testNickCountFromNickListEvent()
	{
		assertTrue(nle.getNicks().size() + "" , nle.getNicks().size() == 1225);
	}
	
	public void testNickCountAfterPartJoinsEtc()
	{
		int size = session.getChannel("#ubuntu").getNicks().size();
		assertTrue(size == 1224);
	}
	
	public void testContainsNick()
	{
		Channel chan = session.getChannel("#ubuntu");
		assertTrue(!chan.getNicks().contains("unstable"));
		assertTrue(chan.getNicks().contains("rosco"));
	}
	
	public void testNickCaseInsensitivity()
	{
		Channel chan = session.getChannel("#ubuntu");
		assertTrue(chan.getNicks().contains("Rosco"));
		assertTrue(chan.getNicks().contains("RoScO"));
		assertTrue(chan.getNicks().contains("ROSCO"));
		
		assertTrue(chan.getNicks().indexOf("Rosco") != -1);
		assertTrue(chan.getNicks().indexOf("RoScO") != -1);
		assertTrue(chan.getNicks().indexOf("ROSCO") != -1);
	}
	
	
}
