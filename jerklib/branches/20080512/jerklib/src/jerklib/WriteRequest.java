package jerklib;


/**
 * IRCWriteRequest - this is sent to an IRCConnection whenever a 'write' needs
 * to happen. There are 3 types of IRCWriteRequests. PRIV_MSG , DIRECT_MSG ,
 * RAW_MSG (from the Type enum). RAW_MSG is used when you need direct access to
 * the IRC stream , else PRIV_MSG or DIRECT_MSG should be used.
 *
 * @author mohadib
 */
public class WriteRequest
{

    private final Type type;
    private final String message, nick;
    private final Channel channel;
    private final Connection con;
    private String channelName;
    private String connectionName;

    /**
     * Type enum is used to determine type. It is returned from getType() PRIV_MSG
     * is a standard msg to an IRC channel. DIRECT_MSG is msg sent directly to
     * another user (not in a channel). RAW_MSG when direct access to the IRC
     * stream is needed.
     */
    public enum Type
    {
        CHANNEL_MSG, PRIVATE_MSG, RAW_MSG
    }

    ;

    public WriteRequest(String message, Connection con, String nick)
    {
        this.type = Type.PRIVATE_MSG;
        this.message = message;
        this.con = con;
        this.nick = nick;
        this.channel = null;
    }


    public WriteRequest(String message, Channel channel, Connection con)
    {
        this.type = Type.CHANNEL_MSG;
        this.message = message;
        this.channel = channel;
        this.channelName = channel.getName();
        this.con = con;
        this.nick = null;
    }

    public WriteRequest(String message, Channel channel, String hostName)
    {
        this.type = Type.CHANNEL_MSG;
        this.message = message;
        this.channel = channel;
        this.channelName = channel.getName();
        this.connectionName = hostName;
        this.con = null;
        this.nick = null;
    }

    public WriteRequest(String message, String channelName, String hostName)
    {
        this.type = Type.CHANNEL_MSG;
        this.message = message;
        this.channel = null;
        this.channelName = channelName;
        this.connectionName = hostName;
        this.con = null;
        this.nick = null;
    }


    public WriteRequest(String message, String channelName, Connection con)
    {
        this.type = Type.CHANNEL_MSG;
        this.message = message;
        this.channel = null;
        this.channelName = channelName;
        this.con = con;
        this.nick = null;
    }


    public WriteRequest(String message, Connection con)
    {
        this.type = Type.RAW_MSG;
        this.message = message;
        this.con = con;
        this.channel = null;
        this.nick = null;
    }


    public WriteRequest(String message, String hostName)
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
