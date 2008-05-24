package jerklib.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.Session;
import jerklib.events.AwayEvent;
import jerklib.events.IRCEvent;
import jerklib.events.AwayEvent.EventType;

public class AwayParser implements CommandParser
{
	public IRCEvent createEvent(IRCEvent event)
	{
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s+(\\S+)\\s:(.*)$");
		Matcher m = p.matcher(event.getRawEventData());
		Session session = event.getSession();
		if (m.matches())
		{
			switch (Integer.parseInt(event.command()))
			{
			case 305:
				return new AwayEvent
				(
					session, 
					EventType.RETURNED_FROM_AWAY, 
					false, 
					true, 
					session.getNick(),
					event.getRawEventData()
				);
			case 306:
			{
				return new AwayEvent
				(
					session, 
					EventType.WENT_AWAY, 
					true, 
					true, 
					session.getNick(), 
					event.getRawEventData()
				);
			}
			}
		}
		
		// :card.freenode.net 301 r0bby_ r0bby :foo
		p = Pattern.compile("^:\\S+\\s+\\d{3}\\s+\\S+\\s+(\\S+)\\s+:(.*)$");
		m = p.matcher(event.getRawEventData());
		m.matches();
		return new AwayEvent(m.group(2), AwayEvent.EventType.USER_IS_AWAY, true, false, m.group(1), event.getRawEventData(), session);
	}
}
