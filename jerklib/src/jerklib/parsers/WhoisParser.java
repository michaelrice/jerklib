package jerklib.parsers;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.IRCEvent;
import jerklib.events.impl.WhoisEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;

public class WhoisParser implements CommandParser
{
	private WhoisEventImpl we;
	
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		int numeric = Integer.valueOf(token.getCommand());
		List<Token> tokens = token.getWordTokens();
		switch (numeric)
		{
		case 311:
		{
			// "<nick> <user> <host> * :<real name>"
			we = new WhoisEventImpl
			(	
					tokens.get(4).data, 
					token.getData().substring(token.getData().lastIndexOf(":") + 1), 
					tokens.get(5).data, 
					tokens.get(6).data, 
					token.getData(), 
					event.getSession()
			); 
			
			break;
		}
		case 319:
		{
			// "<nick> :{[@|+]<channel><space>}"
			// :kubrick.freenode.net 319 scripy mohadib :@#jerklib
			// kubrick.freenode.net 319 scripy mohadib :@#jerklib ##swing
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				List<String> chanNames = Arrays.asList(m.group(1).split("\\s+"));
				we.setChannelNamesList(chanNames);
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// "<nick> <server> :<server info>"
			// :kubrick.freenode.net 312 scripy mohadib irc.freenode.net
			// :http://freenode.net/
		case 312:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s(\\S+)\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				we.setWhoisServer(m.group(1));
				we.setWhoisServerInfo(m.group(2));
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// not in RFC1459
			// :kubrick.freenode.net 320 scripy mohadib :is identified to services
		case 320:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(\\S+)\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				// System.out.println("nick idented: " + m.group(1) + " " + m.group(2));
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// :anthony.freenode.net 317 scripy scripy 2 1202063240 :seconds idle,
			// signon time
			// from rfc "<nick> <integer> :seconds idle"
		case 317:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s+(\\d+)\\s+(\\d+)\\s+:.*$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				we.setSignOnTime(Integer.parseInt(m.group(2)));
				we.setSecondsIdle(Integer.parseInt(m.group(1)));
			}
		}
		case 318:
		{
			// end of whois - fireevent
			if (we != null)
			{
				we.appendRawEventData(token.getData());
				return we;
			}
			break;
		}
		}
		return event;
	}
}
