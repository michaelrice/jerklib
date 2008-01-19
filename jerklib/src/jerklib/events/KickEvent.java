package jerklib.events;

import jerklib.Channel;
import jerklib.Session;

/**
 * @author mohadib
 * 
 * Event fired when someone is kicked from a channel
 * @see Session#kick(String, String, Channel)
 *
 */
public interface KickEvent extends IRCEvent
{
	/**
	 * Gets the nick of the user who
	 * did the kicking
	 * @return nick 
	 */
	public String byWho();
	
	/**
	 * Gets the kick message
	 * @return message
	 */
	public String message();
	
	/**
	 * Gets the nick of who was kicked
	 * @return who was kicked
	 */
	public String who();
	
	
	/**
	 * Gets the channel object someone was kicked from
	 * @return The Channel
	 */
	public Channel getChannel();
}
