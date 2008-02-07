package jerklib.events;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;

/**
 * @author Mohadib
 * Event fired when mode changes from a user or Channel
 * @see Channel
 * @see Session#mode(Channel, String)
 * @see Session#mode(String, Channel, String)
 *
 */
public interface ModeEvent extends IRCEvent 
{
	/**
	 * Get the modes set
	 * @return List with modes
	 */
	public List<String> getModes();
	
	/**
	 * Gets who set the mode
	 * @return who set the mode
	 */
	public String setBy();
	
	/**
	 * If mode event adjusted a Channel mode
	 * then the Channel effected will be returned
	 * @return Channel
	 * @see Channel
	 */
	public Channel getChannel();
	
	/**
	 * If the mode event adjusted a users mode
	 * then the User effected will be returned
	 * @return user 
	 */
	public String getUser();
}
