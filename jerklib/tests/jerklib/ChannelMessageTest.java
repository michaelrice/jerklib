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
 * dalnet - bahmunt
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
		session.addChannel(new Channel("#perkosa" , session));
		
		session.getServerInformation().parseServerInfo(":Vancouver.BC.CA.Undernet.org 005 r0bby___ MAXNICKLEN=15 TOPICLEN=160 AWAYLEN=160 KICKLEN=160 CHANNELLEN=200 MAXCHANNELLEN=200 CHANTYPES=#& PREFIX=(ov)@+ STATUSMSG=@+ CHANMODES=b,k,l,imnpstrDd CASEMAPPING=rfc1459 NETWORK=UnderNet :are supported by this server");
		
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
						assertTrue(me.getHostName() , me.getHostName().equals("nix-555C426C.cust.blixtvik.net"));
						assertTrue(me.getUserName() , me.getUserName().equals("~PircBot"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("TVTorrentsBot"));
						assertTrue(me.getMessage().equals("<sammyp123> o yeh"));
						break;
					}
					case 2:
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
					case 3:
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
					case 4:
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
					case 5:
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
					case 6:
					{
						assertTrue(me.getChannel() != null);
						assertTrue(me.getChannel().getName().equals("#perkosa"));
						assertTrue(me.getHostName().equals("72.20.54.161"));
						assertTrue(me.getUserName().equals("perkosa"));
						assertTrue(me.getSession().equals(session));
						assertTrue(me.getNick().equals("p3rkosa"));
						assertTrue(me.getMessage().equals(" HELLO"));
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
		assertTrue(pmReceived == 6);
		
	}
}
