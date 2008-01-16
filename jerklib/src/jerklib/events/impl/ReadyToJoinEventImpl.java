package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.IRCEvent;


public class ReadyToJoinEventImpl implements IRCEvent
{

	private final Type type = IRCEvent.Type.READY_TO_JOIN;
	private final String data;
	private final Session session;
	
	public ReadyToJoinEventImpl(String data , Session session)
	{
		this.session = session;
		this.data = data;
	}

	public Type getType()
	{
		return type;
	}

	public String getRawEventData()
	{
		return data;
	}

	public Session getSession()
	{
		return session;
	}

	public String toString()
	{
		return data;
	}
	
}
