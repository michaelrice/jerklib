package jerklib.events.impl;

import jerklib.events.AwayEvent;
import jerklib.Session;

public class AwayEventImpl implements AwayEvent {

    private final boolean isAway,isYou;

    private final String rawEventData,ownNick,awayMessage,nick;

    private final Session session;

    private Type type;
    private final EventType eventType;

    public AwayEventImpl(String awayMessage, EventType eventType, boolean away, boolean you, String nick, String ownNick, String rawEventData, Session session) {
        this.awayMessage = awayMessage;
        this.eventType = eventType;
        isAway = away;
        isYou = you;
        this.nick = nick;
        this.ownNick = ownNick;
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getAwayMessage() {
        return awayMessage;
    }

    public boolean isAway() {
        return isAway;
    }

    public boolean isYou() {
        return isYou;
    }

    public String getNick() {
        return nick;
    }

    public String getOwnNick() {
        return ownNick;
    }

    public String getRawEventData() {
        return rawEventData;
    }

    public Session getSession() {
        return session;
    }

    public Type getType() {
        return type;
    }
}