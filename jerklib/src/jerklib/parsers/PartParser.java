package jerklib.parsers;

import java.util.List;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.PartEvent;
import jerklib.events.impl.PartEventImpl;
import jerklib.tokens.EventToken;

/**
 * @author mohadib
 *
 */
public class PartParser implements CommandParser
{
	public PartEvent createEvent(EventToken token, IRCEvent event)
	{
			Session session = event.getSession();
			List<String>args = token.getArguments();
			return new PartEventImpl
			(
					token.getData(), 
					session,
					token.getNick(), // who
					token.getUserName(), // username
					token.getHostName(), // host name
					args.get(0), // channel name
					session.getChannel(args.get(0)), 
					args.size() == 2? args.get(1) : ""
			);
	}
}
