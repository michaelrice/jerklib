package jerklib.events.impl;

import jerklib.ServerInformation;
import jerklib.Session;
import jerklib.events.ServerInformationEvent;

public class ServerInformationEventImpl implements ServerInformationEvent
{

    private final Session session;
    private final String rawEventData;
    private final ServerInformation serverInfo;


    public ServerInformationEventImpl
            (
                    Session session,
                    String rawEventData,
                    ServerInformation serverInfo
            )
    {
        this.session = session;
        this.rawEventData = rawEventData;
        this.serverInfo = serverInfo;
    }

    public ServerInformation getServerInformation()
    {
        return serverInfo;
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
        return Type.SERVER_INFORMATION;
    }

}
