package jerklib.parsers;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NoticeEventImpl;

public class NoticeParser implements CommandParser
{
	
	/*
	 *:DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #jerklib :test
	 *:anthony.freenode.net NOTICE mohadib_ :NickServ set your hostname to foo
	 *:DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #jerklib :test
	 *:NickServ!NickServ@services. NOTICE mohadib_ :This nickname is owned by someone else
	 * NOTICE AUTH :*** No identd (auth) response
	 */
	
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		
		String toWho = "";
		String byWho = session.getConnectedHostName();
		Channel chan = null;
		
		if(!session.isChannelToken(token.getArguments().get(0)))
		{
			toWho = token.getArguments().get(0);
		}
		else
		{
			chan = session.getChannel(token.getArguments().get(0));
		}
		
		if(token.getPrefix().length() > 0)
		{
			if(token.getPrefix().contains("!"))
			{
				byWho = token.getNick();
			}
			else
			{
				byWho = token.getPrefix();
			}
		}
		
		return new NoticeEventImpl
		(
			token.getData(),
			event.getSession(),
			token.getArguments().get(1),
			toWho,
			byWho,
			chan
		);
	}
}
