package jerklib;

import java.util.Collection;
import java.util.List;

import jerklib.events.listeners.IRCEventListener;




public interface Session extends ProfileUpdateable
{
	
	enum State
	{
		CONNECTED,
		CONNECTING,
		HALF_CONNECTED,
		DISCONNECTED
	}

	
	public boolean isConnected();
	
	public RequestedConnection getRequestedConnection();
	
	public String getConnectedHostName();

	public void close(String quitMessage);
	
	
	public boolean isRejoinOnKick();
	
	public boolean isRejoinOnReconnect();
	
	public void setRejoinOnKick(boolean rejoin);
	
	public void setRejoinOnReconnect(boolean rejoin);
	
	
	public Channel getChannel(String channelName);
	
	public Collection<Channel> getChannels();

	public List<String> getChannelNames();
	
	public void joinChannel(String channelName);
	
	public boolean partChannel(Channel channel , String msg);
	
	public boolean partChannel(String channelName , String msg);
	
	public void kick(String userName , String reason , Channel channel);
	
	public void op(String userName , Channel channel);
	
	public void deop(String userName , Channel channel);
	
	public void voice(String userName , Channel channel);
	
	public void deVoice(String userName , Channel channel);
	
	public void mode(String userName , Channel channel , String mode);
	
	public void mode(Channel channel , String mode);
	
	public void rawSay(String data);
	
	public void sayPrivate(String nick , String msg);
	
	public void channelSay(String channelName , String msg);

	public void changeProfile(Profile profile);
	
	public String getNick();
	
	public void whois(String nick);
	
	public void addIRCEventListener(IRCEventListener listener);
	
	public Collection<IRCEventListener> getIRCEventListeners();
	
}
