package jerklib;

import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.MessageEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertTrue;

public class ChannelMessageTest
{
	private MockConnectionManager conMan;
	
	@BeforeTest
	public void setUp()
	{
		conMan = new MockConnectionManager();
	}
	
	
	int pmReceived = 0;
	@Test
	public void testChannelMsgParsing()
	{
		final Session session = conMan.requestConnection
		(
			"kubrick.freenode.net", 
			6667, 
			new ProfileImpl("testnick" , "testnick" , "testnick" , "testnick"), 
			"/home/mohadib/privmsgdata", 
			"/home/mohadib/TEST_OUTPUT"
		);
		
		session.addChannel(new Channel("#tvtorrents" , session));
		
		
		session.addIRCEventListener(new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				pmReceived++;
				assertTrue(e.getType() == Type.CHANNEL_MESSAGE);
				
				MessageEvent me = (MessageEvent)e;
				assertTrue(me.getChannel().getName().equals("#tvtorrents"));
				
				switch(pmReceived)
				{
					case 1: 
					{
						assertTrue(me.getHostName().equals("nix-555C426C.cust.blixtvik.net"));
						assertTrue(me.getUserName().equals("~PircBot"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("TVTorrentsBot"));
						assertTrue(me.getMessage().equals("<sammyp123> o yeh"));
						break;
					}
					case 2:
					{
						assertTrue(me.getHostName().equals("nix-555C426C.cust.blixtvik.net"));
						assertTrue(me.getUserName().equals("~PircBot"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("TVTorrentsBot"));
						assertTrue(me.getMessage().equals("<sammyp123> so if i have say episodes of lost, i have got of the stage 6 site how do i seed them or attach the tracker to let it seed"));
						break;
					}
				}
			}
		});
		
		conMan.start(session);
		assertTrue(pmReceived == 6);
		
	}
}
