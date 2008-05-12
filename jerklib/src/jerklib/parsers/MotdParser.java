package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.impl.MotdEventImpl;

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
