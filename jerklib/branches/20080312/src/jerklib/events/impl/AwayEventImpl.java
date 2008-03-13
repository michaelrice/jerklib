package jerklib.events.impl;

import jerklib.events.AwayEvent;
import jerklib.events.IRCEvent;
import jerklib.Session;

public class AwayEventImpl implements AwayEvent {

    private final boolean isAway,isYou;

    private final String rawEventData,awayMessage,nick;

    private final Session session;

    private Type type = IRCEvent.Type.AWAY_EVENT;

    private EventType eventType;

    public AwayEventImpl(String awayMessage, EventType eventType, boolean away, boolean you, String nick, String rawEventData, Session session) {
        this.awayMessage = awayMessage;
        this.eventType = eventType;
        isAway = away;
        isYou = you;
        this.nick = nick;
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public AwayEventImpl(Session session, EventType eventType, boolean away, boolean you, String nick, String rawEventData) {
        this.awayMessage = ""; 
        this.session = session;
        this.eventType = eventType;
        isAway = away;
        isYou = you;
        this.nick = nick;
        this.rawEventData = rawEventData;
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

    public String getRawEventData() {
        return rawEventData;
    }

    public Session getSession() {
        return session;
    }

    public Type getType() {
        return type;
    }

    public EventType getEventType() {
        return eventType;
    }
    
}