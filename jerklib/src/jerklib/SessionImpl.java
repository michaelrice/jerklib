package jerklib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.Task;

/**
 * @author mohadib
 * @see Session
 * 
 */
public class SessionImpl implements Session
{

	private final List<String> channelNames = new ArrayList<String>();
	private boolean rejoinOnKick = true;
	private Connection con;
	private final RequestedConnection rCon;
	private Profile tmpProfile;
	private boolean profileUpdating;
	private boolean isAway;
	private final List<IRCEventListener> listenerList = new ArrayList<IRCEventListener>();
	private final Map<Type, List<Task>> taskMap = new HashMap<Type, List<Task>>();
	private long lastRetry = -1;
	
	SessionImpl(RequestedConnection rCon)
	{
		this.rCon = rCon;
	}

	void setConnection(Connection con)
	{
		this.con = con;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getRequestedConnection()
	 */
	public RequestedConnection getRequestedConnection()
	{
		return rCon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getChannel(java.lang.String)
	 */
	public Channel getChannel(String channelName)
	{
		if (con != null) { return con.getChannel(channelName); }
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getChannelNames()
	 */
	public List<String> getChannelNames()
	{
		return Collections.unmodifiableList(channelNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getChannels()
	 */
	public Collection<Channel> getChannels()
	{
		if (con != null) { return con.getChannels(); }
		return new ArrayList<Channel>();
	}

	public boolean isAway()
	{
		return isAway;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#isConnected()
	 */
	public boolean isConnected()
	{
		return con != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#isRejoinOnKick()
	 */
	public boolean isRejoinOnKick()
	{
		return rejoinOnKick;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#setRejoinOnKick(boolean)
	 */
	public void setRejoinOnKick(boolean rejoin)
	{
		rejoinOnKick = rejoin;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#joinChannel(java.lang.String)
	 */
	public void joinChannel(String channelName)
	{

		if (!channelNames.contains(channelName) && con != null)
		{
			channelNames.add(channelName);
			con.join(channelName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#joinChannel(java.lang.String, java.lang.String)
	 */
	public void joinChannel(String channelName, String pass)
	{
		if (!channelNames.contains(channelName.toLowerCase()))
		{
			channelNames.add(channelName);
			if (con != null)
			{
				con.join(channelName, pass);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#partChannel(jerklib.Channel, java.lang.String)
	 */
	public boolean partChannel(Channel channel, String msg)
	{
		return partChannel(channel.getName(), msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#partChannel(java.lang.String, java.lang.String)
	 */
	public boolean partChannel(String channelName, String msg)
	{
		channelNames.remove(channelName.toLowerCase());
		return con.part(channelName, msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#close(java.lang.String)
	 */
	public void close(String quitMessage)
	{
		if (con != null && channelNames.size() > 0)
		{
			con.quit(quitMessage);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#sayPrivate(java.lang.String, java.lang.String)
	 */
	public void sayPrivate(String nick, String msg)
	{
		if (con != null && isConnected())
		{
			con.addWriteRequest(new WriteRequest(msg, con, nick));
		}
		else
		{
			new Exception().printStackTrace();
		}
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#channelSay(java.lang.String, java.lang.String)
	 */
	public void sayChannel(String channelName, String msg)
	{
		con.addWriteRequest(new WriteRequest(msg, con.getChannel(channelName), con));
	}

    /**
     * Send an action (aka /me) to a channel/user
     *
     * @param target
     * @param actionText
     */
    public void action(String target, String actionText) {
        if (con != null && isConnected()) {
            con.addWriteRequest(new WriteRequest("\001ACTION " + actionText + "\001", con, target));
        } else {
            new Exception().printStackTrace();
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return rCon.hashCode();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o instanceof Session && o.hashCode() == hashCode()) { return ((Session) o).getRequestedConnection().equals(rCon); }
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getNick()
	 */
	public String getNick()
	{
		return getRequestedConnection().getProfile().getActualNick();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#changeProfile(jerklib.Profile)
	 */
	public void changeProfile(Profile profile)
	{
		tmpProfile = profile;
		profileUpdating = true;
		con.changeNick(tmpProfile.getActualNick());
	}

	public void updateProfileSuccessfully(boolean success)
	{
		if (success)
		{
			((RequestedConnection) rCon).setProfile(tmpProfile);
		}
		tmpProfile = null;
		profileUpdating = false;
	}

	public boolean isProfileUpdating()
	{
		return profileUpdating;
	}

	public String getConnectedHostName()
	{
		return con.getHostName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#rawSay(java.lang.String)
	 */
	public void sayRaw(String data)
	{
		con.addWriteRequest(new WriteRequest(data, con));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#kick(java.lang.String, java.lang.String,
	 *      jerklib.Channel)
	 */
	public void kick(String userName, String reason, Channel channel)
	{
		// todo throw event
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to kick");
			return;
		}

		if (reason == null || reason.equals(""))
		{
			reason = getNick();
		}
		sayRaw("KICK " + channel.getName() + " " + userName + " :" + reason + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#op(java.lang.String, jerklib.Channel)
	 */
	public void op(String userName, Channel channel)
	{
		// todo throw event
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to op");
			return;
		}

		sayRaw("MODE " + channel.getName() + " +o " + userName + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#deop(java.lang.String, jerklib.Channel)
	 */
	public void deop(String userName, Channel channel)
	{
		// todo throw event
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to op");
			return;
		}

		sayRaw("MODE " + channel.getName() + " -o " + userName + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#voice(java.lang.String, jerklib.Channel)
	 */
	public void voice(String userName, Channel channel)
	{
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to voice");
			return;
		}

		sayRaw("MODE " + channel.getName() + " +v " + userName + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#deVoice(java.lang.String, jerklib.Channel)
	 */
	public void deVoice(String userName, Channel channel)
	{
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to devoice");
			return;
		}

		sayRaw("MODE " + channel.getName() + " -v " + userName + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#mode(java.lang.String, jerklib.Channel,
	 *      java.lang.String)
	 */
	public void mode(String userName, Channel channel, String mode)
	{
		if (!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to mode adjust");
			return;
		}

		sayRaw("MODE " + channel.getName() + " " + mode + " " + userName + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#mode(jerklib.Channel, java.lang.String)
	 */
	public void mode(Channel channel, String mode)
	{
		sayRaw("MODE " + channel.getName() + " " + mode + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#whois(java.lang.String)
	 */
	public void whois(String nick)
	{
		con.whois(nick);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see Session#setAway(java.lang.String)
	 */
	public void setAway(String message)
	{
		isAway = true;
		con.setAway(message);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see Session#unsetAway()
	 */
	public void unsetAway()
	{
		/* if we're not away let's not bother even delegating */
		if (isAway)
		{
			con.unSetAway();
			isAway = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#whowas(java.lang.String)
	 */
	public void whowas(String nick)
	{
		con.whoWas(nick);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#channelList()
	 */
	public void channelList()
	{
		con.chanList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#channelList(java.lang.String)
	 */
	public void channelList(String channel)
	{
		con.chanList(channel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#invite(java.lang.String, jerklib.Channel)
	 */
	public void invite(String nick, Channel chan)
	{
		con.invite(nick, chan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getServerVersion()
	 */
	public void getServerVersion()
	{
		con.getServerVersion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getServerVersion(java.lang.String)
	 */
	public void getServerVersion(String hostPattern)
	{
		con.getServerVersion(hostPattern);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see Session#who(java.lang.String)
	 * @param who
	 */
	public void who(String who)
	{
		con.who(who);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#addIRCEventListener(jerklib.events.listeners.IRCEventListener)
	 */
	public void addIRCEventListener(IRCEventListener listener)
	{
		listenerList.add(listener);
	}

	public void removeIRCEventListener(IRCEventListener listener)
	{
		listenerList.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jerklib.Session#getIRCEventListeners()
	 */
	public Collection<IRCEventListener> getIRCEventListeners()
	{
		return Collections.unmodifiableCollection(listenerList);
	}

	public void onEvent(Task task)
	{
		// null means task should be notified of all Events
		onEvent(task, null);
	}

	public void onEvent(Task task, Type type)
	{
		synchronized (taskMap)
		{
			if (!taskMap.containsKey(type))
			{
				List<Task> tasks = new ArrayList<Task>();
				tasks.add(task);
				taskMap.put(type, tasks);
			}
			else
			{
				if(!taskMap.get(type).contains(task))taskMap.get(type).add(task);
			}
		}
	}

	public void notice(String target, String message)
	{
		con.notice(target, message);
	}

	Map<Type, List<Task>> getTasks()
	{
		return new HashMap<Type, List<Task>>(taskMap);
	}

	public void removeTask(Task t)
	{
		synchronized (taskMap)
		{
			for(Iterator<Type> it = taskMap.keySet().iterator(); it.hasNext();)
			{
				List<Task> tasks = taskMap.get(it.next());
				if(tasks != null)
				{
					tasks.remove(t);
				}
			}
		}
	}

	
	void addChannelName(String name)
	{
		if (!
                channelNames.contains(name))
		{
			channelNames.add(name);
		}
	}

	Connection getConnection()
	{
		return con;
	}

	ConnectionState getConnectionState()
	{
		if (con == null) return null;
		else return con.getConnectionState();
	}

	long getLastRetry()
	{
		return lastRetry;
	}

	void retried()
	{
		lastRetry = System.currentTimeMillis();
	}

	void setConnectionState(State state)
	{
		if (con != null)
		{
			if(state == State.DISCONNECTED)
			{
				channelNames.clear();
			}
			con.setConnectionState(state);
		}
	}

}
