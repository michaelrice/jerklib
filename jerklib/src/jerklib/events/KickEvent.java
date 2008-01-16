package jerklib.events;

import jerklib.Channel;

public interface KickEvent extends IRCEvent
{
	public String byWho();
	public String message();
	public String who();
	public Channel getChannel();
}
