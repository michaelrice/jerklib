package jerklib.parsers;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.KickEventImpl;
import jerklib.tokens.EventToken;

/**
 * @author mohadib
 *
 */
public class KickParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Session session = event.getSession();
		Channel channel = session.getChannel(token.getArguments().get(0));
		
		String msg = "";
		if (token.getArguments().size() == 3)
		{
			msg = token.getArguments().get(2);
		}
		
		return new KickEventImpl
		(
			token.getData(), 
			session, 
			token.getNick(), // byWho
			token.getUserName(), // username
			token.getHostName(), // host name
			token.getArguments().get(1), // victim
			msg, // message
			channel
		);
	}
}
