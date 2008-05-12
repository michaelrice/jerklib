package jerklib.parsers;

import java.util.List;

import jerklib.events.IRCEvent;
import jerklib.events.impl.NickInUseEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class NickInUseParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> tokens = token.getWordTokens();
		String nick = tokens.get(2).data.equals("*")?tokens.get(3).data:tokens.get(2).data;
		return new NickInUseEventImpl
		(
				nick,
				token.getData(), 
				event.getSession()
		); 
	}
}
