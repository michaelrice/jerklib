package jerklib.events;

import java.nio.channels.UnresolvedAddressException;

public interface UnresolvedHostnameErrorEvent extends ErrorEvent
{
	String getHostName();
	
	UnresolvedAddressException getException();
}
