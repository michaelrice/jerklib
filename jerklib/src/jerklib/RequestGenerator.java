package jerklib;

public class RequestGenerator 
{
	private Connection con;

	void setConnection(Connection con)
	{
		this.con = con;
	}
	
	void whois(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOIS " + nick , con));
	}

	void invite(String nick, Channel chan)
	{
		con.addWriteRequest(new WriteRequest("INVITE " + nick + " " + chan.getName() , con));
	}
	
	void chanList()
	{
		con.addWriteRequest(new WriteRequest("LIST", con));
	}

	void chanList(String channel)
	{
		con.addWriteRequest(new WriteRequest("LIST " + channel  , con));
	}

	void whoWas(String nick)
	{
		con.addWriteRequest(new WriteRequest("WHOWAS " + nick , con));
	}

	void join(String channel)
	{
		con.addWriteRequest(new WriteRequest("JOIN " + channel , con));
	}

	void join(String channel, String pass)
	{
		con.addWriteRequest(new WriteRequest("JOIN " + channel + " " + pass , con));
	}

	void notice(String target, String msg) 
	{
	     con.addWriteRequest(new WriteRequest("NOTICE "+target+" :"+msg ,con));
	}
	
	void who(String who) 
	{
	  	con.addWriteRequest(new WriteRequest("WHO "+who ,con));        
	}
	
	void setAway(String message)
	{
		con.addWriteRequest(new WriteRequest("AWAY :" + message , con));
	}
	
	void unSetAway()
	{
		con.addWriteRequest(new WriteRequest("AWAY", con));
	}

	void getServerVersion()
	{
		con.addWriteRequest(new WriteRequest("VERSION " + con.getHostName() , con));
	}

	void getServerVersion(String hostPattern)
	{
		con.addWriteRequest(new WriteRequest("VERSION " + hostPattern , con));
	}

	void changeNick(String nick)
	{
		con.addWriteRequest(new WriteRequest("NICK " + nick , con));
	}
}
