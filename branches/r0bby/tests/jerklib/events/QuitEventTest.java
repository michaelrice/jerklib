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
				QuitEvent qe = (QuitEvent)e;
				if(qe.getQuitMessage().indexOf(":") != -1)
				{
					System.err.println(qe.getQuitMessage());
				}
				events.add(qe);
			}
		} , QUIT);
		
		conMan.start(session);
	}
	

	
	@Test
	public void testUnrealQuit()
	{
		
		QuitEvent qe = (QuitEvent)events.get(0);
		assertTrue(qe.getNick().equals("Housefly7k"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Broken pipe"));
		assertTrue(qe.getHostName().equals("nix-86A86BB1.mc.videotron.ca"));
		assertTrue(qe.getUserName().equals("~Housefly7"));
		
		qe = (QuitEvent)events.get(1);
		assertTrue(qe.getNick().equals("Ceogar"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Quit: "));
		assertTrue(qe.getHostName().equals("nix-2ED5FBFB.telia.com"));
		assertTrue(qe.getUserName().equals("~GG-Ceogar"));
		
	}
	
	@Test
	public void testHyperionQuit()
	{
		QuitEvent qe = (QuitEvent)events.get(9);
		assertTrue(qe.getNick().equals("ironeye"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Client Quit"));
		assertTrue(qe.getHostName().equals("88-111-113-67.dynamic.dsl.as9105.com"));
		assertTrue(qe.getUserName().equals("n=ironeye"));
		
		qe = (QuitEvent)events.get(14);
		assertTrue(qe.getNick().equals("Faithful"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Read error: 113 (No route to host)"));
		assertTrue(qe.getHostName().equals("ns.linuxterminal.com"));
		assertTrue(qe.getUserName().equals("n=Faithful"));
		
	}
	
	@Test 
	public void testSnircdQuit()
	{
		QuitEvent qe = (QuitEvent)events.get(15);
		assertTrue(qe.getNick().equals("HELLBOY"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Ping timeout"));
		assertTrue(qe.getHostName().equals("90-227-48-98-no88.tbcn.telia.com"));
		assertTrue(qe.getUserName().equals("~fa"));
		
		qe = (QuitEvent)events.get(21);
		assertTrue(qe.getNick().equals("popsiE"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Ping timeout"));
		assertTrue(qe.getHostName().equals("c83-253-95-204.bredband.comhem.se"));
		assertTrue(qe.getUserName().equals("~Tu21925"));
	}
	
	@Test 
	public void testBahamutQuit()
	{
		QuitEvent qe = (QuitEvent)events.get(88);
		assertTrue(qe.getNick().equals("SeAs"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Read error: Operation timed out"));
		assertTrue(qe.getHostName().equals("202.152.170.240"));
		assertTrue(qe.getUserName().equals("~mincia"));
		
		qe = (QuitEvent)events.get(89);
		assertTrue(qe.getNick().equals("Delanose"));
		assertTrue(qe.getQuitMessage() , qe.getQuitMessage().equals("Quit: huehuehuehu sepinya sekarang"));
		assertTrue(qe.getHostName().equals("202.133.2.118"));
		assertTrue(qe.getUserName().equals("~Yes_I_Am"));
	}
	
	@Test
	public void testNumEventsDispatched()
	{
		assertTrue(events.size() == 4012);
	}
	
}
