package jerklib.events.impl;


import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;


public class ChannelMsgEventImpl implements ChannelMsgEvent
{

	private final String rawEventData, nick, message, nicksHost;
	private final Type type = IRCEvent.Type.CHANNEL_MESSAGE;
	private final Session session;
	private final Channel channel;

	public ChannelMsgEventImpl(String rawEventData, Session session, Channel channel, String nick, String message, String nicksHost)
	{

		this.rawEventData = rawEventData;
		this.session = session;
		this.channel = channel;
		this.nick = nick;
		this.message = message;
		this.nicksHost = nicksHost;

	}

	public Channel getChannel()
	{
		return channel;
	}

	public String getNick()
	{
		return nick;
	}

	public Type getType()
	{
		return type;
	}

	public String getRawEventData()
	{
		return rawEventData;
	}


	public Session getSession()
	{
		return session;
	}

	public String getMessage()
	{
		return this.message;
	}

	public String getNicksHost()
	{
		return nicksHost;
	}

	public String toString()
	{
		return rawEventData;
	}
	
}
