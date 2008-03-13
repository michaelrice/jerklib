package jerklib.events;

import java.util.List;
import java.util.Map;

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
	 * Gets a Map where the keys are the modes adjusted by this event.
	 * The values are a List of users the mode was applied to.The list will be empty
	 * if the mode did not apply to a user
	 * 
	 * @return the map
	 */
	public Map<String , List<String>> getModeMap();
	
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
}
