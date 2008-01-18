package jerklib.events;

import jerklib.Channel;
import jerklib.util.Pair;

import java.util.Map;

public interface ChannelListEvent extends IRCEvent
{

    public Map<Channel, Pair<Integer,String>> getChannels();

    public void appendToMap(Channel chan, Pair<Integer,String> pair); 

}
