package jerklib;

class RequestGenerator
{
	private Connection con;

	void setConnection(Connection con)
	{
		this.con = con;
	}

	public void whois(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOIS " + nick, con));
	}

	public void invite(String nick, Channel chan)
	{
		con.addWriteRequest(new WriteRequest("INVITE " + nick + " " + chan.getName(), con));
	}

	public void chanList()
	{
		con.addWriteRequest(new WriteRequest("LIST", con));
	}

	public void chanList(String channel)
	{
		con.addWriteRequest(new WriteRequest("LIST " + channel, con));
	}

	public void whoWas(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOWAS " + nick, con));
	}

	public void join(String channel)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel, con));
		}
	}
	
	public void join(String channel, String pass)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel + " " + pass, con));
		}
	}
	
	public void ctcp(String target, String request)
	{
		if (con != null)
		{
			con.addWriteRequest(new WriteRequest("\001" + request.toUpperCase() + "\001", con, target));
		}
	}

	public void notice(String target, String msg)
	{
		con.addWriteRequest(new WriteRequest("NOTICE " + target + " :" + msg, con));
	}

	public void who(String who)
	{
		con.addWriteRequest(new WriteRequest("WHO " + who, con));
	}

	public void setAway(String message)
	{
		con.addWriteRequest(new WriteRequest("AWAY :" + message, con));
	}

	public void unSetAway()
	{
		con.addWriteRequest(new WriteRequest("AWAY", con));
	}

	public void getServerVersion()
	{
		con.addWriteRequest(new WriteRequest("VERSION " + con.getHostName(), con));
	}

	public void getServerVersion(String hostPattern)
	{
		con.addWriteRequest(new WriteRequest("VERSION " + hostPattern, con));
	}

	public void changeNick(String nick)
	{
		con.addWriteRequest(new WriteRequest("NICK " + nick, con));
	}

	public void mode(String target , String mode)
	{
		con.addWriteRequest(new WriteRequest("MODE " + target + " " + mode, con));
	}

	public void action(String target, String actionText)
	{
		ctcp(target, actionText);
	}

	public void sayChannel(String msg, Channel channel)
	{
		con.addWriteRequest(new WriteRequest(msg, channel, con));
	}

	public void sayPrivate(String nick, String msg)
	{
		con.addWriteRequest(new WriteRequest(msg, con, nick));
	}

	public void sayRaw(String data)
	{
		con.addWriteRequest(new WriteRequest(data, con));
	}

}
