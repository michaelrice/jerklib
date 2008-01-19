package jerklib;

import java.util.List;


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
	 * @see Session#channelSay(String, String)
	 * @see Session#rawSay(String)
	 */
	void say(String s);
		
	void nickChanged(String oldNick , String newNick);

	List<String> getNicks();
	
	void part(String partMsg);
	
	public String getTopic();
	
	public String getTopicSetter();
	
	public String getTopicSetTime();
	
	public void setTopic(String topic);
	
}
