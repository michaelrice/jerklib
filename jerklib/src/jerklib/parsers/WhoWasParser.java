package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.events.IRCEvent;
import jerklib.events.impl.WhowasEventImpl;

public class WhoWasParser implements CommandParser
{
	
	/* :kubrick.freenode.net 314 scripy1 ty n=ty 71.237.206.180 * :ty
	 "<nick> <user> <host> * :<real name>" */
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<String>args = token.getArguments();
		return new WhowasEventImpl
		(
				args.get(3), 
				args.get(2), 
				args.get(1), 
				args.get(5), 
				token.getData(), 
				event.getSession()
		); 
	}
}
