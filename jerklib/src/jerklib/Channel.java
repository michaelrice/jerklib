package jerklib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

import jerklib.events.TopicEvent;



public class Channel
{
	private String name;
	private Connection con;
	private Session session;
	private Map<String, List<String>> userMap = new HashMap<String, List<String>>();
	private TopicEvent topicEvent;
	
	Channel(String name , Session session)
	{
		this.name = name;
		this.session = session;
		this.con = ((SessionImpl)session).getConnection();
	}
	
	
	
	void updateUsersMode(String username , String mode)
	{
		List<String>modes = userMap.get(username);
		if(modes == null) modes = new ArrayList<String>();
		String modeChar = mode.substring(1);
		modes.remove("-"+modeChar);
		modes.remove("+"+modeChar);
		modes.remove(modeChar);
		modes.add(mode);
		userMap.put(username, modes);
	}
	
	public List<String> getUsersModes(String nick)
	{
		if(userMap.containsKey(nick))
		{
			return userMap.get(nick);
		}
		else
		{
			return new ArrayList<String>();
		}
	}
	
	
	public List<String> getNicksForMode(String mode)
	{
		List<String> nicks = new ArrayList<String>();
		for(String nick : userMap.keySet())
		{
			List<String> modes = userMap.get(nick);
			if(modes != null && modes.contains(mode))
			{
				nicks.add(nick);
			}
		}
		return nicks;
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
	  if(!userMap.containsKey(nick))
	  {
		  
		  ServerInformation info = session.getServerInformation();
		  Map<String,String> nickPrefixMap = info.getNickPrefixeMap();
		  List<String> modes = new ArrayList<String>();
		  for(String prefix : nickPrefixMap.keySet())
		  {
			  if(nick.startsWith(prefix)) 
			  {
				  modes.add(nickPrefixMap.get(prefix));
			  }
		  }
		  
		  if(!modes.isEmpty())
		  {
			  nick = nick.substring(1);
		  }
		  userMap.put(nick, modes);
	  }
	}
	
	boolean removeNick(String nick)
	{
		return userMap.remove(nick) != null; 
	}

	void nickChanged(String oldNick, String newNick)
	{
		List<String> modes = userMap.remove(oldNick);
		userMap.put(newNick, modes);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Channel#getNicks()
	 */
	public List<String> getNicks()
	{
		return new ArrayList<String>(Collections.unmodifiableCollection(userMap.keySet()));
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
        return o == this;
    }
	
}






