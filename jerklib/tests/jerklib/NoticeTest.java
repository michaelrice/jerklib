package jerklib;

import jerklib.events.IRCEvent;
import jerklib.events.NoticeEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.tasks.TaskImpl;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

public class NoticeTest
{

	private MockConnectionManager conMan;
	private Session session;
	int received = 0;
	
	@Test
	public void testNotices()
	{
		conMan = new MockConnectionManager();
		session = conMan.requestConnection
		(
			"anthony.freenode.net", 
			6667, 
			new ProfileImpl("testnick" , "testnick" , "testnick" , "testnick"), 
			"/home/mohadib/noticedata", 
			"/home/mohadib/TEST_OUTPUT"
		);
		
		
		session.onEvent(new TaskImpl("notice")
		{
			public void receiveEvent(IRCEvent e)
			{
				NoticeEvent ne = (NoticeEvent)e;
				received++;
				
				switch(received)
				{
					case 1:
					{
						assertTrue(ne.getNoticeType().equals("channel"));
						assertTrue(ne.getChannel().getName().equals("#jerklib"));
						assertTrue(ne.getNoticeMessage().equals("test"));
						assertTrue(ne.byWho().equals("DIBLET"));
						assertTrue(ne.toWho().equals("#jerklib"));
						assertTrue(ne.getSession().equals(session));
						break;
					}
					case 2:
					{
						assertTrue(ne.getNoticeType().equals("user"));
						assertTrue(ne.getChannel() == null);
						assertTrue(ne.getNoticeMessage().equals("This nickname is owned by someone else"));
						assertTrue(ne.byWho().equals("NickServ"));
						assertTrue(ne.toWho().equals(session.getNick()));
						assertTrue(ne.getSession().equals(session));
						break;
					}
					case 3:
					{
						assertTrue(ne.getNoticeType().equals("server"));
						assertTrue(ne.getChannel() == null);
						assertTrue(ne.getNoticeMessage().equals("NickServ set your hostname to foo"));
						assertTrue(ne.byWho() , ne.byWho().equals("anthony.freenode.net"));
						assertTrue(ne.toWho().equals(session.getNick()));
						assertTrue(ne.getSession().equals(session));
						break;
					}
					case 4:
					{
						assertTrue(ne.getNoticeType().equals("generic"));break;
					}
					case 5:
					{
						assertTrue(ne.getNoticeType().equals("channel"));
						assertTrue(ne.getChannel().getName().equals("&foo"));
						assertTrue(ne.getNoticeMessage().equals("foo"));
						assertTrue(ne.byWho().equals("fehhh"));
						assertTrue(ne.toWho().equals("&foo"));
						assertTrue(ne.getSession().equals(session));
						break;
					}
					case 6:
					{
						assertTrue(ne.getNoticeType().equals("user"));
						assertTrue(ne.getChannel() == null);
						assertTrue(ne.getNoticeMessage().equals("sucker"));
						assertTrue(ne.byWho().equals("fehhh"));
						assertTrue(ne.toWho().equals(session.getNick()));
						assertTrue(ne.getSession().equals(session));
						break;
					}
				}
				
			}
		} , Type.NOTICE);
		
		session.addChannel(new Channel("#jerklib" , session));
		session.addChannel(new Channel("&foo" , session));
		
		session.getServerInformation().parseServerInfo(":Vancouver.BC.CA.Undernet.org 005 r0bby___ MAXNICKLEN=15 TOPICLEN=160 AWAYLEN=160 KICKLEN=160 CHANNELLEN=200 MAXCHANNELLEN=200 CHANTYPES=#& PREFIX=(ov)@+ STATUSMSG=@+ CHANMODES=b,k,l,imnpstrDd CASEMAPPING=rfc1459 NETWORK=UnderNet :are supported by this server");
		
		conMan.start(session);
	}
	
}
