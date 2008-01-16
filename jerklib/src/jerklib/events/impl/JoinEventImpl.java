package jerklib.events.impl;


import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;


public class JoinEventImpl implements JoinEvent
{

	private final Type type = IRCEvent.Type.JOIN;
	private final String rawEventData, who, channelName;
	private final Session session;
	private final Channel chan;

	public JoinEventImpl(String rawEventData, Session session, String who, String channelName, Channel chan)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.who = who;
		this.channelName = channelName;
		this.chan = chan;
	}

	public final String getWho()
	{
		return who;
	}

	public final String getChannelName()
	{
		return channelName;
	}

	public final Channel getChannel()
	{
		return chan;
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

	
	public String toString()
	{
		return rawEventData;
	}
	
}
