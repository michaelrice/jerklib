package jerklib.events.impl;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.WhoisEvent;

public class WhoisEventImpl implements WhoisEvent 
{
	private final Type type = Type.WHOIS_EVENT;
	private final String host,user,realName,nick;
	private final Session session;
	private String whoisServer,rawEventData;
	private List<Channel> channels;
	private boolean isOp , isIdle;
	private long secondsIdle;
	private int signOnTime;
	
	
	
	public WhoisEventImpl
	(
		String nick,
		String realName,
		String user,
		String host,
		Session session
	)
	{
		this.nick = nick;
		this.realName = realName;
		this.user = user;
		this.host = host;
		this.session = session;
	}
	
	@Override
	public List<Channel> getChannelList() 
	{
		return channels;
	}
	
	public void setChannelList(List<Channel> channels)
	{
		this.channels = channels;
	}
	
	@Override
	public String getHost() 
	{
		return host;
	}

	@Override
	public String getUser() 
	{
		return user;
	}

	@Override
	public String getRealName() 
	{
		return realName;
	}
	
	@Override
	public String getNick() 
	{
		return nick;
	}

	@Override
	public boolean isAnOperator() 
	{
		return isOp;
	}
	
	public void setIsOp(boolean isOp)
	{
		this.isOp = isOp;
	}
	
	@Override
	public boolean isIdle() 
	{
		return isIdle;
	}

	public void setIsIdle(boolean isIdle)
	{
		this.isIdle = isIdle;
	}
	
	@Override
	public long secondsIdle() 
	{
		return secondsIdle;
	}

	public void setSecondsIdle(int secondsIdle)
	{
		this.secondsIdle = secondsIdle();
	}
	
	@Override
	public int signOnTime() 
	{
		return signOnTime;
	}
	
	public void setSignOnTime(int signOnTime)
	{
		this.signOnTime = signOnTime;
	}

	@Override
	public String whoisServer() 
	{
		return whoisServer;
	}
	
	public void setWhoisServer(String whoisServer)
	{
		this.whoisServer = whoisServer;
	}
	
	@Override
	public String getRawEventData() 
	{
		return rawEventData;
	}
	
	public void appendRawEventData(String rawEventData)
	{
		this.rawEventData += "\r\n" + rawEventData;
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
