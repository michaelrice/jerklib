package jerklib;

import java.util.List;

import jerklib.events.IRCEvent;

/*
 * Parser to parse just enough to stay connected,
 * get the lib to join channles and get server ircd version
 */
public class MinimalEventParser implements InternalEventParser
{
	private ConnectionManager manager;
	public MinimalEventParser(ConnectionManager manager)
	{
		this.manager = manager;
	}
	
	public void parseEvent(IRCEvent event)
	{
		Session session = event.getSession();
		String data = event.getRawEventData();
		
		EventToken eventToken = new EventToken(data);
		List<Token> tokens = eventToken.getWordTokens();
		if (tokens.isEmpty()) return;

		String command = tokens.get(1).data;

		
			if (data.matches("^PING.*"))
			{
				session.getConnection().pong(event);
				manager.addToRelayList(event);
			}
			else if (data.matches(".*PONG.*"))
			{
				session.getConnection().gotPong();
				manager.addToRelayList(event);
			}
			else if(command.equals("002") | command.equals("351"))
			{
				manager.addToRelayList(IRCEventFactory.serverVersion(eventToken, session));
			}
			else if(command.equals("001"))
			{
				manager.addToRelayList(IRCEventFactory.connectionComplete(eventToken, session));
			}
			else
			{
				manager.addToRelayList(event);
			}
			return;
		}
		
}
