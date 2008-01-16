package jerklib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jerklib.events.TopicEvent;



public class ChannelImpl implements Channel
{
	private String name;
	private Connection con;
	private List<String>users = new ArrayList<String>();
	private TopicEvent topicEvent;
	
	public ChannelImpl(String name , Connection con)
	{
		this.name = name;
		this.con = con;
	}
	
	
	public String getTopic()
	{
		if(topicEvent != null) return topicEvent.getTopic();
		return "";
	}
	
	public String getTopicSetter()
	{
		if(topicEvent != null) return topicEvent.getSetBy();
		return "";
	}
	
	public String getTopicSetTime()
	{
		if(topicEvent != null) return topicEvent.getSetWhen();
		return "";
	}
	
	public void setTopic(String topic)
	{
		con.addWriteRequest
		(
			new WriteRequestImpl("TOPIC " + name + " :" + topic + "\r\n", con)
		);
	}
	
	void setTopicEvent(TopicEvent topicEvent)
	{
		this.topicEvent = topicEvent;
	}
	
	
	public String getName()
	{
		return name;
	}

	public void say(String message)
	{
		con.addWriteRequest(new WriteRequestImpl(message , this , con));
	}
	
	
	public void addNick(String nick)
	{
		if(!users.contains(nick))
		{
			users.add(nick);
		}
	}
	
	public boolean removeNick(String nick)
	{
		return users.remove(nick);
	}

	public void nickChanged(String oldNick, String newNick)
	{
		users.remove(oldNick);
		users.add(newNick);
	}
	
	public List<String> getNicks()
	{
		return Collections.unmodifiableList(users);
	}
	
	public void part(String partMsg)
	{
		con.part(this, partMsg);
	}
	
	
	public int hashCode()
	{
		return (name + con.getHostName()).hashCode();
	}
	
	public boolean equals(Object o)
	{
		return o == this;
	}
	
}






