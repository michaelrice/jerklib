package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ServerInformationEvent;

public class ServerInformationParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		session.getServerInformation().parseServerInfo(token.getRawEventData());
		return new ServerInformationEvent(session, token.getRawEventData(), session.getServerInformation());
	}
}
