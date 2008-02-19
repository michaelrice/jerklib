package jerklib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import jerklib.events.TopicEvent;



public class Channel
{
	private String name;
	private Connection con;
	private List<String> users = new ArrayList<String>();
	private TopicEvent topicEvent;
	
	Channel(String name , Connection con)
	{
		this.name = name;
		this.con = con;
	}
	
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getTopic()
	 */
	public String getTopic()
	{
		return topicEvent != null ? topicEvent.getTopic() : "";
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getTopicSetter()
	 */
	public String getTopicSetter()
	{
	    return topicEvent != null ? topicEvent.getSetBy() : "";
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getTopicSetTime()
	 */
	public Date getTopicSetTime()
	{
	    return topicEvent.getSetWhen();
    }
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#setTopic(java.lang.String)
	 */
	public void setTopic(String topic)
	{
		con.addWriteRequest
		(
			new WriteRequest("TOPIC " + name + " :" + topic + "\r\n", con)
		);
	}
	
	void setTopicEvent(TopicEvent topicEvent)
	{
		this.topicEvent = topicEvent;
	}
	
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see jerklib.Channel#say(java.lang.String)
	 */
	public void say(String message)
	{
		con.addWriteRequest(new WriteRequest(message , this , con));
	}

    public void notice(String message) {
        con.notice(getName(),message);
    }
    


  void addNick(String nick)
	{
		if(!users.contains(nick))
		{
			users.add(nick);
		}
	}
	
	boolean removeNick(String nick)
	{
		return users.remove(nick);
	}

	void nickChanged(String oldNick, String newNick)
	{
		users.remove(oldNick);
		users.add(newNick);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getNicks()
	 */
	public List<String> getNicks()
	{
		return Collections.unmodifiableList(users);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#part(java.lang.String)
	 */
	public void part(String partMsg)
	{
		con.part(this, partMsg);
	}

    /**
     * Send an action
     * @param text action text
     */
    public void action(String text) {
      con.addWriteRequest(new WriteRequest("\001ACTION "+text+"\001",this,con));
    }


    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return (name + con.getHostName()).hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
        return o instanceof Channel && o == this;
    }
	
}






