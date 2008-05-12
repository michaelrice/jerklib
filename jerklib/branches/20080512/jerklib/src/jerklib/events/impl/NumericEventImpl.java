package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.NumericErrorEvent;

public class NumericEventImpl implements NumericErrorEvent
{
    private final String errMsg, rawEventData;
    private final Session session;
    private final int numeric;

    public NumericEventImpl
            (
                    String errMsg,
                    String rawEventData,
                    int numeric,
                    Session session
            )
    {
        super();
        this.errMsg = errMsg;
        this.rawEventData = rawEventData;
        this.session = session;
        this.numeric = numeric;
    }

    public String getErrorMsg()
    {
        return errMsg;
    }

    public int getNumeric()
    {
        return numeric;
    }

    public ErrorType getErrorType()
    {
        return ErrorType.NUMERIC_ERROR;
    }

    public String getRawEventData()
    {
        return rawEventData;
    }

    public Session getSession()
    {
        return session;
    }

    public Type getType()
    {
        return Type.ERROR;
    }

}
