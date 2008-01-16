package jerklib;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jerklib.events.listeners.IRCEventListener;


public class SessionImpl implements Session
{


	final List<String> channelNames = new ArrayList<String>();

	private boolean rejoinOnKick = true, rejoinOnConnect = true;

	private Connection con;

	private final RequestedConnection rCon;

	private Profile tmpProfile; 
	
	private boolean profileUpdating;
	
	private final List<IRCEventListener> listenerList = new ArrayList<IRCEventListener>(); 
	
	SessionImpl(RequestedConnection rCon)
	{
		this.rCon = rCon;
	}

	void setConnection(Connection con)
	{
		this.con = con;
	}



	public RequestedConnection getRequestedConnection()
	{
		return rCon;
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

	public Collection<Channel> getChannels()
	{
		if (con != null) { return con.getChannels(); }
		return new ArrayList<Channel>();
	}

	public boolean isConnected()
	{
		return con != null;
	}

	public boolean isRejoinOnKick()
	{
		return rejoinOnKick;
	}

	public boolean isRejoinOnReconnect()
	{
		return rejoinOnConnect;
	}

	public void setRejoinOnKick(boolean rejoin)
	{
		rejoinOnKick = rejoin;
	}

	public void setRejoinOnReconnect(boolean rejoin)
	{
		rejoinOnConnect = rejoin;
	}

	public void joinChannel(String channelName)
	{
		if (!channelNames.contains(channelName))
		{
			channelNames.add(channelName);
			if (con != null)
			{
				con.join(channelName);
			}
		}
	}

	public boolean partChannel(Channel channel, String msg)
	{
		return partChannel(channel.getName(), msg);
	}

	public boolean partChannel(String channelName, String msg)
	{
		boolean removed = channelNames.remove(channelName);
		if (con != null)
		{
			con.part(channelName, msg);
		}
		return removed;
	}

	public void close(String quitMessage)
	{
		if (con != null && channelNames.size() > 0)
		{
			con.quit(quitMessage);
		}
	}

	public void sayPrivate(String nick, String msg)
	{
		if (con != null && isConnected())
		{
			con.addWriteRequest(new WriteRequestImpl(msg, con, nick));
		}
		else
		{
			new Exception().printStackTrace();
		}
	}

	public void channelSay(String channelName, String msg)
	{
		if (con == null)
		{
			con.addWriteRequest(new WriteRequestImpl(msg, channelName, rCon.getHostName()));
		}
		else
		{
			con.addWriteRequest(new WriteRequestImpl(msg, con.getChannel(channelName), con));
		}
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


	@Override
	public String getNick() 
	{
		return getRequestedConnection().getProfile().getActualNick();
	}

	@Override
	public void changeProfile(Profile profile) 
	{
		tmpProfile = profile;
		profileUpdating = true;
		con.changeNick(tmpProfile.getActualNick());
	}
	
	@Override
	public void updateProfileSuccessfully(boolean success)
	{
		if(success)
		{
			((RequestedConnectionImpl)rCon).setProfile(tmpProfile);
		}
		else
		{
			System.out.println("UPDATED PROFILE FAILEDDDD!!");
		}
		tmpProfile = null;
		profileUpdating = false;
	}
	
	@Override
	public boolean isProfileUpdating() 
	{
		return profileUpdating;
	}

	@Override
	public String getConnectedHostName() 
	{
		return con.getHostName();
	}

	@Override
	public void rawSay(String data) 
	{
		con.addWriteRequest(new WriteRequestImpl(data , con));
	}
	
	@Override
	public void kick(String userName, String reason , Channel channel)
	{
		//todo throw event
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to kick");
			return;
		}
		
		if(reason == null || reason.equals(""))
		{
			reason = getNick();
		}
		rawSay("KICK " + channel.getName() + " " + userName + " :" + reason + "\r\n");
	}
	
	@Override
	public void op(String userName, Channel channel) 
	{
		//todo throw event
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to op");
			return;
		}
		
		rawSay("MODE " + channel.getName() + " +o " + userName + "\r\n");
	}
	
	@Override
	public void deop(String userName, Channel channel) 
	{
		//todo throw event
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to op");
			return;
		}
		
		rawSay("MODE " + channel.getName() + " -o " + userName + "\r\n");
	}
	
	@Override
	public void voice(String userName, Channel channel) 
	{
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to voice");
			return;
		}
		
		rawSay("MODE " + channel.getName() + " +v " + userName + "\r\n");
	}
	
	
	@Override
	public void deVoice(String userName, Channel channel) 
	{
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to devoice");
			return;
		}
		
		rawSay("MODE " + channel.getName() + " -v " + userName + "\r\n");
	}
	
	@Override
	public void mode(String userName, Channel channel, String mode) 
	{
		if(!channel.getNicks().contains(userName))
		{
			System.err.println("No such user to mode adjust");
			return;
		}
		
		rawSay("MODE " + channel.getName() + " " + mode + " " + userName + "\r\n");
	}
	
	@Override
	public void mode(Channel channel, String mode) 
	{
		rawSay("MODE " + channel.getName() + " " + mode + "\r\n");
	}
	
	public void whois(String nick)
	{
		con.whois(nick);
	}
	
	@Override
	public void addIRCEventListener(IRCEventListener listener) 
	{
		listenerList.add(listener);
	}
	
	@Override
	public Collection<IRCEventListener> getIRCEventListeners() 
	{
		return Collections.unmodifiableCollection(listenerList);
	}
	
}
