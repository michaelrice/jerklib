package jerklib;



public class RequestedConnectionImpl implements RequestedConnection
{

	private final String hostName;
	private final int port;
	private Profile profile;
	private final long requestedTime = System.currentTimeMillis();
	
	public RequestedConnectionImpl(String hostName , int port , Profile profile)
	{
		this.hostName = hostName;
		this.port = port;
		this.profile = profile;
	}
	
	
	public String getHostName()
	{
		return hostName;
	}

	public int getPort()
	{
		return port;
	}

	public Profile getProfile()
	{
		return profile;
	}

	public long getTimeRequested()
	{
		return requestedTime;
	}

	public int hashCode()
	{
		return hostName.hashCode() + port + profile.hashCode();
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof RequestedConnection && o.hashCode() == hashCode())
		{
			RequestedConnection rCon = (RequestedConnection)o;
			return rCon.getHostName().equals(hostName) &&
				rCon.getPort() == port &&
					rCon.getProfile().equals(profile);
		}
		return false;
	}
	
	void setProfile(Profile profile)
	{
		this.profile = profile;
	}
	
	
	
}
