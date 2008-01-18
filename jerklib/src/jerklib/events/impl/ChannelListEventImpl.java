package jerklib.events.impl;

import jerklib.events.ChannelListEvent;
import jerklib.events.IRCEvent;
import jerklib.Session;
import jerklib.Channel;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class ChannelListEventImpl implements ChannelListEvent
{

	private final Session session;
	private String rawEventData;
	private final Map<Channel, Integer> chanMap = new HashMap<Channel, Integer>();
	private final Type type = IRCEvent.Type.CHANNEL_LIST_EVENT;

	public ChannelListEventImpl(String rawEventData, Session session)
	{
		this.rawEventData = rawEventData;
		this.session = session;
	}

	public Map<Channel, Integer> getChannels()
	{
		return Collections.unmodifiableMap(chanMap);
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

	public void appendToMap(Channel channel, int numberOfUsers)
	{
		chanMap.put(channel, numberOfUsers);
	}

	public void appendToRawEventData(String data)
	{
		rawEventData += "\r\n" + data;
	}
}
