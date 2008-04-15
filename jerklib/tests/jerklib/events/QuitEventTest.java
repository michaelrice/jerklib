package jerklib.events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import jerklib.EventTestBase;
import jerklib.tasks.TaskImpl;
import static jerklib.events.IRCEvent.Type.*;
import static org.testng.AssertJUnit.assertTrue;

public class QuitEventTest extends EventTestBase
{
	private List<QuitEvent>events = new ArrayList<QuitEvent>();
	
	public QuitEventTest()
	{
		super("/quit.data" , System.getProperty("user.home") + File.separator + "jerklib.tests.user.ouput");
	}
	
	@BeforeTest
	public void init()
	{
		createSession();
		addServerInfo(ServerInfo.ALL);
		for(String name : new String[]{"#perkosa","#tvtorrents","#ubuntu","#cod4.wars","#jerklib","#testing","#test" , "#foooo"})
		{
			addChannel(name);
		}
		
		session.onEvent(new TaskImpl("quit")
		{
			public void receiveEvent(IRCEvent e)
			{
				events.add((QuitEvent)e);
			}
		} , QUIT);
		
		conMan.start(session);
	}
	
	
	@Test
	public void testUnrealQuit()
	{
		
		QuitEvent qe = (QuitEvent)events.get(0);
		assertTrue(qe.getWho().equals("Housefly7k"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Broken pipe"));
		assertTrue(qe.getHostName().equals("nix-86A86BB1.mc.videotron.ca"));
		assertTrue(qe.getUserName().equals("~Housefly7"));
		
		qe = (QuitEvent)events.get(1);
		assertTrue(qe.getWho().equals("Ceogar"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Quit: "));
		assertTrue(qe.getHostName().equals("nix-2ED5FBFB.telia.com"));
		assertTrue(qe.getUserName().equals("~GG-Ceogar"));
		
	}
	
	@Test
	public void testHyperionQuit()
	{
		QuitEvent qe = (QuitEvent)events.get(9);
		assertTrue(qe.getWho().equals("ironeye"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Client Quit"));
		assertTrue(qe.getHostName().equals("88-111-113-67.dynamic.dsl.as9105.com"));
		assertTrue(qe.getUserName().equals("n=ironeye"));
		
		qe = (QuitEvent)events.get(14);
		assertTrue(qe.getWho().equals("Faithful"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Read error: 113 (No route to host)"));
		assertTrue(qe.getHostName().equals("ns.linuxterminal.com"));
		assertTrue(qe.getUserName().equals("n=Faithful"));
		
	}
	
	
	@Test
	public void testNumEventsDispatched()
	{
		assertTrue(events.size() == 4012);
	}
	
}
