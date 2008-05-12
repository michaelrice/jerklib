package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.PartEvent;
import jerklib.events.impl.PartEventImpl;

public class PartParser implements CommandParser
{
	public PartEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		List<Token>tokens = token.getWordTokens();
			String partMsg = tokens.size() >= 4?token.concatTokens(6).substring(1):"";
			if(tokens.get(2).data.startsWith(":"))tokens.get(2).data = tokens.get(2).data.substring(1);
			return new PartEventImpl
			(
					token.getData(), 
					session,
					ParserUtil.getNick(tokens.get(0)), // who
					ParserUtil.getUserName(tokens.get(0)), // username
					ParserUtil.getHostName(tokens.get(0)), // host name
					session.getChannel(tokens.get(2).data).getName(), // channel name
					session.getChannel(tokens.get(2).data), 
					partMsg 
			);
	}
}
