package jerklib;

class RequestGenerator 
{
	
	private Connection con;
	void setConnection(Connection con)
	{
		this.con = con;
	}

	RequestGenerator(){}
	
	public void whois(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOIS " + nick , con));
	}

	public void invite(String nick, Channel chan)
	{
		con.addWriteRequest(new WriteRequest("INVITE " + nick + " " + chan.getName() , con));
	}
	
	public void chanList()
	{
		con.addWriteRequest(new WriteRequest("LIST", con));
	}

	public void chanList(String channel)
	{
		con.addWriteRequest(new WriteRequest("LIST " + channel  , con));
	}

	public void whoWas(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOWAS " + nick , con));
	}

	public void join(String channel)
	{
		if(con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel , con));
		}
	}

    public void ctcp(String target, String request) 
    {
    	if(con != null)
    	{
    		con.addWriteRequest(new WriteRequest("\001"+request.toUpperCase()+"\001",con,target));
    	}
    }
	
	public void join(String channel, String pass)
	{
		if(con != null)
		{
			con.addWriteRequest(new WriteRequest("JOIN " + channel + " " + pass , con));
		}
	}

	public void notice(String target, String msg) 
	{
	     con.addWriteRequest(new WriteRequest("NOTICE "+target+" :"+msg ,con));
	}
	
	public void who(String who) 
	{
	  	con.addWriteRequest(new WriteRequest("WHO "+who ,con));        
	}
	
	public void setAway(String message)
	{
		con.addWriteRequest(new WriteRequest("AWAY :" + message , con));
	}
	
	public void unSetAway()
	{
		con.addWriteRequest(new WriteRequest("AWAY", con));
	}

	public void getServerVersion()
	{
		con.addWriteRequest(new WriteRequest("VERSION " + con.getHostName() , con));
	}

	public void getServerVersion(String hostPattern)
	{
		con.addWriteRequest(new WriteRequest("VERSION " + hostPattern , con));
	}

	public void changeNick(String nick)
	{
		con.addWriteRequest(new WriteRequest("NICK " + nick , con));
	}

	public void mode(Channel channel, String mode)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " " + mode  , con));
	}
	
	public void mode(String userName, Channel channel, String mode)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " " + mode + " " + userName , con));
	}

	public void deVoice(String userName, Channel channel)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " -v " + userName , con));
	}

	public void voice(String userName, Channel channel)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " +v " + userName , con));
	}

	public void op(String userName, Channel channel)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " +o " + userName , con));
	}
	
	public void deop(String userName, Channel channel)
	{
		con.addWriteRequest(new WriteRequest("MODE " + channel.getName() + " -o " + userName , con));
	}
	
	public void kick(String userName, String reason, Channel channel)
	{
		con.addWriteRequest(new WriteRequest("KICK " + channel.getName() + " " + userName + " :" + reason , con));
	}
	
	public void action(String target, String actionText) 
    {
    	ctcp(target, actionText);
    }
	
	public void sayChannel(String channelName, String msg)
	{
		con.addWriteRequest(new WriteRequest(msg, con.getChannel(channelName), con));
	}

	public void sayPrivate(String nick, String msg)
	{
		con.addWriteRequest(new WriteRequest(msg, con, nick));
	}
	
  	public void part(Channel channel, String partMsg)
  	{
		part(channel.getName(), partMsg);
  	}

	public void part(String channelName, String partMsg)
	{
		con.addWriteRequest(new WriteRequest("PART " + channelName + " :" + partMsg , con));
	}
	
	public void sayRaw(String data)
	{
		con.addWriteRequest(new WriteRequest(data, con));
	}

}
