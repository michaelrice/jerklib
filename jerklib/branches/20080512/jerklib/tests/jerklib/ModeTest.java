package jerklib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jerklib.events.IRCEvent;
import jerklib.events.ModeEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;

public class ModeTest
{

	private MockConnectionManager conMan;
	private Session session;
	private List<ModeEvent> events = new ArrayList<ModeEvent>();
	
	public void setUp()
	{
		conMan = new MockConnectionManager();
		session = conMan.requestConnection
		(
			"kubrick.freenode.net", 
			6667, 
			new ProfileImpl("testnick" , "testnick" , "testnick" , "testnick"), 
			"/home/mohadib/mode_msg_data", 
			"/home/mohadib/TEST_OUTPUT"
		);
		
		session.onEvent(new TaskImpl("mode")
		{
			public void receiveEvent(IRCEvent e)
			{
				events.add((ModeEvent)e);
			}
		} , Type.MODE_EVENT);
		
		session.addChannel(new Channel("#jerklib" , session));
		session.addChannel(new Channel("#ubuntu" , session));
		session.addChannel(new Channel("#freenode" , session));
		session.getServerInformation().parseServerInfo(":kubrick.freenode.net 005 mohadib__ IRCD=dancer CAPAB CHANTYPES=# EXCEPTS INVEX CHANMODES=bdeIq,k,lfJD,cgijLmnPQrRstz CHANLIMIT=#:20 PREFIX=(ov)@+ MAXLIST=bdeI:50 MODES=4 STATUSMSG=@ KNOCK NICKLEN=16 :are supported by this server");
		
		conMan.start(session);
	}
	
	
	public void testAmountDispatched()
	{
		assertTrue(String.valueOf(events.size()) ,events.size() == 27);
	}
	
	
	public void testUserModes()
	{
		
		ModeEvent me = events.get(0);
		Map<String, List<String>> modeMap = me.getModeMap();
		assertTrue(me.getChannel() == null);
		assertTrue(me.getSession().equals(session));
		assertTrue(me.setBy().equals("services"));
		assertTrue(modeMap.size() == 1);
		assertTrue(modeMap.containsKey("+e"));
		assertTrue(modeMap.get("+e").size() == 1);
		assertTrue(modeMap.get("+e").get(0).equals("testnick"));
	}
	
	
	public void testChannelModes()
	{
		ModeEvent me = events.get(2);
		Map<String, List<String>> modeMap = me.getModeMap();
		assertTrue(me.getChannel().getName().equals("#jerklib"));
		assertTrue(me.getSession().equals(session));
		assertTrue(me.setBy().equals("mohadib"));
		assertTrue(modeMap.size() == 2);
		assertTrue(modeMap.containsKey("+v"));
		assertTrue(modeMap.containsKey("+o"));
		
		List<String> vNicks = modeMap.get("+v");
		assertTrue(vNicks.size() == 3);
		assertTrue(vNicks.contains("staykov"));
		assertTrue(vNicks.contains("r0bby"));
		assertTrue(vNicks.contains("jottinger"));
		assertTrue(!vNicks.contains("mr_ank"));
		
		List<String> oNicks = modeMap.get("+o");
		assertTrue(oNicks.size() == 1);
		assertTrue(oNicks.contains("mr_ank"));
	}
	
	
	
	
	
	
}
