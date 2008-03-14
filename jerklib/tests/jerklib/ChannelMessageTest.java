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



/*
 * The data file for this test has data from
 * 
 * irc.outsiderz.com - Unreal3.2.7
 * irc.quakenet.org - u2.10.12.10+snircd(1.3.4)
 * undernet - 	u2.10.12.12 
 * irc.nixgeeks.com -  Unreal3.2.3
 * freenode -  hyperion-1.0.2b
 * 
 */

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
		session.addChannel(new Channel("#mp3passion" , session));
		session.addChannel(new Channel("#ubuntu" , session));
		session.addChannel(new Channel("#cod4.wars" , session));
		session.addChannel(new Channel("#cd+g" , session));
		
		
		session.addIRCEventListener(new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				pmReceived++;
				assertTrue(e.getType() == Type.CHANNEL_MESSAGE);
				
				MessageEvent me = (MessageEvent)e;
				
				switch(pmReceived)
				{
					case 1: 
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#tvtorrents"));
						assertTrue(me.getHostName().equals("nix-555C426C.cust.blixtvik.net"));
						assertTrue(me.getUserName().equals("~PircBot"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("TVTorrentsBot"));
						assertTrue(me.getMessage().equals("<sammyp123> o yeh"));
						break;
					}
					case 2:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#tvtorrents"));
						assertTrue(me.getHostName().equals("nix-555C426C.cust.blixtvik.net"));
						assertTrue(me.getUserName().equals("~PircBot"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("TVTorrentsBot"));
						assertTrue(me.getMessage().equals("<sammyp123> so if i have say episodes of lost, i have got of the stage 6 site how do i seed them or attach the tracker to let it seed"));
						break;
					}
					case 3:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#mp3passion"));
						assertTrue(me.getHostName().equals("bas9-montreal02-1096615543.dsl.bell.ca"));
						assertTrue(me.getUserName().equals("Eclipse"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("Arukko"));
						assertTrue(me.getMessage().equals("!afroman 07-santos-crazy_crazy_lovin.mp3  ::INFO:: 2.1MB"));
						break;
					}
					case 4:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#mp3passion"));
						assertTrue(me.getHostName().equals("bas9-montreal02-1096615543.dsl.bell.ca"));
						assertTrue(me.getUserName().equals("Eclipse"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("Arukko"));
						assertTrue(me.getMessage().equals("!afroman 08-santos-move_it.mp3  ::INFO:: 2.7MB"));
						break;
					}
					case 5:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#ubuntu"));
						assertTrue(me.getHostName().equals("mtl-pppoe-adsl729.securenet.net"));
						assertTrue(me.getUserName().equals("n=jean"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("Pelo"));
						assertTrue(me.getMessage().equals("gr1ff1n,  what's the command ?"));
						break;
					}
					case 6:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#ubuntu"));
						assertTrue(me.getHostName().equals("r190-64-97-115.dialup.adsl.anteldata.net.uy"));
						assertTrue(me.getUserName().equals("n=juan"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("Juan"));
						assertTrue(me.getMessage().equals("nothing"));
						break;
					}
					case 7:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#cod4.wars"));
						assertTrue(me.getHostName().equals("o0o.o0o.o0o.o88-51-51-6o9.co.uk"));
						assertTrue(me.getUserName().equals("4096ahmet"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("cN^proven"));
						assertTrue(me.getMessage().equals("5on5 cb.eu server on"));
						break;
					}
					case 8:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#cod4.wars"));
						assertTrue(me.getHostName().equals("p57B3E8F1.dip.t-dialin.net"));
						assertTrue(me.getUserName().equals("~gamelukas"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("sfclukas"));
						assertTrue(me.getMessage().equals("7on7 server on"));
						break;
					}
					case 9:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#cd+g"));
						assertTrue(me.getHostName().equals("outsiderz-9BEEF55C.dhcp.stls.mo.charter.com"));
						assertTrue(me.getUserName().equals("carl"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("carl"));
						assertTrue(me.getMessage().equals("@FIND C-SIDE"));
						break;
					}
					case 10:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#cd+g"));
						assertTrue(me.getHostName().equals("outsiderz-9BEEF55C.dhcp.stls.mo.charter.com"));
						assertTrue(me.getUserName().equals("carl"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("carl"));
						assertTrue(me.getMessage().equals("@find this town needs a bar"));
						break;
					}
					default:
					{
						assertTrue(false);
					}
				}
			}
		});
		
		conMan.start(session);
		assertTrue(pmReceived == 10);
		
	}
}
