package jerklib;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jerklib.Session.State;
import jerklib.events.IRCEvent;
import jerklib.events.listeners.WriteRequestListener;


class Connection
{
	
	/* ConnectionManager for this Connection */
	private final ConnectionManager manager;
	
	/* SocketChannel this connection will use for reading/writing */
	private final SocketChannel socChannel;
	
	/* A Buffer of write request */
	private final List<WriteRequest> writeRequests = Collections.synchronizedList(new ArrayList<WriteRequest>());
	
	/* a Map to index currently joined channels by name */
	private Map<String, Channel> channelMap = new HashMap<String, Channel>();
	
	/* ByteBuffer for readinging into */
	private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
	
	/* indicates of an event fragment is waiting */
	private boolean gotFragment;
	
	/* buffer for event fragments */
	private  StringBuffer stringBuff = new StringBuffer();
	
	/* actual hostname connected to */
	private String actualHostName;
	
	/* ConnectionState object to hold state information about connection */
	private final ConnectionState conState = new ConnectionState(); 
	
	
	
	Connection(ConnectionManager manager, SocketChannel socChannel)
	{
		this.manager = manager;
		this.socChannel = socChannel;
	}

	Profile getProfile()
	{
		return manager.getSessionFor(this).getRequestedConnection().getProfile();
	}
	
	ConnectionState getConnectionState()
	{
		return conState;
	}

	void setConnectionState(State connectionState)
	{
		conState.setConState(connectionState);
	}

	
	void setHostName(String name)
	{
		manager.getSessionFor(this).setConnectionState(State.CONNECTED);
		actualHostName = name;
	}
	
	String getHostName()
	{
		return actualHostName;
	}
	
	void whois(String nick)
	{
		addWriteRequest(new WriteRequestImpl("WHOIS " + nick + "\r\n" , this));
	}
	
	
	List<Channel>  removeNickFromAllChannels(String nick)
	{
		List<Channel>returnList = new ArrayList<Channel>();
		for(Channel chan : channelMap.values())
		{
			if(chan.removeNick(nick))
			{
				returnList.add(chan);
			}
		}
		return returnList;
	}
	
	void nickChanged(String oldNick , String newNick)
	{
		synchronized (channelMap)
		{
			for(Channel chan : channelMap.values())
			{
				if(chan.getNicks().contains(oldNick))
				{
					chan.nickChanged(oldNick, newNick);
				}
			}
		}
	}
	
	
	
	void removeChannel(Channel channel)
	{
		channelMap.remove(channel.getName());
	}

	Channel getChannel(String name)
	{
		return channelMap.get(name);
	}

	Collection <Channel> getChannels()
	{
		return Collections.unmodifiableCollection(channelMap.values());
	}
	
	void addChannel(Channel channel)
	{
		channelMap.put(channel.getName(), channel);
	}

	void chanList()
	{
		addWriteRequest(new WriteRequestImpl("LIST\r\n" , this));
	}
	
	void chanList(String channel)
	{
		addWriteRequest(new WriteRequestImpl("LIST " + channel + "\r\n" , this));
	}
	
	void whoWas(String nick)
	{
		addWriteRequest(new WriteRequestImpl("WHOWAS " + nick + "\r\n", this));
	}
	
	void join(final String channel)
	{
		if (!channelMap.containsKey(channel))
		{
			WriteRequest request = new WriteRequestImpl("JOIN " + channel + "\r\n", this);

			writeRequests.add(request);
		}
	}

	void join(final String channel , String pass)
	{
		if (!channelMap.containsKey(channel))
		{
			WriteRequest request = new WriteRequestImpl("JOIN " + channel + " " + pass + "\r\n", this);

			writeRequests.add(request);
		}
	}
	
	boolean part(Channel channel , String partMsg)
	{
		return part(channel.getName(), partMsg);
	}
	
	boolean part(String channelName , String partMsg)
	{
		if(channelMap.containsKey(channelName))
		{
			WriteRequest request = new WriteRequestImpl( "PART " + channelName + " :" + partMsg + "\r\n",this);
			
			writeRequests.add(request);
			
			return true;
		}
		return false;
	}

	void changeNick(String nick)
	{
		WriteRequest request = new WriteRequestImpl("NICK " + nick + "\r\n" , this);
		writeRequests.add(request);
	}
	
	void addWriteRequest(WriteRequest request)
	{
		writeRequests.add(request);
	}
	
	boolean finishConnect() throws IOException
	{
		boolean connected = socChannel.finishConnect();
		if (connected)
		{
			 conState.setConState(State.CONNECTED);
		}
		else
		{
			conState.setConState(State.CONNECTING);
		}
		return connected;
	}

