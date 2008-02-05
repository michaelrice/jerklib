package jerklib;

import java.util.List;


/**
 * Class that represents an IRC Channel. The only way to get an
 * instance of an implementing class is by joining a channel with one of 
 * the Session.joinChannel() methods. You will then receive the Channel
 * object , if join was succesful , in a JoinCompleteEvent. You can 
 * also get at already joined channels via the Session.
 * 
 * @see Session
 * @see Session#joinChannel(String)
 * @see Session#joinChannel(String, String)
 * 
 * @author mohadib
 *
 */
public interface Channel
{
	
	/**
	 * returns name of channel
	 * 
	 * @return name of channel
	 */
	String getName();
	
	/**
	 * Says something in the channel
	 * 
	 * @param s what to say
	 * @see Session#sayChannel(String, String)
	 * @see Session#sayRaw(String)
	 */
	void say(String s);

    /**
     * Send a notice to the channel as a whole.
     * @param s what to say. 
     */
    void notice(String s);

    /**
	 * gets a list of nicks in the channel
	 * 
	 * @return list of nicks in channel
	 */
	List<String> getNicks();
	
	/**
	 * parts a channel with given message
	 * to part with no message use an empty string as argument
	 * 
	 * @param partMsg message to part with
	 */
	void part(String partMsg);
	
	/**
	 * gets the channel topic
	 * 
	 * @return the topic for channel
	 */
	public String getTopic();
	
	/**
	 * gets nick of person who set topic
	 * @return nick of topic setter
	 */
	public String getTopicSetter();
	
	/**
	 * gets unix time of when the topic was set
	 * @return unix time of when topic was set
	 */
	public String getTopicSetTime();
	
	/**
	 * sets the topic in a channel
	 * @param topic new topic
	 */
	public void setTopic(String topic);
	
}
