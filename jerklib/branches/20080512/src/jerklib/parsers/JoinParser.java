package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;
import jerklib.events.impl.JoinCompleteEventImpl;
import jerklib.events.impl.JoinEventImpl;

public class JoinParser implements CommandParser
{

	// :r0bby!n=wakawaka@guifications/user/r0bby JOIN :#jerklib
	// :mohadib_!~mohadib@68.35.11.181 JOIN &test
	
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> tokens = token.getWordTokens();
		Session session = event.getSession();
		
		JoinEvent je = new JoinEventImpl
		(
			token.getData(), 
			session, 
			ParserUtil.getNick(tokens.get(0)), // nick
			ParserUtil.getUserName(tokens.get(0)), // user name
			ParserUtil.getHostName(tokens.get(0)), // host
			tokens.get(2).data.replaceFirst(":", ""), // channel name
			session.getChannel(tokens.get(2).data.replaceFirst(":", "")) // channel
		);
		
		if(je.getNick().equalsIgnoreCase(event.getSession().getNick()))
		{
			System.out.println("GOOOOOOOT JOIN COMPLETE");
			return new JoinCompleteEventImpl
			(
				je.getRawEventData(), 
				je.getSession(),
				new Channel(je.getChannelName() , event.getSession())
			);
		}
		return je;
	}
}
