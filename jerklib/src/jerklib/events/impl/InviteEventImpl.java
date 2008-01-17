package jerklib.events.impl;

import jerklib.events.InviteEvent;
import jerklib.events.IRCEvent;
import jerklib.Session;

public class InviteEventImpl implements InviteEvent {
    private final String nick,channel,rawEventData;
    private Type type = IRCEvent.Type.INVITE_EVENT;
    private Session session;

    public InviteEventImpl(String channel, String nick, String rawEventData, Session session){
        this.channel = channel;
        this.nick = nick;
        this.rawEventData = rawEventData;
        this.session = session;
        this.type = type;
    }

    /**
     * getType() is used to find out the exact type of event the IRCEvent object
     * is. The IRCEvent object can be cast into a more specific event object to
     * get access to convience methods for the specific event types.
     *
     * @return <code>Type</code> enum for event.
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the channel to which we were invited to
     *
     * @return the channel we were invited to.
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the nick of the person who invited us
     *
     * @return the nick of the person who invited us
     */
    public String getNick() {
        return nick;
    }

    /**
     * getRawEventData() returns the raw IRC data that makes up this event
     *
     * @return <code>String</code> Raw IRC event text.
     */
    public String getRawEventData() {
        return rawEventData;
    }

    public Session getSession() {
        return session;
    }
}
