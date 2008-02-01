package jerklib;

import java.util.List;



/**
 * A pkg access interface that is used internally
 * to manage Session properties like channels joined
 * and connection state.
 * 
 * @author mohadib
 * @see Session
 */
interface InternalSession extends Session
{
	
	/**
	 * Gets the proxied Session.
	 * @return the Session
	 */
	Session getSession();
	
	
	/**
	 * Gets the connection state of this Session
	 * @return the ConnectionState
	 */
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
