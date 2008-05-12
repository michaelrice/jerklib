package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.ServerVersionEvent;

public class ServerVersionEventImpl implements ServerVersionEvent
{
    private final String comment, hostName, version, debugLevel, rawEventData;
    private final Session session;
    private final Type type = Type.SERVER_VERSION_EVENT;

    public ServerVersionEventImpl
            (
                    String comment,
                    String hostName,
                    String version,
                    String debugLevel,
                    String rawEventData,
                    Session session
            )
    {
        super();
        this.comment = comment;
        this.hostName = hostName;
        this.version = version;
        this.debugLevel = debugLevel;
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public String getComment()
    {
        return comment;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getVersion()
    {
        return version;
    }

    public String getdebugLevel()
    {
        return debugLevel;
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
        return type;
    }

}
