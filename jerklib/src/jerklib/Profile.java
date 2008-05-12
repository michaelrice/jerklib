package jerklib;


/**
 * Profiles are used when making connections to servers.
 *
 * @author mohadib
 */
public interface Profile extends Cloneable
{
    /**
     * Gets the nick currently being used
     *
     * @return the nick
     */
    String getActualNick();


    /**
     * Return the first nick to try when connecting
     *
     * @return the first nick
     */
    String getFirstNick();

    /**
     * Return the second nick to try when connecting
     *
     * @return the second nick
     */
    String getSecondNick();

    /**
     * Return the third nick to try when connecting
     *
     * @return the third nick
     */
    String getThirdNick();

    /**
     * Return the username
     *
     * @return the username
     */
    String getName();
    
    Profile clone();
}
