package jerklib.events.impl;

import java.nio.channels.UnresolvedAddressException;

import jerklib.Session;
import jerklib.events.UnresolvedHostnameErrorEvent;

public class UnresolvedHostnameErrorEventImpl implements UnresolvedHostnameErrorEvent
{
	private Session session;
	private String rawEventData,hostName;
	private UnresolvedAddressException exception;
	
	
	
	
	public UnresolvedHostnameErrorEventImpl
	(
		Session session,
		String rawEventData, String hostName,
		UnresolvedAddressException exception
	) 
	{
		this.session = session;
		this.rawEventData = rawEventData;
		this.hostName = hostName;
		this.exception = exception;
	}

	public UnresolvedAddressException getException() 
	{
		return exception;
	}

	public String getHostName() 
	{
		return hostName;
	}

	public ErrorType getErrorType() 
	{
		return ErrorType.UNRESOLVED_HOSTNAME;
	}

	public String getRawEventData() 
	{
		return rawEventData;
	}

	public Session getSession() 
	{
		return session;
	}

	public Type getType() 
	{
		return Type.ERROR;
	}
	
}
