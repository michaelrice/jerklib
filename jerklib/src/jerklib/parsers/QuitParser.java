package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.QuitEvent;
import jerklib.events.impl.QuitEventImpl;
import jerklib.tokens.EventToken;

public class QuitParser implements CommandParser
{
	public QuitEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		String nick = token.getNick();
		List<Channel> chanList = event.getSession().removeNickFromAllChannels(nick);
		return new QuitEventImpl
		(
			token.getData(), 
			session, 
			nick, // who
			token.getUserName(), // username
			token.getHostName(), // hostName
			token.getArguments().get(0), // message
			chanList
		);
	}
}
