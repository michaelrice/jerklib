package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ServerVersionEvent;

/*
"<version>.<debuglevel> <server> :<comments>"
:kubrick.freenode.net 351 scripy hyperion-1.0.2b(382). kubrick.freenode.net :iM dncrTS/v4
:kubrick.freenode.net 002 mohadib_ :Your host is kubrick.freenode.net[kubrick.freenode.net/6667], running version hyperion-1.0.2b
:irc.nixgeeks.com 002 mohadib :Your host is irc.nixgeeks.com, running version Unreal3.2.3
*/

public class ServerVersionParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{

		Session session = event.getSession();
		if(token.numeric() == 002)
		{
			return new ServerVersionEvent
			(
				token.arg(1),
				token.prefix(),
				token.arg(1).substring(token.arg(1).indexOf("running ") + 8),
				"",
				token.getRawEventData(),
				session
			);
		}
		
			return new ServerVersionEvent
			(
				token.arg(3),
				token.prefix(),
				token.arg(1), 
				"", 
				token.getRawEventData(), 
				session
			);
	}
}
