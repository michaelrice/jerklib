package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.IRCEvent;

/**
 * The event fired when a connection to a server is lost (disconnected).
 * 
 * @author mohadib
 *
 */
public class ConnectionLostEvent extends IRCEvent
{
	public ConnectionLostEvent(String data , Session session)
	{
		super(data,session,Type.CONNECTION_LOST);
	}
}
