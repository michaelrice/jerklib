package jerklib.events.impl;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;

import java.util.List;

public class ModeEventImpl implements ModeEvent
{

	private final Type type = Type.MODE_EVENT;
	private final ModeType modeType;
	private final Session session;
	private final String rawEventData, setBy;
	private final Channel channel;
	private final List<ModeAdjustment>modeAdjustments;

	public ModeEventImpl
	(
		ModeType type,
		String rawEventData, 
		Session session, 
		List<ModeAdjustment>modeAdjustments, 
		String setBy, 
		Channel channel
	)
	{
		modeType = type;
		this.rawEventData = rawEventData;
		this.session = session;
		this.modeAdjustments = modeAdjustments;
		this.setBy = setBy;
		this.channel = channel;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public List<ModeAdjustment> getModeAdjustments()
	{
		return modeAdjustments;
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

	public ModeType getModeType()
	{
		return modeType;
	}
	
	public Type getType()
	{
		return type;
	}

}
