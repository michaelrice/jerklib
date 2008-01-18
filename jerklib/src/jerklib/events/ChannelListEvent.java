package jerklib.events;


public interface ChannelListEvent extends IRCEvent
{

	public String getChannelName();
	public int getNumberOfUser();
  public String getTopic();

}
