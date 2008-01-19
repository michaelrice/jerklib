package jerklib.events;

public interface ServerVersionEvent extends IRCEvent
{
	String getVersion();
	String getdebugLevel();
	String getHostName();
	String getComment();
}
