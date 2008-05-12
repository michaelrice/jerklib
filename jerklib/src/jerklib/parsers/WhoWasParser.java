package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.impl.WhowasEventImpl;

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
