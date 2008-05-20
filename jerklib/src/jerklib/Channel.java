package jerklib;

import jerklib.events.TopicEvent;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeAdjustment.Action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Class to represent a <b>joined</b> IRC channel. This class has methods to
 * interact with IRC Channels like say() , part() , getChannelModes() etc.
 * 
 * You will never need to create an instance of this class manually. Instead it
 * will be created for you and stored in the Session when you successfully join
 * a channel.
 * 
 * @see Session
 * @see Session#getChannel(String)
 * @see Session#getChannels()
 * @see Session#getChannelNames()
 * @see JoinCompleteEvent
 * 
 * @author mohadib
 * 
 */
public class Channel
{
	/* channel name */
	private String name;
	private Connection con;
	private Session session;
	private Map<String, List<ModeAdjustment>> userMap;
	private List<ModeAdjustment> channelModes = new ArrayList<ModeAdjustment>();
	private TopicEvent topicEvent;

	/**
	 * This should only be used internally and for testing
	 * 
	 * @param name - Name of Channel
	 * @param session - Session Channel belongs to
	 */
	public Channel(String name, Session session)
	{
		/* create a map that will match exact and key to lowercase */
		userMap = new HashMap<String, List<ModeAdjustment>>()
		{
			public List<ModeAdjustment> get(Object key)
			{
				List<ModeAdjustment> rList = super.get(key);
				if(key != null && rList == null)
				{
					rList = super.get(key.toString().toLowerCase());
				}
				return rList;
			}
			
			public List<ModeAdjustment> remove(Object key)
			{
				List<ModeAdjustment> rList =  super.remove(key);
				if(key != null && rList == null)
				{
					rList = super.remove(key.toString().toLowerCase());
				}
				return rList;
			}
			
			public boolean containsKey(Object key)
			{
				boolean b =  super.containsKey(key);
				if(!b) b = super.containsKey(key.toString().toLowerCase());
				return b;
			}
		};
		
		this.name = name;
		this.session = session;
		this.con = session.getConnection();
	}

	/**
	 * Updates Channel's modes.
	 * 
	 * Only tracks channel modes that apply to users in the channel if the mode is
	 * in the nick prefix map received in numeric 005. If no numeric is passed
	 * o,v,h are used by default.
	 * 
	 * So basically modes that do not change the apperance of a nick with a prefix
	 * are not tracked if the mode applies to a user. Example: q and b are not
	 * tracked.
	 * 
	 * If the mode does not apply to a user in the channel , the mode will always
	 * be tracked. Example: i is tracked
	 * 
	 * @param modes -
	 *          list of ModeAdjustments
	 */
	void updateModes(List<ModeAdjustment> modes)
	{
		ServerInformation info = session.getServerInformation();
		List<String> nickModes = new ArrayList<String>(info.getNickPrefixMap().values());

		for (ModeAdjustment mode : modes)
		{
			if (nickModes.contains(String.valueOf(mode.getMode())) && userMap.containsKey(mode.getArgument()))
			{
				updateMode(mode, userMap.get(mode.getArgument()));
			}
			/* filter out channel modes that apply to users that are not in prefix map */
			/* like +b - this behviour might not be desired , time will tell */
			else if (mode.getMode() != 'q' && mode.getMode() != 'b')
			{
				updateMode(mode, channelModes);
			}
		}
	}

	/**
	 * If Action is MINUS and the same mode exists with a PLUS Action then just
	 * remove the PLUS mode ModeAdjustment from the collection.
	 * 
	 * If Action is MINUS and the same mode with PLUS does not exist then add the
	 * MINUS mode to the ModeAdjustment collection
	 * 
	 * if Action is PLUS and the same mode exists with a MINUS Action then remove
	 * MINUS mode and add PLUS mode
	 * 
	 * If Action is PLUS and the same mode with MINUS does not exist then just add
	 * PLUS mode to collection
	 * 
	 * @param mode
	 */
	private void updateMode(ModeAdjustment mode, List<ModeAdjustment> modes)
	{
		int index = indexOfMode(mode.getMode(), modes);

		if (mode.getAction() == Action.MINUS)
		{
			if (index != -1)
			{
				ModeAdjustment ma = modes.remove(index);
				if (ma.getAction() == Action.MINUS) modes.add(ma);
			}
			else
			{
				modes.add(mode);
			}
		}
		else
		{
			if (index != -1) modes.remove(index);
			modes.add(mode);
		}
	}

	/**
	 * Finds index of a mode in a List of ModeAdjustments
	 * 
	 * @param mode
	 *          mode to find
	 * @param modes
	 *          list to search
	 * @return index or -1 if not found
	 */
	private int indexOfMode(char mode, List<ModeAdjustment> modes)
	{
		for (int i = 0; i < modes.size(); i++)
		{
			ModeAdjustment ma = modes.get(i);
			if (ma.getMode() == mode) return i;
		}
		return -1;
	}

	/**
	 * Get a list of user's channel modes
	 * Returns an empty list if the nick does not exist.
	 * 
	 * @param nick
	 * @return list of ModeAdjustments for user
	 */
	public List<ModeAdjustment> getUsersModes(String nick)
	{
		if (userMap.containsKey(nick))
		{
			return new ArrayList<ModeAdjustment>(userMap.get(nick));
		}
		else
		{
			return new ArrayList<ModeAdjustment>();
		}
	}

