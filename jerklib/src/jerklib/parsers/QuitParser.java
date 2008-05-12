package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.QuitEvent;
import jerklib.events.impl.QuitEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

public class QuitParser implements CommandParser
{
	public QuitEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token>tokens = token.getWordTokens();
		Session session = event.getSession();
		String nick = TokenUtil.getNick(tokens.get(0));
		List<Channel> chanList = event.getSession().removeNickFromAllChannels(nick);
		return new QuitEventImpl
		(
			token.getData(), 
			session, 
			nick, // who
			TokenUtil.getUserName(tokens.get(0)), // username
			TokenUtil.getHostName(tokens.get(0)), // hostName
			token.concatTokens(4).substring(1), // message
			chanList
		);
	}
}
