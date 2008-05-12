package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.NoticeEvent;
import jerklib.events.impl.NoticeEventImpl;

public class NoticeParser implements CommandParser
{
	public NoticeEvent createEvent(EventToken token, IRCEvent event)
	{
		final List<Token> tokens = token.getWordTokens();
		Session session = event.getSession();
		// generic notice NOTICE AUTH :*** No identd (auth) response
		if(tokens.get(0).data.equals("NOTICE"))
		{
			return new NoticeEventImpl
			(
				token.getData(), 
				session, 
				"generic", 
				token.substring(token.getData().indexOf(":") + 1), 
				session.getNick(), 
				session.getConnectedHostName(),
				null
			);
		}
		
		if(tokens.get(1).data.equals("NOTICE"))
		{
			//from server to user
			//:anthony.freenode.net NOTICE mohadib_ :NickServ set your hostname to foo
			if(tokens.get(0).data.indexOf("@") == -1)
			{
				return new NoticeEventImpl
				(
					token.getData(), 
					session, 
					"server", 
					token.substring(token.getData().indexOf(":" , 1) + 1), 
					session.getNick(), 
					session.getConnectedHostName(), 
					null
				);
			}
			
			//from user to channel
			// channel notice :DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #jerklib :test
			if(session.isChannelToken(tokens.get(2)))
			{
				Channel channel = session.getChannel(tokens.get(2).data);
				return new NoticeEventImpl
				(
						token.getData(),
						session,
						"channel",
						token.getData().substring(token.getData().indexOf(":", 1) + 1),
						channel.getName(),
						ParserUtil.getNick(tokens.get(0)),
						channel
				);
			}
			else
			{
				//:NickServ!NickServ@services. NOTICE mohadib_ :This nickname is owned by someone else
				return new NoticeEventImpl
				(
						token.getData(),
						session,
						"user",
						token.getData().substring(token.getData().indexOf(":", 1) + 1),
						session.getNick(),
						ParserUtil.getNick(tokens.get(0)),
						null
				);
			}
		}
		
		return null;
	}
}
