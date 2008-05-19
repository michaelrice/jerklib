package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.ConnectionLostEvent;

public class ConnectionLostEventImpl implements ConnectionLostEvent
{
	private Session session;
	
	
	public ConnectionLostEventImpl(Session session)
	{
		this.session = session;
	}
	
	public String getRawEventData()
	{
		return "";
	}

	public Session getSession()
	{
		return session;
	}

	public Type getType()
	{
		return Type.CONNECTION_LOST;
	}

}
