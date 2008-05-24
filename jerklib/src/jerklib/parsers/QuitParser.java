package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.QuitEvent;

public class QuitParser implements CommandParser
{
	public QuitEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		String nick = token.getNick();
		List<Channel> chanList = event.getSession().removeNickFromAllChannels(nick);
		return new QuitEvent
		(
			token.getRawEventData(), 
			session, 
			token.arg(0), // message
			chanList
		);
	}
}
