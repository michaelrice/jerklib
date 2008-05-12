package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.QuitEvent;
import jerklib.events.impl.QuitEventImpl;

public class QuitParser implements CommandParser
{
	public QuitEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token>tokens = token.getWordTokens();
		Session session = event.getSession();
		String nick = ParserUtil.getNick(tokens.get(0));
		List<Channel> chanList = event.getSession().removeNickFromAllChannels(nick);
		return new QuitEventImpl
		(
			token.getData(), 
			session, 
			nick, // who
			ParserUtil.getUserName(tokens.get(0)), // username
			ParserUtil.getHostName(tokens.get(0)), // hostName
			token.concatTokens(4).substring(1), // message
			chanList
		);
	}
}
