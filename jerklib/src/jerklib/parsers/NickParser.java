package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickChangeEventImpl;

public class NickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> tokens = token.getWordTokens();
		Session session = event.getSession();
		return new NickChangeEventImpl
		(
				token.getData(), 
				session, 
				ParserUtil.getNick(tokens.get(0)), // old
				tokens.get(2).data.substring(1), // new nick
				ParserUtil.getHostName(tokens.get(0)), // hostname
				ParserUtil.getUserName(tokens.get(0)) // username
	); 
	}
}
