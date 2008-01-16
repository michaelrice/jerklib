package jerklib.events;

import java.util.List;

import jerklib.Channel;

public interface WhoisEvent extends IRCEvent
{
	public String getNick();
	
	public String getHost();
	
	public String getIdent();
	
	public List<Channel> getChannelList();
	
	public String whoisServer();
	
	public boolean isAnOperator();
	
	public boolean isIdle();
	
	public long secondsIdle();
	
	public int signOnTime();
}
