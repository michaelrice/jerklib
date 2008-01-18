package jerklib.events;

import jerklib.Channel;

import java.util.Map;

public interface ChannelListEvent extends IRCEvent
{

    public Map<Channel,Integer> getChannels();

    public void appendToMap(Channel chan, int numberOfUsers);

}
