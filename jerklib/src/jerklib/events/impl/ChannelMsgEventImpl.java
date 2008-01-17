package jerklib.events.impl;


import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;


public class ChannelMsgEventImpl implements ChannelMsgEvent
{

	private final String rawEventData, nick, userName, message, host;
	private final Type type = IRCEvent.Type.CHANNEL_MESSAGE;
	private final Session session;
	private final Channel channel;

	public ChannelMsgEventImpl
	(
			String rawEventData, 
			Session session, 
			Channel channel, 
			String nick, 
			String userName, 
			String message, 
			String host
	)
	{

		this.rawEventData = rawEventData;
		this.session = session;
		this.channel = channel;
		this.nick = nick;
        this.userName = userName;
        this.message = message;
		this.host = host;

	}


    public Channel getChannel()
	{
		return channel;
	}

	public String getNick()
	{
		return nick;
	}

  public String getUserName()
  {
  	return userName;
  }
    public Type getType()
	{
		return type;
	}

	public String getRawEventData()
	{
		return rawEventData;
	}


	public Session getSession()
	{
		return session;
	}

	public String getMessage()
	{
		return this.message;
	}

	public String getHostName()
	{
		return host;
	}

	public String toString()
	{
		return rawEventData;
	}
	
}