	void login()
	{
		WriteRequest request = new WriteRequestImpl("NICK " + getProfile().getActualNick() + "\r\n",this);
		writeRequests.add(request);

		request = new WriteRequestImpl("USER " + getProfile().getName() + " 0 0 :" + getProfile().getName() + "\r\n",this);
		writeRequests.add(request);
	}

	int read()
	{
		
		readBuffer.clear();

		int numRead = 0;

		try
		{
			numRead = socChannel.read(readBuffer);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			conState.setConState(State.DISCONNECTED);
		}

		if(numRead == -1)
		{
			conState.setConState(State.DISCONNECTED);
		}
		
		if (conState.getConState() == State.DISCONNECTED || numRead <= 0) return 0;

		readBuffer.flip();

		String tmpStr = new String(readBuffer.array(), 0, numRead);

		
		// read did not contain a \r\n
		if (tmpStr.indexOf("\r\n") == -1)
		{
			// append whole thing to buffer and set fragment flag
			stringBuff.append(tmpStr);
			gotFragment = true;
			
			//now maybe should check to see if i have /r/n in the buffer??
			
			return numRead;
		}

		// this read had a \r\n in it
		
		if (gotFragment)
		{
			//prepend fragment to front of current message
			tmpStr = stringBuff.toString() + tmpStr;
			stringBuff.delete(0, stringBuff.length());
			gotFragment = false;
		}

		String[] strSplit = tmpStr.split("\r\n");

		for (int i = 0; i < (strSplit.length - 1); i++)
		{
			manager.addToEventQueue(createDefaultIRCEvent(strSplit[i]));
		}

		String last = strSplit[strSplit.length - 1];

		if (!tmpStr.endsWith("\r\n"))
		{
			// sence string did not end with \r\n we need to
			// append the last element in strSplit to a stringbuffer
			// for next read and set flag to indicate we have a fragment waiting
			stringBuff.append(last); 
			gotFragment = true;
			
			//now maybe should check to see if i have /r/n in the buffer??
		}
		else
		{
			manager.addToEventQueue(createDefaultIRCEvent(last));
		}

		return numRead;
	}

	int doWrites()
	{
		int amount = 0;

		for (WriteRequest request : writeRequests)
		{
			String data;

			if (request.getType() == WriteRequest.Type.CHANNEL_MSG)
			{
				data = "PRIVMSG " + request.getChannelName() + " :" + request.getMessage() + "\r\n";
			}
			else if (request.getType() == WriteRequest.Type.PRIVATE_MSG)
			{
				data = "PRIVMSG " + request.getNick() + " :" + request.getMessage() + "\r\n";
			}
			else
			{
				data = request.getMessage();
			}

			byte[] dataArray = data.getBytes();
			ByteBuffer buff = ByteBuffer.allocate(dataArray.length);
			buff.put(dataArray);
			buff.flip();

			try
			{
				amount += socChannel.write(buff);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				conState.setConState(State.DISCONNECTED);
			}
			
			if(conState.getConState() == Session.State.DISCONNECTED) return amount;
			
			fireWriteEvent(request);
		}

		writeRequests.clear();

		return amount;

	}

	void ping()
	{
		WriteRequest request = new WriteRequestImpl("PING " + actualHostName + "\r\n" , this );
		writeRequests.add(request);
		conState.pingSent();
	}
	
	void pong(IRCEvent event)
	{
		conState.gotResponse();
    String data = event.getRawEventData().substring(event.getRawEventData().lastIndexOf(":") + 1);
		WriteRequest request = new WriteRequestImpl("PONG " + data + "\r\n" , this );
		writeRequests.add(request);
	}
	
	void gotPong()
	{
		conState.gotResponse();
	}
	
	void quit(String quitMessage)
	{
		try
		{
			WriteRequest request = new WriteRequestImpl("QUIT :" + quitMessage + "\r\n" , this);
			
			writeRequests.add(request);
			
			//clear out write queue 
			doWrites();
			
			//send quit message here
			socChannel.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void fireWriteEvent(WriteRequest request)
	{
		for(WriteRequestListener listener : manager.getWriteListeners())
		{
			listener.receiveEvent(request);
		}
	}
	
	private IRCEvent createDefaultIRCEvent(final String rawData)
	{

		
		return

		new IRCEvent()
		{

			public Session getSession()
			{
				return manager.getSessionFor(Connection.this);
			}

			@SuppressWarnings("unused")
			public String getHostName()
			{
				return "";
			}

			@SuppressWarnings("unused")
			public int getPort()
			{
				return 6667;
			}

			public String getRawEventData()
			{
				return rawData;
			}

			public Type getType()
			{
				return IRCEvent.Type.DEFAULT;
			}
		};
	}
	
}
