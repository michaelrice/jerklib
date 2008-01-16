package jerklib.events.impl;


import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.PartEvent;


public class PartEventImpl implements PartEvent
{

	private final Type type = IRCEvent.Type.PART;
	private final String rawEventData, channelName, who, partMessage;
	private final Session session;
	private final Channel channel;

	public PartEventImpl(String rawEventData,Session session, String who, String channelName, Channel channel,
			String partMessage)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.channelName = channelName;
		this.channel = channel;
		this.who = who;
		this.partMessage = partMessage;
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
		return channel;
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

	public final String getPartMessage()
	{
		return this.partMessage;
	}

	public String toString()
	{
		return rawEventData;
	}
	
}
