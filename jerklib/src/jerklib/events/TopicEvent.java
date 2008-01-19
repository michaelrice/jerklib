package jerklib.events;

import jerklib.Channel;


/**
 * @author mohadib
 * Event fired when topic is received
 * @see Channel
 */
public interface TopicEvent extends IRCEvent
{
	/**
	 * Gets the topic
	 * @return the topic
	 */
	public String getTopic();
	
	/**
	 * Gets who set the topic
	 * @return topic setter
	 */
	public String getSetBy();
	
	/**
	 * Gets when topic was set
	 * @return when
	 */
	public String getSetWhen();
	
	/**
	 * Gets Channel
	 * 
	 * @return Channel
	 * @see Channel 
	 */
	public Channel getChannel();
	
}
