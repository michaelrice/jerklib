package jerklib.parsers;

import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.PartEvent;

/**
 * @author mohadib
 *
 */
public class PartParser implements CommandParser
{
	public PartEvent createEvent(EventToken token, IRCEvent event)
	{
			Session session = event.getSession();
			return new PartEvent
			(
					token.getRawEventData(), 
					session,
					token.getNick(), // who
					session.getChannel(token.arg(0)), 
					token.args().size() == 2? token.arg(1) : ""
			);
	}
}
