package jerklib.events;

import jerklib.Channel;


public interface TopicEvent extends IRCEvent
{
	public String getTopic();
	
	public String getSetBy();
	
	public String getSetWhen();
	
	public Channel getChannel();
	
}
