package jerklib.events.impl;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.ModeEvent;

public class ModeEventImpl implements ModeEvent 
{

	private final Type type = Type.MODE_EVENT;
	private final Session session;
	private final String rawEventData ,user , setBy;
	private final Channel channel;
	private final List<String>modes;
	
	public ModeEventImpl
	(
		String rawEventData,
		Session session,
		List<String> modes,
		String user,
		String setBy,
		Channel channel
	)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.modes = modes;
		this.user = user;
		this.setBy = setBy;
		this.channel = channel;
	}
	
	public Channel getChannel() 
	{
		return channel;
	}

	public List<String> getModes()
	{
		return modes;
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
