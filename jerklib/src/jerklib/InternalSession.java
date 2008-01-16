package jerklib;

import java.util.List;


interface InternalSession extends Session
{
	
	Session getSession();
	
	ConnectionState getConnectionState();
	
	void setConnectionState(State state);
	
	Connection getConnection();
	
	void setConnection(Connection con);
	
	public void retried();
	
	public long getLastRetry();
	
	public List<String> getChannelNames();
	
	public void rejoinChannels();
	
	public void addChannelName(String name);
	
	
}
