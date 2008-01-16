package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.MotdEvent;


public class MotdEventImpl implements MotdEvent
{

	private final String rawEventData, motdLine;
	private final Type type = IRCEvent.Type.MOTD;
	private Session session;

	public MotdEventImpl(String rawEventData, Session session, String motdLine)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.motdLine = motdLine;
	}

	public String getMOTDLine()
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
	
}
