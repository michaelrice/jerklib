package jerklib;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.Task;



/**
 * (non-Javadoc)
 * @see InternalSession
 * @author mohadib
 *
 *
 */
class InternalSessionImpl implements InternalSession
{

	private final  Session session;
	private Connection con;
	private long lastRetry = -1;

	InternalSessionImpl(Session session)
	{
		this.session = session;
	}

	public Session getSession()
	{
		return session;
	}

	public void addChannelName(String name)
	{
		List<String>names = ((SessionImpl)session).channelNames;
		if(!names.contains(name))
		{
			names.add(name);
		}
	}

	public List<String> getChannelNames()
	{
		return session.getChannelNames();
	}

	public Connection getConnection()
	{
		return con;
	}

	public ConnectionState getConnectionState()
	{
		if(con == null) return null;
		else return con.getConnectionState();
	}

	public long getLastRetry()
	{
		return lastRetry;
	}

	public void rejoinChannels()
	{
		if(con != null && session.isRejoinOnReconnect())
		{
			for(String name : session.getChannelNames())
			{
				con.join(name);
			}
		}
	}

	public void retried()
	{
		lastRetry = System.currentTimeMillis();
	}

	public void setConnection(Connection con)
	{
		this.con = con;
		((SessionImpl)session).setConnection(con);
	}

	public void setConnectionState(State state)
	{
		if(con != null) con.setConnectionState(state);
	}

	public void close(String quitMessage)
	{
		session.close(quitMessage);
	}

	public Channel getChannel(String channelName)
	{
		return session.getChannel(channelName);
	}

	public Collection<Channel> getChannels()
	{
		return session.getChannels();
	}

	public RequestedConnection getRequestedConnection()
	{
		return session.getRequestedConnection();
	}

	public boolean isConnected()
	{
		return session.isConnected();
	}

	public boolean isRejoinOnKick()
	{
		return session.isRejoinOnKick();
	}

	public boolean isRejoinOnReconnect()
	{
		return session.isRejoinOnReconnect();
	}

	public void joinChannel(String channelName)
	{
		session.joinChannel(channelName);
	}

	public void joinChannel(String channelName, String pass)
	{
		session.joinChannel(channelName, pass);
	}

	public boolean partChannel(Channel channel , String msg)
	{
		return session.partChannel(channel , msg);
	}

	public boolean partChannel(String channelName , String msg)
	{
		return session.partChannel(channelName , msg);
	}

	public void sayPrivate(String nick, String msg)
	{
		session.sayPrivate(nick, msg);
	}

	public void setAway(String message)
	{
		session.setAway(message);
	}

	public void unsetAway()
	{
		session.unsetAway();
	}

	public void channelSay(String channelName, String msg)
	{
		session.channelSay(channelName, msg);
	}

	public void setRejoinOnKick(boolean rejoin)
	{
		session.setRejoinOnKick(rejoin);
	}

	public void setRejoinOnReconnect(boolean rejoin)
	{
		session.setRejoinOnReconnect(rejoin);
	}

	public int hashCode()
	{
		return session.getRequestedConnection().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof InternalSession && o.hashCode() == hashCode())
		{
			return ((InternalSession) o).getRequestedConnection().equals(session.getRequestedConnection());
		}
		return false;
	}


	public void changeProfile(Profile profile)
	{
		session.changeProfile(profile);
	}


	public String getNick()
	{
		return session.getNick();
	}


	public void updateProfileSuccessfully(boolean success)
	{
		session.updateProfileSuccessfully(success);
	}


	public boolean isProfileUpdating()
	{
		return session.isProfileUpdating();
	}


	public String getConnectedHostName()
	{
		return session.getConnectedHostName();
	}


	public void rawSay(String data)
	{
		session.rawSay(data);

	}

	public void kick(String userName, String reason, Channel channel)
	{
		session.kick(userName, reason, channel);
	}

	public void addIRCEventListener(IRCEventListener listener)
	{
		session.addIRCEventListener(listener);
	}

	public void removeIRCEventListener(IRCEventListener listener)
	{
		session.removeIRCEventListener(listener);
	}

	public Collection<IRCEventListener> getIRCEventListeners()
	{
		return session.getIRCEventListeners();
	}

	public void op(String userName, Channel channel)
	{

		session.op(userName, channel);
	}

	public void deop(String userName, Channel channel)
	{
		session.deop(userName, channel);
	}

	public void voice(String userName, Channel channel)
	{
		session.voice(userName, channel);
	}

	public void deVoice(String userName, Channel channel)
	{
		session.deVoice(userName, channel);
	}

	public void whois(String nick)
	{
		session.whois(nick);
	}

	public void whowas(String nick)
	{
		session.whowas(nick);
	}

	public void who(String who)
	{
		session.who(who);
   }

	public void mode(Channel channel, String mode)
	{
		session.mode(channel, mode);
	}

	public void mode(String userName, Channel channel, String mode)
	{
		session.mode(userName, channel, mode);
	}


	public void invite(String nick, Channel chan)
	{
		session.invite(nick, chan);
	}

	public void channelList()
	{
		session.channelList();
	}


	public void channelList(String channel)
	{
		session.channelList(channel);
	}


	public void getServerVersion()
	{
		session.getServerVersion();
	}


	public void getServerVersion(String hostPattern)
	{
		session.getServerVersion(hostPattern);
	}

	public void onEvent(Task task)
	{
		session.onEvent(task);
	}

	public void onEvent(Task task, Type type)
	{
		session.onEvent(task, type);
	}

	Map<Type, List<Task>> getTasks()
	{
		return ((SessionImpl)session).getTasks();
	}
}



