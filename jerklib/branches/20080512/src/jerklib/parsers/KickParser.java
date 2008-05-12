package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.impl.KickEventImpl;

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
					ParserUtil.getNick(tokens.get(0)), // byWho
					ParserUtil.getUserName(tokens.get(0)), // username
					ParserUtil.getHostName(tokens.get(0)), // host name
					tokens.get(3).data, // victim
					token.concatTokens(8).substring(1), // message
					channel
			);
	}
}
