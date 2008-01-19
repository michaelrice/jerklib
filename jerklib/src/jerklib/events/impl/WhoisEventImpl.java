package jerklib.events.impl;

import java.util.List;

import jerklib.Session;
import jerklib.events.WhoisEvent;

public class WhoisEventImpl implements WhoisEvent 
{
	private final Type type = Type.WHOIS_EVENT;
	private final String host,user,realName,nick;
	private final Session session;
	private String whoisServer,whoisServerInfo,rawEventData;
	private List<String> channelNames;
	private boolean isOp , isIdle;
	private long secondsIdle;
	private int signOnTime;
	
	
	
	public WhoisEventImpl
	(
		String nick,
		String realName,
		String user,
		String host,
		String rawEventData,
		Session session
	)
	{
		this.nick = nick;
		this.realName = realName;
		this.user = user;
		this.host = host;
		this.session = session;
		this.rawEventData = rawEventData;
	}
	
	@Override
	public List<String> getChannelNames() 
	{
		return channelNames;
	}
	
	public void setChannelNamesList(List<String> chanNames)
	{
		channelNames = chanNames;
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
	public String whoisServerInfo()
	{
		return whoisServerInfo;
	}
	
	public void setWhoisServerInfo(String whoisServerInfo)
	{
		this.whoisServerInfo = whoisServerInfo;
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
