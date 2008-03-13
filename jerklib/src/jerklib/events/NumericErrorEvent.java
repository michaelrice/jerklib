package jerklib.events;

/**
 * @author Mohadib
 *         Event fired for most all numeric error replies
 */
public interface NumericErrorEvent extends ErrorEvent
{
    /**
     * gets error message
     *
     * @return error message
     */
    public String getErrorMsg();

    /**
     * Gets numeric error code
     *
     * @return numeric
     */
    public int getNumeric();

}
