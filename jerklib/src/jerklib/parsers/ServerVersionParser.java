package jerklib.parsers;

import java.util.List;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ServerVersionEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class ServerVersionParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> tokens = token.getWordTokens();
		Session session = event.getSession();
		
		if(token.getCommand().equals("002"))
		{
			return new ServerVersionEventImpl
			(
				"",
				tokens.get(6).data,
				tokens.get(9).data,
				"",
				token.getData(),
				session
			);
		}
		
			return new ServerVersionEventImpl
			(
				token.concatTokens(10).substring(1), 
				tokens.get(5).data,
				tokens.get(4).data, 
				"", 
				token.getData(), 
				session
			);
	}
}
