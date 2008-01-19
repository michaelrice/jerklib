	package jerklib.events;

import java.util.List;


public interface WhoisEvent extends IRCEvent
{
	public String getNick();
	
	public String getHost();
	
	public String getUser();
	
	public String getRealName();
	
	public List<String> getChannelNames();
	
	public String whoisServer();
	
	public String whoisServerInfo();
	
	public boolean isAnOperator();
	
	public boolean isIdle();
	
	public long secondsIdle();
	
	public int signOnTime();
}
