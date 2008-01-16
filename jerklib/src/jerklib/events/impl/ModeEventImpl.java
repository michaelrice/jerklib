package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.ModeEvent;

public class ModeEventImpl implements ModeEvent {

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
	
	@Override
	public Channel getChannel() 
	{
		return channel;
	}

	@Override
	public String getMode() 
	{
		return mode;
	}

	@Override
	public String getUser() 
	{
		return user;
	}

	@Override
	public String setBy() 
	{
		return setBy;
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
