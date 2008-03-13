package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;


public class ConnectionCompleteEventImpl implements ConnectionCompleteEvent
{

    private final String rawEventData, hostName, oldHostName;
    private final Session session;

    public ConnectionCompleteEventImpl(String rawEventData, String hostName, Session session, String oldHostName)
    {

        this.rawEventData = rawEventData;
        this.hostName = hostName;
        this.session = session;
        this.oldHostName = oldHostName;

    }

    public String getOldHostName()
    {
        return oldHostName;
    }

    public Type getType()
    {
        return IRCEvent.Type.CONNECT_COMPLETE;
    }

    public String getRawEventData()
    {
        return rawEventData;
    }

    public String getActualHostName()
    {
        return hostName;
    }


    public Session getSession()
    {
        return session;
    }


    public String toString()
    {
        return rawEventData;
    }

}
