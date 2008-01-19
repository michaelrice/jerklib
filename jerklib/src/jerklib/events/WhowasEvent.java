package jerklib.events;

public interface WhowasEvent extends IRCEvent
{
	String getNick();
	String getUserName();
	String getHostName();
	String getRealName();
}
