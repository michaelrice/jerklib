package jerklib.events;


/**
 * Event fired when an Invite message is recieved from server
 * 
 * @author  Robert O'Connor <robby.oconnor@gmail.com>
 *
 */
public interface InviteEvent extends IRCEvent
{

    /**
     * Gets the nick of the person who invited us
     * @return the nick of the person who invited us
     */
    public String getNick();

    /**
     * Gets the channel to which we were invited to
     * @return the channel we were invited to.
     */
    public String getChannel();
}
