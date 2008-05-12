package jerklib.parsers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.impl.NickListEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class NamesParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		
		if(token.getCommand().matches("366"))
		{
			List<Token>tokens = token.getWordTokens();
			Session session = event.getSession();
			return new NickListEventImpl
			(
					token.getData(), 
					session, 
					session.getChannel(tokens.get(3).data), 
					session.getChannel(tokens.get(3).data).getNicks()
			); 
		}
		
		Pattern p = Pattern.compile("^:(?:.+?)\\s+353\\s+\\S+\\s+(?:=|@)\\s+(\\S+)\\s:(.+)$");
		Matcher m = p.matcher(token.getData());
		if (m.matches())
		{
			Channel chan = event.getSession().getChannel(m.group(1));
			String[] names = m.group(2).split("\\s+");

			for (String name : names)
			{
				if (name != null && name.length() > 0)
				{
					chan.addNick(name);
				}
			}
		}
		return event;
	}
}