	/**
	 * Gets a list of user in channel with a given mode set.
	 * A user will only match if they have the exact mode set.
	 * Non existance of +(mode) does not imply that the user is -(mode)
	 * 
	 * So searching for -v would almost always return an empty list.
	 * 
	 * @param action 
	 * @param mode
	 * @return List of nicks with mode/action set
	 */
	public List<String> getNicksForMode(Action action , char mode)
	{
		List<String> nicks = new ArrayList<String>();
		for (String nick : getNicks())
		{
			List<ModeAdjustment> modes = userMap.get(nick);
			for (ModeAdjustment ma : modes)
			{
				if (ma.getMode() == mode && ma.getAction() == action) nicks.add(nick);
			}
		}
		return nicks;
	}

	/**
	 * Returns a list of modes that apply to the channel but dont apply
	 * to users in the channel. I.E. +o is not returned as that applies
	 * to users in the channel not just the channel.
	 * 
	 * @return List of ModeAdjustments for the Channel
	 */
	public List<ModeAdjustment> getChannelModes()
	{
		return new ArrayList<ModeAdjustment>(channelModes);
	}

	/**
	 * Sets a mode in the Channel is you have the permissions to do so.
	 * 
	 * example: +vv00 foo bar baz bob
	 * example: -v+i foo
	 * 
	 * @param mode to set.
	 */
	public void mode(String mode)
	{
		session.mode(name, mode);
	}

	/**
	 * Gets the topic for the channel or an empty string is the topic is not set.
	 * 
	 * @return topic for channel
	 */
	public String getTopic()
	{
		return topicEvent != null ? topicEvent.getTopic() : "";
	}
	
	/**
	 * Gets the nick of who set the topic or an empty string if the topic is not set.
	 * 
	 * @return
	 */
	public String getTopicSetter()
	{
		return topicEvent != null ? topicEvent.getSetBy() : "";
	}

	/**
	 * Returns the Date the topic was set or null if the topic is unset.
	 * 
	 * @return
	 */
	public Date getTopicSetTime()
	{
		return topicEvent == null? null:topicEvent.getSetWhen();
	}

	/**
	 * Sets the topic of the Channel is you have the permissions to do so.
	 * 
	 * @param topic to use.
	 */
	public void setTopic(String topic)
	{
		con.addWriteRequest(new WriteRequest("TOPIC " + name + " :" + topic + "\r\n", con));
	}

	/**
	 * This method should only be used internally
	 * 
	 * @param topicEvent
	 */
	public void setTopicEvent(TopicEvent topicEvent)
	{
		this.topicEvent = topicEvent;
	}

	/**
	 * Gets the Channel name.
	 * 
	 * @return name of Channel
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Speak in the Channel.
	 * 
	 * @param message - what to say
	 */
	public void say(String message)
	{
		session.sayChannel(this, message);
	}

	/**
	 * Send a notice in the Channel
	 * 
	 * @param message - notice messgae
	 */
	public void notice(String message)
	{
		session.notice(getName(), message);
	}
	
	/**
	 * This method is for internal use only
	 * 
	 * @param nick to add
	 */
	public void addNick(String nick)
	{
		if (!userMap.containsKey(nick))
		{

			ServerInformation info = session.getServerInformation();
			Map<String, String> nickPrefixMap = info.getNickPrefixMap();
			List<ModeAdjustment> modes = new ArrayList<ModeAdjustment>();
			for (String prefix : nickPrefixMap.keySet())
			{
				if (nick.startsWith(prefix))
				{
					modes.add(new ModeAdjustment(Action.PLUS, nickPrefixMap.get(prefix).charAt(0), ""));
				}
			}

			// TODO can a nick come in as voiced and oped? +@dib ?
			// if so substring nick with modes.size();
			if (!modes.isEmpty())
			{
				nick = nick.substring(1);
			}
			userMap.put(nick, modes);
		}
	}

	/**
	 * removes a nick from the Channel nick list
	 * 
	 * @param nick
	 * @return true if nick was removed else false
	 */
	boolean removeNick(String nick)
	{
		return userMap.remove(nick) != null;
	}

	/**
	 * Called to update nick list when nick change happens
	 * 
	 * @param oldNick
	 * @param newNick
	 */
	void nickChanged(String oldNick, String newNick)
	{
		List<ModeAdjustment> modes = userMap.remove(oldNick);
		userMap.put(newNick, modes);
	}

	/**
	 * Gets a list of nicks for Channel.
	 * The list returned has a case insenstive
	 * indexOf() and contains()
	 * 
	 * @return List of nicks
	 */
	public List<String> getNicks()
	{
		return new ArrayList<String>(userMap.keySet())
		{
			public int indexOf(Object o)
			{
				if (o != null)
				{
					for (int i = 0; i < size(); i++)
					{
						if (get(i).equalsIgnoreCase(o.toString())) { return i; }
					}
				}
				return -1;
			}
		};
	}
	
	/**
	 * Part the channel
	 * 
	 * @param partMsg
	 */
	public void part(String partMsg)
	{
		session.part(this, partMsg);
	}

	/**
	 * Send an action
	 * 
	 * @param text action text
	 */
	public void action(String text)
	{
		con.addWriteRequest(new WriteRequest("\001ACTION " + text + "\001", this, con));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (this == o) { return true; }
		if (!(o instanceof Channel)) { return false; }

		Channel channel = (Channel) o;

		if (con != null ? !con.getHostName().equals(channel.con.getHostName()) : channel.con != null) { return false; }
		if (name != null ? !name.equals(channel.name) : channel.name != null) { return false; }

		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int result;
		result = (name != null ? name.hashCode() : 0);
		result = 31 * result + (con != null ? con.getHostName().hashCode() : 0);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[Channel: name=" + name + "]";
	}
}
