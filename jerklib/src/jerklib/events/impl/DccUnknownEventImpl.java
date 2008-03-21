package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.DccEvent;
import jerklib.events.DccUnknownEvent;

/**
 * 
 * @author Andres N. Kievsky
 */
public class DccUnknownEventImpl extends DccEventImpl implements DccUnknownEvent
{

	public DccUnknownEventImpl(String ctcpString, String hostName, String message, String nick, String userName, String rawEventData, Channel channel, Session session)
	{
		super(ctcpString, hostName, message, nick, userName, rawEventData, channel, session);
	}

	public DccType getDccType()
	{
		return DccEvent.DccType.UNKNOWN;
	}

}
