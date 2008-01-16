package jerklib.events;

import jerklib.Channel;

public interface ModeEvent extends IRCEvent 
{
	public String getMode();
	
	public String setBy();
	
	public Channel getChannel();
	
	public String getUser();
}
