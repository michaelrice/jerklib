package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.DccEvent;

class DccEventImpl implements DccEvent
{
	private String ctcpString , hostName , message , nick , userName , rawEventData;
	private Channel channel;
	private Session session;

	protected DccEventImpl
	(
		String ctcpString, 
		String hostName, 
		String message,
		String nick, 
		String userName, 
		String rawEventData, 
		Channel channel,
		Session session
	) 
	{
		super();
		this.ctcpString = ctcpString;
		this.hostName = hostName;
		this.message = message;
		this.nick = nick;
		this.userName = userName;
		this.rawEventData = rawEventData;
		this.channel = channel;
		this.session = session;
	}

	
	@Override
	public DccType getDccType()
	{
		return DccType.UNKNOWN;
	}

	@Override
	public String getRawEventData()
	{
		return this.rawEventData;
	}

	@Override
	public Session getSession()
	{
		return this.session;
	}

	@Override
	public Type getType()
	{
		return Type.CTCP_EVENT;
	}

	@Override
	public String getCtcpString()
	{
		return this.ctcpString;
	}

	@Override
	public Channel getChannel()
	{
		return this.channel;
	}

	@Override
	public String getHostName()
	{
		return this.hostName;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

	@Override
	public String getNick()
	{
		return this.nick;
	}

	@Override
	public String getUserName()
	{
		return this.userName;
	}

}
