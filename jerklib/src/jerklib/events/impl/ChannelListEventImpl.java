package jerklib.events.impl;

import jerklib.events.ChannelListEvent;
import jerklib.events.IRCEvent;
import jerklib.Session;
import jerklib.Channel;
import jerklib.util.Pair;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class ChannelListEventImpl implements ChannelListEvent {

    private final Session session;

    private final String rawEventData;

    private final Map<Channel, Pair<Integer,String>> chanMap = new HashMap<Channel, Pair<Integer,String>>();

    private final Type type = IRCEvent.Type.CHANNEL_LIST_EVENT;

    public ChannelListEventImpl(String rawEventData, Session session) {
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public Map<Channel,Pair<Integer,String>> getChannels() {
        return Collections.unmodifiableMap(chanMap);
    }

    public Type getType() {
        return type;
    }

    public String getRawEventData() {
        return rawEventData;
    }

    public Session getSession() {
        return session;
    }

    public void appendToMap(Channel channel, Pair<Integer,String> pair) {
        chanMap.put(channel,pair);
    }
}
