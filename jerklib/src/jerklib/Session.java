package jerklib;

import java.util.Collection;
import java.util.List;

import jerklib.events.listeners.IRCEventListener;

/**
 * 
 * Session is the main way to interact with the connected server
 * Add IRCEventListeners to Session to be notified of all incoming events
 * from server
 * 
 * @author Mohadib
 * @see IRCEventListener
 * @see Channel
 */
public interface Session extends ProfileUpdateable
{
	
	/**
	 * Enum to represent connection state
	 * @author mohadib
	 *
	 */
	enum State
	{
		CONNECTED,
		CONNECTING,
		HALF_CONNECTED,
		DISCONNECTED
	}

	
	/**
	 * 
	 * @return true is connected to server , else false
	 */
	public boolean isConnected();
	
	/**
	 * @return the RequestedConnection object for this Session
	 * @see RequestedConnection
	 */
	public RequestedConnection getRequestedConnection();
	
	/**
	 * @return host name of server this Session is connected to
	 */
	public String getConnectedHostName();

	/**
	 * Disconnects from server and closes session
	 * @param quitMessage quit message
	 */
	public void close(String quitMessage);
	
	
	/**
	 * @return true if should rejoin channles kicked from , else false
	 */
	public boolean isRejoinOnKick();
	
	/**
	 * @return true if should rejoin channels on reconnect , else false
	 */
	public boolean isRejoinOnReconnect();
	
	/**
	 * Sets whether channels should be rejoined when kicked
	 * @param rejoin true if should rejoin , else false
	 */
	public void setRejoinOnKick(boolean rejoin);
	
	/**
	 * Sets weather channels should be rejoined on reconnect
	 * @param rejoin true if should rejoin else false
	 */
	public void setRejoinOnReconnect(boolean rejoin);
	
	
	/**
	 * Gets a Channel that is currently joined by name
	 * @param channelName Name of Channel to get
	 * @return Channel
	 * @see Channel
	 */
	public Channel getChannel(String channelName);
	
	/**
	 * Gets a collection of all channels joined on this Sessions connection
	 * @return Collection of channels
	 * @see Session#getChannelNames()
	 * @see Collection
	 * @see Channel
	 */
	public Collection<Channel> getChannels();

	/**
	 * Gets a list of channel names of currently joined channels
	 * @return List of channel names
	 * @see Session#getChannels()
	 */
	public List<String> getChannelNames();
	
	/**
	 * Joins a channel
	 * @param channelName channel name to join
	 */
	public void joinChannel(String channelName);
	
	/**
	 * Joins a password protected channel
	 * @param channelName channel name to join
	 * @param pass password to use
	 * @see Channel
	 */
	public void joinChannel(String channelName , String pass);
	
	/**
	 * Parts a currently join channel
	 * @param channel Channel to part
	 * @param msg Msg to part with
	 * @return true if Channel was parted , else false
	 * @see Channel
	 */
	public boolean partChannel(Channel channel , String msg);
	
	/**
	 * Parts a currently join channel
	 * @param channelName channel to part
	 * @param msg msg Msg to part with
	 * @return true if Channel was parted , else false
	 * @see Channel
	 */
	public boolean partChannel(String channelName , String msg);
	
	/**
	 * Kicks someone from a channel
	 * 
	 * @param nick user to kick
	 * @param reason reason for kick
	 * @param channel Channel to kick user from
	 * @see Channel
	 */
	public void kick(String nick , String reason , Channel channel);
	
	/**
	 * Ops a user
	 * @param nick user to op
	 * @param channel Channel to op user in
	 * @see Channel
	 */
	public void op(String nick , Channel channel);
	
	/** deops a user
	 * @param nick user to deop
	 * @param channel Channel to deop user in
	 * @see Channel
	 */
	public void deop(String nick , Channel channel);
	
	/**
	 * Voice a user
	 * @param nick user to voice
	 * @param channel Channel to voice user in
	 * @see Channel
	 */
	public void voice(String nick , Channel channel);
	
	/**
	 * devoice a user
	 * @param nick user to devoice
	 * @param channel Channel to devoice user in
	 * @see Channel
	 */
	public void deVoice(String nick , Channel channel);
	
	/**
	 * sets a user mode
	 * 
	 * @param nick  user to set mode on
	 * @param channel Channel to set mode in
	 * @param mode mode to set
	 */
	public void mode(String nick , Channel channel , String mode);
	
	/**
	 * Sets a channel mode
	 * @param channel Channel to use
	 * @param mode mode to set
	 */
	public void mode(Channel channel , String mode);
	
	/**
	 * Send a raw string to the server - dont forget the "\r\n"
	 * @param data raw string to send
	 */
	public void rawSay(String data);
	
	/**
	 * Sends a private message to a user
	 * @param nick user to send message
	 * @param msg message to send
	 */
	public void sayPrivate(String nick , String msg);
	
	/**
	 * sends a message to a Channel
	 * @param channelName Name of channel to send to
	 * @param msg msg to send
	 * @see Channel#say(String)
	 */
	public void channelSay(String channelName , String msg);

	/**
	 * Change the profile - Change nick
	 * @param profile Profile to use
	 * @see Profile
	 * @see ProfileImpl
	 */
	public void changeProfile(Profile profile);
	
	/**
	 * @return nick used for Session
	 */
	public String getNick();
	
	/**
	 * Sends a whois query 
	 * @param nick Nick to whois
	 */
	public void whois(String nick);
	
	/**
	 * Send a whowas query
	 * @param nick nick to whowas
	 */
	public void whowas(String nick);
	
	/**
	 * Invite a user to a channel
	 * @param nick user to invite
	 * @param chan channel to invite user to 
	 */
	public void invite(String nick , Channel chan);
	
	/**
	 * List of channels on server - use with caution
	 */
	public void channelList();
	
	/**
	 * List information about a specific channel
	 * @param channel Name of Channel to get information about
	 */
	public void channelList(String channel);
	
	/**
	 * Gets the server version string
	 */
	public void getServerVersion();
	
	
	/**
	 * Gets the server version string of a server matching the pattern
	 * ex. *.gov would match a irc server from the .gov TLD
	 * @param hostPattern pattern of hostname
	 */
	public void getServerVersion(String hostPattern);
	
	/**
	 * Adds an event listener to be notified of all incoming events for Session
	 * @param listener Listener to notify
	 * @see IRCEventListener
	 */
	public void addIRCEventListener(IRCEventListener listener);
	
	/**
	 * Returns the list of IRCEventListeners registered with this Session
	 * @return List of listeners
	 * @see IRCEventListener
	 */
	public Collection<IRCEventListener> getIRCEventListeners();

    /**
     * Set yourself away.
     * @see Connection#setAway(java.lang.String)
     * @param message
     */
    public void setAway(String message);

    /**
     * unset yourself away.
     * @see Connection#unSetAway()
     */
    public void unsetAway();

    /**
     * Get the last away message used.     *
     * @return the away message
     */
    public String getPreviousAwayMsg(); 
	
}
