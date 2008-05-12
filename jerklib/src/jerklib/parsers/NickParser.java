package jerklib.parsers;

import java.util.List;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickChangeEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

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
				TokenUtil.getNick(tokens.get(0)), // old
				tokens.get(2).data.substring(1), // new nick
				TokenUtil.getHostName(tokens.get(0)), // hostname
				TokenUtil.getUserName(tokens.get(0)) // username
	); 
	}
}
