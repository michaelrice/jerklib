package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ServerInformationEventImpl;

public class ServerInformationParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		session.getServerInformation().parseServerInfo(token.getData());
		return new ServerInformationEventImpl(session, token.getData(), session.getServerInformation());
	}
}
