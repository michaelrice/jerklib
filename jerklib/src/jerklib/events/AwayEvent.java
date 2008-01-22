package jerklib.events;

public interface AwayEvent extends IRCEvent {


    /**
     * an enum to determine the type of event that was fired.
     * WENT_AWAY is when we went go away
     * RETURNED_FROM_AWAY is when we return from an away state
     * USER_IS_AWAY is when a remote user goes away other than us
     */
    public static enum EventType {
        WENT_AWAY,
        RETURNED_FROM_AWAY,
        USER_IS_AWAY
    }

    /**
     * Return the event type.
     * @return the type of event that was fired.
     */
    public EventType getEventType();

    /**
     * Whether or not we're actually away.
     * @return if we're away or not.
     */
    public boolean isAway();


    /**
     * The away message.
     * @return the away message.
     */
    public String getAwayMessage();

    /**
     * Whether or not it was us that fired this event
     * @return if it was us or not.
     */
    public boolean isYou();

    /**
     * Get the nick who fired the event.
     * @return
     */
    public String getNick();


}
