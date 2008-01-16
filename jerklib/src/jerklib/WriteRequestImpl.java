package jerklib;




/**
 * @author mohadib
 *
 */
public class WriteRequestImpl implements WriteRequest
{

	private final Type type;
	private final String message, nick;
	private final Channel channel;
	private final Connection con;
	private String channelName;
	private String connectionName;
	
	
	
	public WriteRequestImpl(String message, Connection con, String nick)
	{
		this.type = Type.PRIVATE_MSG;
		this.message = message;
		this.con = con;
		this.nick = nick;
		this.channel = null;
	}

	
	
	public WriteRequestImpl(String message, Channel channel, Connection con)
	{
		this.type = Type.CHANNEL_MSG;
		this.message = message;
		this.channel = channel;
		this.channelName = channel.getName();
		this.con = con;
		this.nick = null;
	}
	
	public WriteRequestImpl(String message, Channel channel, String hostName)
	{
		this.type = Type.CHANNEL_MSG;
		this.message = message;
		this.channel = channel;
		this.channelName = channel.getName();
		this.connectionName = hostName;
		this.con = null;
		this.nick = null;
	}
	
	public WriteRequestImpl(String message, String channelName, String hostName)
	{
		this.type = Type.CHANNEL_MSG;
		this.message = message;
		this.channel = null;
		this.channelName = channelName;
		this.connectionName = hostName;
		this.con = null;
		this.nick = null;
	}
	
	
	public WriteRequestImpl(String message, String channelName, Connection con)
	{
		this.type = Type.CHANNEL_MSG;
		this.message = message;
		this.channel = null;
		this.channelName = channelName;
		this.con = con;
		this.nick = null;
	}
	

	public WriteRequestImpl(String message, Connection con)
	{
		this.type = Type.RAW_MSG;
		this.message = message;
		this.con = con;
		this.channel = null;
		this.nick = null;
	}
	
	
	public WriteRequestImpl(String message, String hostName)
	{
		this.type = Type.RAW_MSG;
		this.message = message;
		this.connectionName = hostName;
		this.con = null;
		this.channel = null;
		this.nick = null;
	}

	
	public Type getType()
	{
		return type;
	}

	public String getMessage()
	{
		return message;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public Connection getConnection()
	{
		return con;
	}

	public String getNick()
	{
		return nick;
	}
	
	public String getChannelName()
	{
		return channelName;
	}
	
	
	public String getConnectionName()
	{
		return connectionName;
	}
	

}
