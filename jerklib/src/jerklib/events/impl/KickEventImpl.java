package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.KickEvent;

public class KickEventImpl implements KickEvent 
{

	private final Type type = Type.KICK_EVENT;
	private final String byWho,who,message,rawEventData;
	private final Channel channel;
	private final Session session;
	
	
	public KickEventImpl
	(
		String rawEventData,
		Session session,
		String byWho,
		String who,
		String message,
		Channel channel
	)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.byWho = byWho;
		this.who = who;
		this.message = message;
		this.channel = channel;
	}
	
	@Override
	public String byWho() 
	{
		return byWho;
	}

	@Override
	public String who()
	{
		return who;
	}

	@Override
	public String message() 
	{
		return message;
	}

	@Override
	public Channel getChannel() 
	{
		return channel;
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
