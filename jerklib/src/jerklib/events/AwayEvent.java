package jerklib.events;

public interface AwayEvent extends IRCEvent {


    public static enum EventType {
        WENT_AWAY,
        RETURNED_FROM_AWAY,
        USER_IS_AWAY
    }

    public EventType getEventType();

    public boolean isAway();

    public String getOwnNick();

    public String getAwayMessage();

    public boolean isYou();

    public String getNick(); 


}
