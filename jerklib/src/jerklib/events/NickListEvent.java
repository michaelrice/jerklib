package jerklib.events;

import jerklib.Channel;

import java.util.List;

/**
 * @author mohadib
 *         Event fired when nick list event comes from server
 */
public interface NickListEvent extends IRCEvent
{

    /**
     * Gets the channel the nick list came from
     *
     * @return Channel
     * @see Channel
     */
    public Channel getChannel();


    /**
     * Gets the nick list for the Channel
     *
     * @return List of nicks in channel
     */
    public List<String> getNicks();
}
