package jerklib.events;

import java.util.List;

import jerklib.Channel;

public interface NickListEvent extends IRCEvent 
{
	public Channel getChannel();
	public List<String> getNicks();
}
