package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickChangeEventImpl;

public class NickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		return new NickChangeEventImpl
		(
				token.getData(), 
				session, 
				token.getNick(), // old
				token.getArguments().get(0), // new nick
				token.getHostName(), // hostname
				token.getUserName() // username
		); 
	}
}
