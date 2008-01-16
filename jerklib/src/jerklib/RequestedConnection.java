package jerklib;


public interface RequestedConnection
{

	public String getHostName();
  
	public int getPort();
	
	public long getTimeRequested();
	
	public Profile getProfile();
	
}
