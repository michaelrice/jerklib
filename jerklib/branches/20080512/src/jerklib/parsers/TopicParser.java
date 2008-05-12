package jerklib.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.TopicEvent;
import jerklib.events.impl.TopicEventImpl;


//TODO this class is fucked and a left over from old code
//PLOX FIX ME
public class TopicParser implements CommandParser
{
	private Map<Channel, TopicEvent> topicMap = new HashMap<Channel, TopicEvent>();
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		int numeric = Integer.parseInt(token.getCommand());
		if(numeric == 332)
		{
			List<Token> tokens = token.getWordTokens();
			TopicEvent tEvent = new TopicEventImpl
			(
					token.getData(), 
					event.getSession(), 
					event.getSession().getChannel(tokens.get(3).data),
					token.concatTokens(8).substring(1)
			);
			if (topicMap.containsValue(tEvent.getChannel()))
			{
				((TopicEventImpl) topicMap.get(tEvent.getChannel())).appendToTopic(tEvent.getTopic());
			}
			else
			{
				topicMap.put(tEvent.getChannel(), tEvent);
			}
		}
		else
		{
			Pattern p = Pattern.compile(":(\\S+)\\s+333\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)$");
			Matcher m = p.matcher(token.getData());
			m.matches();
			Channel chan = (Channel)event.getSession().getChannel(m.group(3));
			if (topicMap.containsKey(chan))
			{
				TopicEventImpl tEvent = (TopicEventImpl) topicMap.get(chan);
				topicMap.remove(chan);
				tEvent.setSetBy(m.group(4));
				tEvent.setSetWhen(m.group(5));
				chan.setTopicEvent(tEvent);
				return tEvent;
			}
		}
		return event;
	}
}
