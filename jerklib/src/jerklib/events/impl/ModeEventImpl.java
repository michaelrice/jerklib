package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.ModeEvent;

public class ModeEventImpl implements ModeEvent 
{

	private final Type type = Type.MODE_EVENT;
	private final Session session;
	private final String rawEventData , mode , user , setBy;
	private final Channel channel;
	
	public ModeEventImpl
	(
		String rawEventData,
		Session session,
		String mode,
		String user,
		String setBy,
		Channel channel
	)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.mode = mode;
		this.user = user;
		this.setBy = setBy;
		this.channel = channel;
	}
	
	public Channel getChannel() 
	{
		return channel;
	}

	public String getMode() 
	{
		return mode;
	}

	public String getUser() 
	{
		return user;
	}

	public String setBy() 
	{
		return setBy;
	}

	public String getRawEventData() 
	{
		return rawEventData;
	}

	public Session getSession() 
	{
		return session;
	}

	public Type getType() 
	{
		return type;
	}

}
