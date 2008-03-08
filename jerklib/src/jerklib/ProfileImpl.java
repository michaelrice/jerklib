package jerklib;


/**
 * @author mohadib
 * 
 * Used when get a new instance of connection mananer.
 * Connection manager uses this information when making new connections
 *
 */
public class ProfileImpl implements Profile
{
	private String name , actualNick , firstNick , secondNick , thirdNick;
	
	/**
	 * @param name Username
	 * @param nick Nick
	 * @param secondNick Alt nick 1
	 * @param thirdNick Alt nick 2
	 */
	public ProfileImpl(String name , String nick ,String secondNick , String thirdNick)
	{
		this.name = name == null ? "" : name;
		this.firstNick = nick == null ? "" : nick;
		this.secondNick = secondNick == null ? "" : secondNick;
		this.thirdNick = thirdNick == null ? "" : thirdNick;
		actualNick = firstNick;
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Profile#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see jerklib.Profile#getFirstNick()
	 */
	public String getFirstNick()
	{
		return firstNick;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return (name + actualNick).hashCode();
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Profile#getSecondNick()
	 */
	public String getSecondNick() 
	{
		return secondNick;
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Profile#getThirdNick()
	 */
	public String getThirdNick() 
	{
		return thirdNick;
	}
	
	/* (non-Javadoc)
	 * @see jerklib.Profile#getActualNick()
	 */
	public String getActualNick() 
	{
		return actualNick;
	}
	
	public void setActualNick(String aNick) 
	{
		actualNick = aNick;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Profile && o.hashCode() == hashCode())
		{
			Profile p = (Profile)o;
			return p.getName().equals(name) && p.getActualNick().equals(actualNick);
		}
		return false;
	}
	
	
	public Profile clone()
	{
		ProfileImpl impl = new ProfileImpl
		(
			name,
			firstNick,
			secondNick,
			thirdNick
		);
		impl.setActualNick(actualNick);
		return impl;
	}
}
