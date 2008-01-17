package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;

public class PrivateMessageEventImpl implements PrivateMsgEvent
{

	private final String rawEventData, nick, message, host, userName;
	private final Type type = IRCEvent.Type.PRIVATE_MESSAGE;
	private final Session session;

	public PrivateMessageEventImpl
	(
			String rawEventData, 
			Session session,
			String nick, 
			String userName, 
			String message, 
			String host)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.nick = nick;
		this.userName = userName;
		this.message = message;
		this.host = host;

	}

	public final String getUserName()
	{
		return userName;
	}

	public final String getNick()
	{
		return nick;
	}

	public final Type getType()
	{
		return type;
	}

	public final String getRawEventData()
	{
		return rawEventData;
	}

	public final Session getSession()
	{
		return session;
	}

	public final String getMessage()
	{
		return this.message;
	}

	public final String getHostName()
	{
		return host;
	}

	public String toString()
	{
		return rawEventData;
	}

}
