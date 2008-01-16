package jerklib.events.impl;


import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.QuitEvent;


public class QuitEventImpl implements QuitEvent
{

	private final Type type = IRCEvent.Type.QUIT;
	private final String rawEventData, who, msg;
	private final Session session;
	private final List<Channel> chanList;

	public QuitEventImpl(String rawEventData,Session session, String who, String msg, List<Channel> chanList)
	{
		this.rawEventData = rawEventData;
		this.who = who;
		this.session = session;
		this.msg = msg;
		this.chanList = chanList;
	}

	public final String getWho()
	{
		return who;
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

	public final String getQuitMessage()
	{
		return msg;
	}

	public final List<Channel> getChannelList()
	{
		return chanList;
	}

	public String toString()
	{
		return rawEventData;
	}
	
}
