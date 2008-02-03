package jerklib.events;

/**
 * <p>
 *  This is an event that is fired under three conditions:
 *  <ul>
 *      <li>Sending a message to a user who is marked as away.</li>
 *      <li>You mark yourself as away.</li>
 *      <li>You return from away.</li>
 * </ul>
 * You can determine under which circumstance the event was fired by looking at
 * the {@link EventType}.</p> 
 * @author Robert O'Connor &lt;robby.oconnor@gmail.com&gt;
 *
 */
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
     * Return the event type that was fired
     * @see EventType
     * @return the type of event that was fired.
     */
    public EventType getEventType();

    /**
     * Whether or not you or the user is away. 
     * @return if we're away or not.
     */
    public boolean isAway();


    /**
     * Returns the away message or an empty String if it was you who caused the event to fire. 
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
     * @return the nick of the user who caused the event to fire.
     */
    public String getNick();


}
