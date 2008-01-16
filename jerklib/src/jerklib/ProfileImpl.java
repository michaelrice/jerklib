package jerklib;


public class ProfileImpl implements Profile
{
	private String name , actualNick , firstNick , secondNick , thirdNick;
	
	public ProfileImpl(String name , String nick ,String secondNick , String thirdNick)
	{
		this.name = name == null ? "" : name;
		this.firstNick = nick == null ? "" : nick;
		this.secondNick = secondNick == null ? "" : secondNick;
		this.thirdNick = thirdNick == null ? "" : thirdNick;
		actualNick = firstNick;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getFirstNick()
	{
		return firstNick;
	}
	
	@Override
	public int hashCode()
	{
		return (name + actualNick).hashCode();
	}
	
	@Override
	public String getSecondNick() {
		return secondNick;
	}
	
	@Override
	public String getThirdNick() 
	{
		return thirdNick;
	}
	
	@Override
	public String getActualNick() 
	{
		return actualNick;
	}
	
	@Override
	public void setActualNick(String aNick) 
	{
		actualNick = aNick;
	}
	
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
}
