package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.DccAcceptEvent;
import jerklib.events.DccEvent;

/**
 * 
 * @author Andres N. Kievsky
 */
public class DccAcceptEventImpl extends DccEventImpl implements DccAcceptEvent
{

	private String filename;
	private int port;
	private long position;

	public DccAcceptEventImpl(String filename, int port, long position, String ctcpString, String hostName, String message, String nick, String userName, String rawEventData, Channel channel,
			Session session)
	{
		super(ctcpString, hostName, message, nick, userName, rawEventData, channel, session);
		this.filename = filename;
		this.port = port;
		this.position = position;
	}

	public DccType getDccType()
	{
		return DccEvent.DccType.ACCEPT;
	}

	@Override
	public String getFilename()
	{
		return this.filename;
	}

	@Override
	public int getPort()
	{
		return this.port;
	}

	@Override
	public long getPosition()
	{
		return this.position;
	}

}
