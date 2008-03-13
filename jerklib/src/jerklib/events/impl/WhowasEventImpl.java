package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.WhowasEvent;

public class WhowasEventImpl implements WhowasEvent
{
    private final Type type = Type.WHOWAS_EVENT;
    private final String hostName, userName, nick, realName, rawEventData;
    private final Session session;


    public WhowasEventImpl
            (
                    String hostName,
                    String userName,
                    String nick,
                    String realName,
                    String rawEventData,
                    Session session
            )
    {
        this.hostName = hostName;
        this.userName = userName;
        this.nick = nick;
        this.realName = realName;
        this.rawEventData = rawEventData;
        this.session = session;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getNick()
    {
        return nick;
    }

    public String getRealName()
    {
        return realName;
    }

    public String getUserName()
    {
        return userName;
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
