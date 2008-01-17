package jerklib.events.impl;

import jerklib.events.ChannelListEvent;
import jerklib.events.IRCEvent;
import jerklib.Session;

public class ChannelListEventImpl implements ChannelListEvent {

    private final int numberOfUsers;

    private final String rawEventData,channelName,channelTopic;

    private final Type type = IRCEvent.Type.CHANNEL_LIST_EVENT;
    private final Session session;

    public ChannelListEventImpl(String channelName, String channelTopic, int numberOfUsers, String rawEventData, Session session) {
        this.channelName = channelName;
        this.channelTopic = channelTopic;
        this.numberOfUsers = numberOfUsers;
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public Session getSession() {
        return session;
    }


    public String getChannelTopic() {
        return channelTopic;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public String getChannelName() {
        return channelName;
    }

   public String getRawEventData() {
        return rawEventData;
    }

    public Type getType() {
        return type;
    }
}
