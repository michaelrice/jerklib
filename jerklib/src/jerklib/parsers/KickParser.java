package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.KickEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

public class KickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		List<Token>tokens = token.getWordTokens();
			Channel channel = session.getChannel(tokens.get(2).data);
			return new KickEventImpl
			(
					token.getData(), 
					session, 
					TokenUtil.getNick(tokens.get(0)), // byWho
					TokenUtil.getUserName(tokens.get(0)), // username
					TokenUtil.getHostName(tokens.get(0)), // host name
					tokens.get(3).data, // victim
					token.concatTokens(8).substring(1), // message
					channel
			);
	}
}
