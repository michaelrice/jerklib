package jerklib.parsers;

import java.util.List;

import jerklib.events.IRCEvent;
import jerklib.events.impl.MotdEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class MotdParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token>tokens = token.getWordTokens();
		return new MotdEventImpl
		(
			token.getData(), 
			event.getSession(), 
			token.concatTokens(6).substring(1), 
			tokens.get(0).data.substring(1)
		);
	}
}
