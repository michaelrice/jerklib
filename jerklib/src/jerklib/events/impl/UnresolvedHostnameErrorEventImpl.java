package jerklib.events.impl;

import jerklib.Session;
import java.nio.channels.UnresolvedAddressException;


/**
 * Error generated when a DNS lookup fails during connection.
 * 
 * @author mohadib
 *
 */
public class UnresolvedHostnameErrorEventImpl extends ErrorEvent
{
	private String hostName;
	private UnresolvedAddressException exception;

	public UnresolvedHostnameErrorEventImpl
	(
		Session session, 
		String rawEventData, 
		String hostName, 
		UnresolvedAddressException exception
	)
	{
		super(rawEventData, session, ErrorType.UNRESOLVED_HOSTNAME);
		this.hostName = hostName;
		this.exception = exception;
	}

  /**
   * Gets the wrapped UnresolvedAddressException
   * @return UnresolvedAddressException
   */
	public UnresolvedAddressException getException()
	{
		return exception;
	}

	 /**
   * Gets the unresolvable hostname
   * @return hostname that could not be resloved
   */
	public String getHostName()
	{
		return hostName;
	}
}
