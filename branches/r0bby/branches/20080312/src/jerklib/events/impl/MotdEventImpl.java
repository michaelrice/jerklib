package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.MotdEvent;


public class MotdEventImpl implements MotdEvent
{

	private final String rawEventData, motdLine,hostName;
	private final Type type = IRCEvent.Type.MOTD;
	private Session session;

	public MotdEventImpl(String rawEventData, Session session, String motdLine , String hostName)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.motdLine = motdLine;
		this.hostName = hostName;
	}

	public String getMotdLine()
	{
		return motdLine;
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

	public String toString()
	{
		return rawEventData;
	}
	
	public String getHostName()
	{
		return hostName;
	}
}
