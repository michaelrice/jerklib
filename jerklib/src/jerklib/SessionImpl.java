package jerklib;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jerklib.events.listeners.IRCEventListener;


/**
 * @author mohadib
 * @see Session
 *
 */
public class SessionImpl implements Session
{


	final List<String> channelNames = new ArrayList<String>();
	private boolean rejoinOnKick = true, rejoinOnConnect = true;
	private Connection con;
	private final RequestedConnection rCon;
	private Profile tmpProfile; 
	private boolean profileUpdating;
    private boolean isAway;
    private final List<IRCEventListener> listenerList = new ArrayList<IRCEventListener>();

	
	SessionImpl(RequestedConnection rCon)
	{
		this.rCon = rCon;
    }

	void setConnection(Connection con)
	{
		this.con = con;
	}



	/* (non-Javadoc)
	 * @see jerklib.Session#getRequestedConnection()
	 */
	public RequestedConnection getRequestedConnection()
	{
		return rCon;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#getChannel(java.lang.String)
	 */
	public Channel getChannel(String channelName)
	{
		if (con != null) { return con.getChannel(channelName); }
		return null;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#getChannelNames()
	 */
	public List<String> getChannelNames()
	{
		return Collections.unmodifiableList(channelNames);
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#getChannels()
	 */
	public Collection<Channel> getChannels()
	{
		if (con != null) { return con.getChannels(); }
		return new ArrayList<Channel>();
	}

    public boolean isAway() {
        return isAway;
    }

    

    /* (non-Javadoc)
	 * @see jerklib.Session#isConnected()
	 */
	public boolean isConnected()
	{
		return con != null;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#isRejoinOnKick()
	 */
	public boolean isRejoinOnKick()
	{
		return rejoinOnKick;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#isRejoinOnReconnect()
	 */
	public boolean isRejoinOnReconnect()
	{
		return rejoinOnConnect;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#setRejoinOnKick(boolean)
	 */
	public void setRejoinOnKick(boolean rejoin)
	{
		rejoinOnKick = rejoin;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#setRejoinOnReconnect(boolean)
	 */
	public void setRejoinOnReconnect(boolean rejoin)
	{
		rejoinOnConnect = rejoin;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#joinChannel(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see jerklib.Session#joinChannel(java.lang.String, java.lang.String)
	 */
	@Override
	public void joinChannel(String channelName, String pass)
	{
    if (!channelNames.contains(channelName))
		{
			channelNames.add(channelName);
			if (con != null)
			{
				con.join(channelName , pass);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#partChannel(jerklib.Channel, java.lang.String)
	 */
	public boolean partChannel(Channel channel, String msg)
	{
		return partChannel(channel.getName(), msg);
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#partChannel(java.lang.String, java.lang.String)
	 */
	public boolean partChannel(String channelName, String msg)
	{
		boolean removed = channelNames.remove(channelName);
		if (con != null)
		{
			con.part(channelName, msg);
		}
		return removed;
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#close(java.lang.String)
	 */
	public void close(String quitMessage)
	{
		if (con != null && channelNames.size() > 0)
		{
			con.quit(quitMessage);
		}
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#sayPrivate(java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see jerklib.Session#channelSay(java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return rCon.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o instanceof Session && o.hashCode() == hashCode()) { return ((Session) o).getRequestedConnection().equals(rCon); }
		return false;
	}


	/* (non-Javadoc)
	 * @see jerklib.Session#getNick()
	 */
	@Override
	public String getNick() 
	{
		return getRequestedConnection().getProfile().getActualNick();
	}

	/* (non-Javadoc)
	 * @see jerklib.Session#changeProfile(jerklib.Profile)
	 */
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

	/* (non-Javadoc)
	 * @see jerklib.Session#rawSay(java.lang.String)
	 */
	@Override
	public void rawSay(String data) 
	{
		con.addWriteRequest(new WriteRequestImpl(data , con));
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#kick(java.lang.String, java.lang.String, jerklib.Channel)
	 */
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
	
	/* (non-Javadoc)
	 * @see jerklib.Session#op(java.lang.String, jerklib.Channel)
	 */
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
	
	/* (non-Javadoc)
	 * @see jerklib.Session#deop(java.lang.String, jerklib.Channel)
	 */
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
	
	/* (non-Javadoc)
	 * @see jerklib.Session#voice(java.lang.String, jerklib.Channel)
	 */
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
	
	
	/* (non-Javadoc)
	 * @see jerklib.Session#deVoice(java.lang.String, jerklib.Channel)
	 */
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
	
	/* (non-Javadoc)
	 * @see jerklib.Session#mode(java.lang.String, jerklib.Channel, java.lang.String)
	 */
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
	
	/* (non-Javadoc)
	 * @see jerklib.Session#mode(jerklib.Channel, java.lang.String)
	 */
	@Override
	public void mode(Channel channel, String mode) 
	{
		rawSay("MODE " + channel.getName() + " " + mode + "\r\n");
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#whois(java.lang.String)
	 */
	public void whois(String nick)
	{
		con.whois(nick);
	}

    /**
     * (non-Javadoc)
     * @see Session#setAway(java.lang.String)
     */
    @Override
    public void setAway(String message) {    
    	isAway = true;                
        con.setAway(message);
    }

    /**
     * (non-Javadoc)
     * @see Session#unsetAway()
     */
    @Override
    public void unsetAway() {
        /* if we're not away let's not bother even delegating */
        if(isAway) {
            con.unSetAway();
            isAway = false;
        }
    }

    /* (non-Javadoc)
	 * @see jerklib.Session#whowas(java.lang.String)
	 */
	public void whowas(String nick)
	{
		con.whoWas(nick);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#channelList()
	 */
	@Override
	public void channelList()
	{
		con.chanList();
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#channelList(java.lang.String)
	 */
	@Override
	public void channelList(String channel)
	{
		con.chanList(channel);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#invite(java.lang.String, jerklib.Channel)
	 */
	@Override
	public void invite(String nick, Channel chan)
	{
		con.invite(nick, chan);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#getServerVersion()
	 */
	@Override
	public void getServerVersion()
	{
		con.getServerVersion();
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#getServerVersion(java.lang.String)
	 */
	@Override
	public void getServerVersion(String hostPattern)
	{
		con.getServerVersion(hostPattern);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#addIRCEventListener(jerklib.events.listeners.IRCEventListener)
	 */
	@Override
	public void addIRCEventListener(IRCEventListener listener) 
	{
		listenerList.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Session#getIRCEventListeners()
	 */
	@Override
	public Collection<IRCEventListener> getIRCEventListeners() 
	{
		return Collections.unmodifiableCollection(listenerList);
	}

}
