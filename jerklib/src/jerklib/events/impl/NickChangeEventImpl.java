package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.NickChangeEvent;


public class NickChangeEventImpl implements NickChangeEvent
{

	private final Type type = IRCEvent.Type.NICK_CHANGE;
	private final String rawEventData,oldNick, newNick;
	private final Session session;

	public NickChangeEventImpl(String rawEventData, Session session, String oldNick, String newNick)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.oldNick = oldNick;
		this.newNick = newNick;
	}

	public final String getOldNick()
	{
		return oldNick;
	}

	public final String getNewNick()
	{
		return newNick;
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
