package jerklib.parsers;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.NickListEvent;

/**
 * @author mohadib
 *
 */
public class NamesParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{

		if (token.command().matches("366"))
		{
			Session session = event.getSession();
			return new NickListEvent
			(
				token.getRawEventData(), 
				session, 
				session.getChannel(token.arg(1)),
				session.getChannel(token.arg(1)).getNicks());
		}

		Channel chan = event.getSession().getChannel(token.arg(2));
		String[] names = token.arg(3).split("\\s+");

		for (String name : names)
		{
			if (name != null && name.length() > 0)
			{
				chan.addNick(name);
			}
		}
		return event;
	}
}
