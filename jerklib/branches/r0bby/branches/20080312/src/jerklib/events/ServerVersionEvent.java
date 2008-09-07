package jerklib.events;

/**
 * @author Mohadib
 * Event fired for server version information
 *
 */
public interface ServerVersionEvent extends IRCEvent
{
	/**
	 * Gets the version string the server sent
	 * @return version string 
	 */
	String getVersion();
	
	/**
	 * Not impled
	 * @return Not impled
	 */
	String getdebugLevel();
	
	/**
	 * Gets the host name
	 * @return hostname
	 */
	String getHostName();
	
	/**
	 * Gets the server version comment
	 * @return comment
	 */
	String getComment();
}
