package jerklib.parsers;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickListEventImpl;
import jerklib.tokens.EventToken;

/**
 * @author mohadib
 *
 */
public class NamesParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{

		if (token.getCommand().matches("366"))
		{
			Session session = event.getSession();
			return new NickListEventImpl
			(
				token.getData(), 
				session, 
				session.getChannel(token.getArguments().get(1)),
				session.getChannel(token.getArguments().get(1)).getNicks());
		}

		Channel chan = event.getSession().getChannel(token.getArguments().get(2));
		String[] names = token.getArguments().get(3).split("\\s+");

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
