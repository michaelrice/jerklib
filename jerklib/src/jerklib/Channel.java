package jerklib;

import java.util.List;


public interface Channel
{
	String getName();
	
	void say(String s);
	
	void addNick(String nick);
	
	boolean removeNick(String nick);
	
	void nickChanged(String oldNick , String newNick);

	List<String> getNicks();
	
	void part(String partMsg);
	
	public String getTopic();
	
	public String getTopicSetter();
	
	public String getTopicSetTime();
	
	public void setTopic(String topic);
	
}
