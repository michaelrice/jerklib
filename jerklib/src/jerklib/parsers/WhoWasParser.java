package jerklib.parsers;

import java.util.List;

import jerklib.events.IRCEvent;
import jerklib.events.impl.WhowasEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class WhoWasParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> tokens = token.getWordTokens();
		return new WhowasEventImpl
		(
				tokens.get(5).data, 
				tokens.get(4).data, 
				tokens.get(3).data, 
				tokens.get(tokens.size() -1).data.substring(1), 
				token.getData(), 
				event.getSession()
		); 
	}
}
