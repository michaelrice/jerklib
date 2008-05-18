package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ServerVersionEventImpl;

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
		List<String>args = token.getArguments();
		if(token.getNumeric() == 002)
		{
			return new ServerVersionEventImpl
			(
				args.get(1),
				token.getPrefix(),
				args.get(1).substring(args.get(1).indexOf("running ") + 8),
				"",
				token.getData(),
				session
			);
		}
		
			return new ServerVersionEventImpl
			(
				token.getArguments().get(3),
				token.getPrefix(),
				token.getArguments().get(1), 
				"", 
				token.getData(), 
				session
			);
	}
}
