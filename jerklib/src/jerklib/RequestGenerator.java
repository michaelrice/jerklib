package jerklib;

/**
 * A class for writing Session level events. 
 * By that I mean not Channel level stuff , though
 * some methods can be passed a Channel or channel name as a target.
 * 
 * @author mohadib
 *
 */
class RequestGenerator
{
	private Connection con;

	/**
	 * Sets the Connection to use
	 *  
	 * @param con
	 */
	void setConnection(Connection con)
	{
		this.con = con;
	}

	/**
	 * Send Who request 
	 * 
	 * @param who
	 */
	public void who(String who)
	{
		con.addWriteRequest(new WriteRequest("WHO " + who, con));
	}
	
	/**
	 * Send a whois query
	 * 
	 * @param nick - target of whois
	 */
	public void whois(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOIS " + nick, con));
	}

	/**
	 * Send WhoWas query
	 * @param nick
	 */
	public void whoWas(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOWAS " + nick, con));
	}
	
	/**
	 * Invite a user to a channel
	 * 
	 * @param nick
	 * @param chan
	 */
	public void invite(String nick, Channel chan)
	{
		con.addWriteRequest(new WriteRequest("INVITE " + nick + " " + chan.getName(), con));
	}

	/**
	 * Get a List of Channels from server.
	 */
	public void chanList()
	{
		con.addWriteRequest(new WriteRequest("LIST", con));
	}

	
	/**
	 * Get information on a secific channel
	 * 
	 * @param channel
	 */
	public void chanList(String channel)
	{
		con.addWriteRequest(new WriteRequest("LIST " + channel, con));
	}

	/**
	 * Join a Channel
	 * 
	 * @param channel
	 */
	public void join(String channel)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel, con));
		}
	}
	
	/**
	 * Join a password protected Channel
	 * 
	 * @param channel
	 * @param pass
	 */
	public void join(String channel, String pass)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel + " " + pass, con));
		}
	}
	
	/**
	 * Send a ctcp request
	 * @param target
	 * @param request
	 */
	public void ctcp(String target, String request)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("\001" + request.toUpperCase() + "\001", con, target));
		}
	}

	
	/**
	 * Send a notice
	 * 
	 * @param target
	 * @param msg
	 */
	public void notice(String target, String msg)
	{
		con.addWriteRequest(new WriteRequest("NOTICE " + target + " :" + msg, con));
	}

	/**
	 * Set self away
	 * 
	 * @param message
	 */
	public void setAway(String message)
	{
		con.addWriteRequest(new WriteRequest("AWAY :" + message, con));
	}

	/**
	 * Unset away
	 * 
	 */
	public void unSetAway()
	{
		con.addWriteRequest(new WriteRequest("AWAY", con));
	}

	/**
	 * Send server version query
	 */
	public void getServerVersion()
	{
		con.addWriteRequest(new WriteRequest("VERSION " + con.getHostName(), con));
	}

	/**
	 * Send server version query for specific hostmask pattern
	 * 
	 * @param hostPattern
	 */
	public void getServerVersion(String hostPattern)
	{
		con.addWriteRequest(new WriteRequest("VERSION " + hostPattern, con));
	}

	/**
	 * Send nick change request
	 * 
	 * @param nick
	 */
	public void changeNick(String nick)
	{
		con.addWriteRequest(new WriteRequest("NICK " + nick, con));
	}

	/**
	 * Set a mode
	 * 
	 * @param target
	 * @param mode
	 */
	public void mode(String target , String mode)
	{
		con.addWriteRequest(new WriteRequest("MODE " + target + " " + mode, con));
	}

	/**
	 * Send ctcp action
	 * 
	 * @param target
	 * @param actionText
	 */
	public void action(String target, String actionText)
	{
		ctcp(target, actionText);
	}

	/**
	 * Speak in a channel
	 * @param msg
	 * @param channel
	 * @see Channel#say(String)
	 */
	public void sayChannel(String msg, Channel channel)
	{
		con.addWriteRequest(new WriteRequest(msg, channel, con));
	}

	/**
	 * Send a private message
	 * 
	 * @param nick
	 * @param msg
	 */
	public void sayPrivate(String nick, String msg)
	{
		con.addWriteRequest(new WriteRequest(msg, con, nick));
	}

	/**
	 * Send raw text to server
	 * 
	 * @param data
	 */
	public void sayRaw(String data)
	{
		con.addWriteRequest(new WriteRequest(data, con));
	}

}
