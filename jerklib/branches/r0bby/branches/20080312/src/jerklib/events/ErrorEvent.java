package jerklib.events;

public interface ErrorEvent extends IRCEvent
{
	public enum ErrorType
	{
		NUMERIC_ERROR,
		UNRESOLVED_HOSTNAME
	}
	
	public ErrorType getErrorType();
}
