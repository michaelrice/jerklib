package jerklib.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.IRCEvent;
import jerklib.tokens.EventToken;

public class TopicUpdatedParser implements CommandParser
{
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		Pattern p = Pattern.compile("^.+?TOPIC\\s+(.+?)\\s+.*$");
		Matcher m = p.matcher(token.getData());
		m.matches();
		event.getSession().sayRaw("TOPIC " + m.group(1));
		return event;
	}
}
