package jerklib.events;

public interface InviteEvent extends IRCEvent{

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
