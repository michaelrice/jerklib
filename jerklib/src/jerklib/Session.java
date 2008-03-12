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
 * 
 */
public class Session extends RequestGenerator
{

	private final List<String> channelNames = new ArrayList<String>();
	private final List<IRCEventListener> listenerList = new ArrayList<IRCEventListener>();
	private final Map<Type, List<Task>> taskMap = new HashMap<Type, List<Task>>();
	private final RequestedConnection rCon;
	private Connection con;
	private boolean rejoinOnKick = true, profileUpdating, isAway;
	private Profile tmpProfile;
	private long lastRetry = -1, lastResponse = System.currentTimeMillis();
	private ServerInformation serverInfo = new ServerInformation();
	private State state = State.DISCONNECTED;

	public void sayChannel(Channel channel, String msg)
	{
		sayChannel(channel.getName(), msg);
	}

	enum State
	{
		CONNECTED, 
		CONNECTING, 
		HALF_CONNECTED, 
		DISCONNECTED, 
		MARKED_FOR_REMOVAL, 
		NEED_TO_PING, 
		PING_SENT, 
		NEED_TO_RECONNECT
	}

	Session(RequestedConnection rCon)
	{
		this.rCon = rCon;
	}

	/* general methods */

	public boolean isConnected()
	{
		return state == State.CONNECTED;
	}

	public boolean isRejoinOnKick()
	{
		return rejoinOnKick;
	}

	public void setRejoinOnKick(boolean rejoin)
	{
		rejoinOnKick = rejoin;
	}

	public void close(String quitMessage)
	{
		if (con != null)
		{
			con.quit(quitMessage);
		}
	}

	public String getNick()
	{
		return getRequestedConnection().getProfile().getActualNick();
	}

	public void changeProfile(Profile profile)
	{
		tmpProfile = profile;
		profileUpdating = true;
		super.changeNick(tmpProfile.getActualNick());
	}

	public boolean isProfileUpdating()
	{
		return profileUpdating;
	}

	public void kick(String userName, String reason, Channel channel)
	{
		if (reason == null || reason.length() == 0)
		{
			reason = getNick();
		}
		super.kick(userName, reason, channel);
	}

	public boolean isAway()
	{
		return isAway;
	}

	public void setAway(String message)
	{
		isAway = true;
		super.setAway(message);
	}

	public void unsetAway()
	{
		/* if we're not away let's not bother even delegating */
		if (isAway)
		{
			super.unSetAway();
			isAway = false;
		}
	}

	/* methods to get information about connection and server */

	public ServerInformation getServerInformation()
	{
		return serverInfo;
	}

	public RequestedConnection getRequestedConnection()
	{
		return rCon;
	}

	public String getConnectedHostName()
	{
		return con.getHostName();
	}

	/* methods for adding/removing IRCEventListeners and Tasks */

	public void addIRCEventListener(IRCEventListener listener)
	{
		listenerList.add(listener);
	}

	public void removeIRCEventListener(IRCEventListener listener)
	{
		listenerList.remove(listener);
	}

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
				if (!taskMap.get(type).contains(task)) taskMap.get(type).add(task);
			}
		}
	}

	Map<Type, List<Task>> getTasks()
	{
		return new HashMap<Type, List<Task>>(taskMap);
	}

	public void removeTask(Task t)
	{
		synchronized (taskMap)
		{
			for (Iterator<Type> it = taskMap.keySet().iterator(); it.hasNext();)
			{
				List<Task> tasks = taskMap.get(it.next());
				if (tasks != null)
				{
					tasks.remove(t);
				}
			}
		}
	}

	void updateProfileSuccessfully(boolean success)
	{
		if (success)
		{
			rCon.setProfile(tmpProfile);
		}
		tmpProfile = null;
		profileUpdating = false;
	}

	/* Channel methods */

	public Collection<Channel> getChannels()
	{
		if (con != null) { return con.getChannels(); }
		return new ArrayList<Channel>();
	}

	public Channel getChannel(String channelName)
	{
		if (con != null) { return con.getChannel(channelName); }
		return null;
	}

	public List<String> getChannelNames()
	{
		return Collections.unmodifiableList(channelNames);
	}

	void addChannelName(String name)
	{
		if (!channelNames.contains(name))
		{
			channelNames.add(name);
		}
	}

	void removeChannelName(String name)
	{
		channelNames.remove(name);
	}

	/* methods to track connection attempts */

	long getLastRetry()
	{
		return lastRetry;
	}

	void retried()
	{
		lastRetry = System.currentTimeMillis();
	}

	/* methods to get/set Connection object */
	void setConnection(Connection con)
	{
		this.con = con;
		super.setConnection(con);
	}

	Connection getConnection()
	{
		return con;
	}

	/* Methods to get and set the state of the session */

	void gotResponse()
	{
		lastResponse = System.currentTimeMillis();
		state = State.CONNECTED;
	}

	void pingSent()
	{
		state = State.PING_SENT;
	}

	void disconnected()
	{
		state = State.DISCONNECTED;
		if (con != null)
		{
			con.quit("");
			con = null;
		}
		channelNames.clear();
	}

	void connected()
	{
		gotResponse();
	}

	void connecting()
	{
		state = State.CONNECTING;
	}

	void halfConnected()
	{
		state = State.HALF_CONNECTED;
	}

	void markForRemoval()
	{
		state = State.MARKED_FOR_REMOVAL;
	}

	State getState()
	{
		long current = System.currentTimeMillis();

		if (current - lastResponse > 300000 && state == State.NEED_TO_PING)
		{
			state = State.NEED_TO_RECONNECT;
		}
		else if (current - lastResponse > 200000 && state != State.PING_SENT)
		{
			state = State.NEED_TO_PING;
		}

		return state;
	}

	public int hashCode()
	{
		return rCon.hashCode();
	}

	public boolean equals(Object o)
	{
		if (o instanceof Session && o.hashCode() == hashCode()) { return ((Session) o).getRequestedConnection().equals(rCon); }
		return false;
	}

}
