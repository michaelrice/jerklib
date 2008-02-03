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

	@Override
	public Session getSession()
	{
		return session;
	}

	@Override
	public void addChannelName(String name)
	{
		List<String>names = ((SessionImpl)session).channelNames;
		if(!names.contains(name))
		{
			names.add(name);
		}
	}

	@Override
	public List<String> getChannelNames()
	{
		return session.getChannelNames();
	}

	@Override
	public Connection getConnection()
	{
		return con;
	}

	@Override
	public ConnectionState getConnectionState()
	{
		if(con == null) return null;
		else return con.getConnectionState();
	}

	@Override
	public long getLastRetry()
	{
		return lastRetry;
	}

	@Override
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

	@Override
	public void retried()
	{
		lastRetry = System.currentTimeMillis();
	}

	@Override
	public void setConnection(Connection con)
	{
		this.con = con;
		((SessionImpl)session).setConnection(con);
	}

	@Override
	public void setConnectionState(State state)
	{
		if(con != null) con.setConnectionState(state);
	}

	@Override
	public void close(String quitMessage)
	{
		session.close(quitMessage);
	}

	@Override
	public Channel getChannel(String channelName)
	{
		return session.getChannel(channelName);
	}

	@Override
	public Collection<Channel> getChannels()
	{
		return session.getChannels();
	}

	@Override
	public RequestedConnection getRequestedConnection()
	{
		return session.getRequestedConnection();
	}

	@Override
	public boolean isConnected()
	{
		return session.isConnected();
	}

	@Override
	public boolean isRejoinOnKick()
	{
		return session.isRejoinOnKick();
	}

	@Override
	public boolean isRejoinOnReconnect()
	{
		return session.isRejoinOnReconnect();
	}

	@Override
	public void joinChannel(String channelName)
	{
		session.joinChannel(channelName);
	}

	@Override
	public void joinChannel(String channelName, String pass)
	{
		session.joinChannel(channelName, pass);
	}

	@Override
	public boolean partChannel(Channel channel , String msg)
	{
		return session.partChannel(channel , msg);
	}

	@Override
	public boolean partChannel(String channelName , String msg)
	{
		return session.partChannel(channelName , msg);
	}

	@Override
	public void sayPrivate(String nick, String msg)
	{
		session.sayPrivate(nick, msg);
	}

	@Override
	public void setAway(String message)
	{
		session.setAway(message);
	}
	
	@Override
	public void unsetAway()
	{
		session.unsetAway();
	}
	
	@Override
	public void channelSay(String channelName, String msg)
	{
		session.channelSay(channelName, msg);
	}
	
	@Override
	public void setRejoinOnKick(boolean rejoin)
	{
		session.setRejoinOnKick(rejoin);
	}
	
	@Override
	public void setRejoinOnReconnect(boolean rejoin)
	{
		session.setRejoinOnReconnect(rejoin);
	}

	@Override
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


	@Override
	public void changeProfile(Profile profile)
	{
		session.changeProfile(profile);
	}


	@Override
	public String getNick()
	{
		return session.getNick();
	}


	@Override
	public void updateProfileSuccessfully(boolean success)
	{
		session.updateProfileSuccessfully(success);
	}


	@Override
	public boolean isProfileUpdating()
	{
		return session.isProfileUpdating();
	}


	@Override
	public String getConnectedHostName()
	{
		return session.getConnectedHostName();
	}


	@Override
	public void rawSay(String data)
	{
		session.rawSay(data);

	}

	@Override
	public void kick(String userName, String reason, Channel channel)
	{
		session.kick(userName, reason, channel);
	}

	@Override
	public void addIRCEventListener(IRCEventListener listener)
	{
		session.addIRCEventListener(listener);
	}
	
	@Override
	public void removeIRCEventListener(IRCEventListener listener)
	{
		session.removeIRCEventListener(listener);
	}

	@Override
	public Collection<IRCEventListener> getIRCEventListeners()
	{
		return session.getIRCEventListeners();
	}

	@Override
	public void op(String userName, Channel channel)
	{

		session.op(userName, channel);
	}

	@Override
	public void deop(String userName, Channel channel)
	{
		session.deop(userName, channel);
	}

	@Override
	public void voice(String userName, Channel channel)
	{
		session.voice(userName, channel);
	}

	@Override
	public void deVoice(String userName, Channel channel)
	{
		session.deVoice(userName, channel);
	}

	@Override
	public void whois(String nick)
	{
		session.whois(nick);
	}

	@Override
	public void whowas(String nick)
	{
		session.whowas(nick);
	}

	@Override
	public void who(String who) 
	{
		session.who(who);
   }

   @Override
	public void mode(Channel channel, String mode)
	{
		session.mode(channel, mode);
	}

	@Override
	public void mode(String userName, Channel channel, String mode)
	{
		session.mode(userName, channel, mode);
	}


	@Override
	public void invite(String nick, Channel chan)
	{
		session.invite(nick, chan);
	}

	@Override
	public void channelList()
	{
		session.channelList();
	}


	@Override
	public void channelList(String channel)
	{
		session.channelList(channel);
	}


	@Override
	public void getServerVersion()
	{
		session.getServerVersion();
	}


	@Override
	public void getServerVersion(String hostPattern)
	{
		session.getServerVersion(hostPattern);
	}

	@Override
	public void onEvent(Task task)
	{
		session.onEvent(task);
	}
	
	@Override
	public void onEvent(Task task, Type type)
	{
		session.onEvent(task, type);
	}
	
	Map<Type, List<Task>> getTasks()
	{
		return ((SessionImpl)session).getTasks();
	}
}



