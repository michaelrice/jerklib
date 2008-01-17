package jerklib.events;

public interface ChannelListEvent extends IRCEvent
{

    public int getNumberOfUsers();

    public String getChannelName();

    public String getChannelTopic();

}
