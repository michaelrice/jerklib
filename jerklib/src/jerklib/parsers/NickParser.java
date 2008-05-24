package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.NickChangeEvent;

public class NickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		return new NickChangeEvent
		(
				token.getRawEventData(), 
				session, 
				token.getNick(), // old
				token.arg(0)// new nick
		); 
	}
}
