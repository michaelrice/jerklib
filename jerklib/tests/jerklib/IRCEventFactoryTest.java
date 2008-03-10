package jerklib;

import jerklib.events.ChannelListEvent;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.InviteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.MotdEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.NickInUseEvent;
import jerklib.events.NoticeEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class IRCEventFactoryTest
{
	private ConnectionManager conMan;
	private Session session;

    @BeforeTest
    void setUp() throws Exception
	{
		conMan = new ConnectionManager
		(
				new ProfileImpl
				(
					"DIBLET",
					"DIBLET1",
					"DIBLET2",
					"DIBLET3"
				)
		);
		conMan.requestConnection("irc.freenode.net");
		session = conMan.getSession("irc.freenode.net");
		
		while(!session.isConnected())
		{
			Thread.sleep(500);
		}
		
	}
	
	@AfterTest
	protected void tearDown() throws Exception
	{
		conMan.quit();
	}

	@Test
	public void testParseEvent()
	{
		Connection con = ((Session)session).getConnection();
		con.addChannel(new Channel("#test",session ));
		((Channel)con.getChannel("#test")).addNick("mohadib");
		
		KickEvent ke = IRCEventFactory.kick(":mohadib!~mohadib@67.41.102.162 KICK #test scab :bye!", con);
		assertNotNull(ke);
		assertEquals("mohadib" , ke.byWho());
		assertEquals("~mohadib", ke.getUserName());
		assertEquals("67.41.102.162", ke.getHostName());
		assertEquals("#test", ke.getChannel().getName());
		assertEquals("scab", ke.getWho());
		assertEquals("bye!", ke.getMessage());
		
        /*
		MessageEvent cme = IRCEventFactory.privateMsg(":fuknuit!~admin@212.199.146.104 PRIVMSG #test :blah blah", con);
		assertNotNull(cme);
		assertEquals("fuknuit", cme.getNick());
		assertEquals("~admin", cme.getUserName());
		assertEquals("212.199.146.104", cme.getHostName());
		assertEquals("#test", cme.getChannel().getName());
		assertEquals("blah blah", cme.getMessage());


		MessageEvent pme = IRCEventFactory.privateMsg(":mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!!", con);
		assertNotNull(pme);
		assertEquals("mohadib", pme.getNick());
		assertEquals("~mohadib", pme.getUserName());
		assertEquals("67.41.102.162", pme.getHostName());
		assertEquals("HY!!", pme.getMessage());
		*/
		
		ConnectionCompleteEvent cce = IRCEventFactory.connectionComplete(":irc.nmglug.org 001 DIBLET1 :Welcome to the nmglug.org", con);
		assertNotNull(cce);
		assertEquals("irc.nmglug.org", cce.getActualHostName());
		
		InviteEvent ie = IRCEventFactory.invite(":r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2", con);
		assertNotNull(ie);
		assertEquals("r0bby", ie.getNick());
		assertEquals("n=wakawaka", ie.getUserName());
		assertEquals("guifications/user/r0bby", ie.getHostName());
		assertEquals("#jerklib2", ie.getChannelName());
		
		NickChangeEvent nce = IRCEventFactory.nickChange(":raving!n=raving@74.195.43.119 NICK :Sir_Fawnpug", con);
		assertNotNull(nce);
		assertEquals("Sir_Fawnpug", nce.getNewNick());
		assertEquals("raving", nce.getOldNick());
		assertEquals("n=raving", nce.getUserName());
		assertEquals("74.195.43.119", nce.getHostName());
		
		PartEvent pe = IRCEventFactory.part(":r0bby!n=wakawaka@guifications/user/r0bby PART #test :FOO", con);
		assertNotNull(pe);
		assertEquals("r0bby", pe.getWho());
		assertEquals("n=wakawaka", pe.getUserName());
		assertEquals("guifications/user/r0bby", pe.getHostName());
		assertEquals("#test",  pe.getChannelName());
		assertEquals("FOO", pe.getPartMessage());
		
		ChannelListEvent cle = IRCEventFactory.chanList(":anthony.freenode.net 322 mohadib_ #jerklib 5 :JerkLib IRC Library - https://sourceforge.net/projects/jerklib", con);
		assertNotNull(cle);
		assertEquals("#jerklib", cle.getChannelName());
		assertEquals(5 , cle.getNumberOfUser());
		assertEquals("JerkLib IRC Library - https://sourceforge.net/projects/jerklib", cle.getTopic());
		
		JoinEvent je = IRCEventFactory.regularJoin(":r0bby!n=wakawaka@guifications/user/r0bby JOIN :#test", con);
		assertNotNull(je);
		assertEquals("r0bby", je.getNick());
		assertEquals("n=wakawaka" , je.getUserName());
		assertEquals("guifications/user/r0bby", je.getHostName());
		assertEquals("#test", je.getChannelName());
		
		QuitEvent qe = IRCEventFactory.quit(":mohadib!n=dib@cpe-24-164-167-171.hvc.res.rr.com QUIT :Client Quit", con);
		assertNotNull(qe);
		assertEquals("mohadib", qe.getWho());
		assertEquals("n=dib", qe.getUserName());
		assertEquals("cpe-24-164-167-171.hvc.res.rr.com", qe.getHostName());
		assertEquals("Client Quit", qe.getQuitMessage());
		
		//channel notice
		NoticeEvent ne = IRCEventFactory.notice(":DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #test :test", con);
		assertNotNull(ne);
		assertEquals("channel" , ne.getNoticeType());
		assertEquals("#test", ne.getChannel().getName());
		assertEquals("DIBLET", ne.byWho());
		assertEquals("test", ne.getNoticeMessage());
		assertEquals("", ne.toWho());
		
		//user notice
		ne = IRCEventFactory.notice(":NickServ!NickServ@services. NOTICE mohadib_ :This nickname is owned by someone else", con);
		assertNotNull(ne);
		assertEquals("user", ne.getNoticeType());
		assertNull(ne.getChannel());
		assertEquals("NickServ", ne.byWho());
		assertEquals("This nickname is owned by someone else", ne.getNoticeMessage());
		assertEquals("mohadib_", ne.toWho());
		
		//generic notice
		ne = IRCEventFactory.notice("NOTICE AUTH :*** No identd (auth) response" , con);
		assertNotNull(ne);
		assertEquals("generic", ne.getNoticeType());
		assertNull(ne.getChannel());
		assertEquals("", ne.byWho());
		assertEquals("AUTH :*** No identd (auth) response", ne.getNoticeMessage());
		assertEquals("", ne.toWho());
		
		MotdEvent me = IRCEventFactory.motd(":anthony.freenode.net 375 DIBLET1 :- anthony.freenode.net Message of the Day -", con);
		assertNotNull(me);
		assertEquals("- anthony.freenode.net Message of the Day -", me.getMotdLine());
		assertEquals("anthony.freenode.net", me.getHostName());
		
		me = IRCEventFactory.motd(":anthony.freenode.net 372 DIBLET1 :- Welcome to anthony.freenode.net in Irvine, CA, USA!  Thanks to", con);
		assertNotNull(me);
		assertEquals("- Welcome to anthony.freenode.net in Irvine, CA, USA!  Thanks to", me.getMotdLine());
		
		me = IRCEventFactory.motd(":anthony.freenode.net 376 DIBLET1 :End of /MOTD command.", con);
		assertNotNull(me);
		assertEquals("End of /MOTD command.", me.getMotdLine());
			
		NickInUseEvent nie = IRCEventFactory.nickInUse(":simmons.freenode.net 433 * fran :Nickname is already in use.", con);
		assertNotNull(nie);
		assertEquals("fran", nie.getInUseNick());
		
	}
	
}
