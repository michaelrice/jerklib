package jerklib.events;

public interface AwayEvent extends IRCEvent {


    /**
     * An enum to determine the type of event that was fired.
     * <br>WENT_AWAY is when we went go away.<br>
     * RETURNED_FROM_AWAY is when we return from an away state.<br>
     * USER_IS_AWAY is when a remote user goes away other than us.<br>
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
     * One caveat about this method is that if you are returning from away, it will return an empty String.
     * @return the away message
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
