package jerklib.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.IRCEvent;
import jerklib.events.InviteEvent;

public class InviteParser implements CommandParser
{
	/* :r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2 */
	/* :yaloki!~yaloki@localhost INVITE SuSEmeet #test */
	public IRCEvent createEvent(IRCEvent event)
	{
		String data = event.getRawEventData();
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+INVITE.+?:(.*)$");
		Matcher m = p.matcher(data);
		m.matches();
		return new InviteEvent
		(
			m.group(4).toLowerCase(), 
			data, 
			event.getSession()
		); 
	}
}
