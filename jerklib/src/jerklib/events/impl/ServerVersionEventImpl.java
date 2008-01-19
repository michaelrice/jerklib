package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.ServerVersionEvent;

public class ServerVersionEventImpl implements ServerVersionEvent
{
	private final String comment,hostName,version,debugLevel,rawEventData;
	private final Session session;
	private final Type type = Type.SERVER_VERSION_EVENT;
	
	public ServerVersionEventImpl
	(
			String comment, 
			String hostName, 
			String version, 
			String debugLevel, 
			String rawEventData, 
			Session session
	)
	{
		super();
		this.comment = comment;
		this.hostName = hostName;
		this.version = version;
		this.debugLevel = debugLevel;
		this.rawEventData = rawEventData;
		this.session = session;
	}

	@Override
	public String getComment()
	{
		return comment;
	}

	@Override
	public String getHostName()
	{
		return hostName;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public String getdebugLevel()
	{
		return debugLevel;
	}

	@Override
	public String getRawEventData()
	{
		return rawEventData;
	}

	@Override
	public Session getSession()
	{
		return session;
	}

	@Override
	public Type getType()
	{
		return type;
	}

}
