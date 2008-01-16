package jerklib.events.impl;


import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;


public class PrivateMessageEventImpl implements PrivateMsgEvent
{

	private final String rawEventData,nick,message, nicksHost,login;
	private final Type type = IRCEvent.Type.PRIVATE_MESSAGE;
	private final Session session;

	public PrivateMessageEventImpl(String rawEventData,Session session, String nick, String login, String message, String nicksHost)
	{
		this.rawEventData = rawEventData;
		this.session = session;
		this.nick = nick;
        this.login = login;
        this.message = message;
		this.nicksHost = nicksHost;

	}

    public final String getLogin()
    {
        return login;
    }

    public final String getNick()
	{
		return nick;
	}

	public final Type getType()
	{
		return type;
	}

	public final String getRawEventData()
	{
		return rawEventData;
	}


	public final Session getSession()
	{
		return session;
	}

	public final String getMessage()
	{
		return this.message;
	}

	public final String getNicksHost()
	{
		return nicksHost;
	}

	public String toString()
	{
		return rawEventData;
	}
	
}
