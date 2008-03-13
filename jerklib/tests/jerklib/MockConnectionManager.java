package jerklib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jerklib.events.IRCEvent;


/*
 * what can be tested here:
 * 	1. event relaying
 *  2. event parsing
 *  3. the IRCEventFactory
 *  4. WriteListeners notify/get/add/remove
 *  5. session methods get/remove etc
 *  6. all aspects of sesion can be tested via mock connection
 *  
 *  what cant be tested:
 *  	1. all the connection methods makeCOnnections/checkConnections etc
 *  	2. doNetworkIO
 * 
 */
public class MockConnectionManager extends ConnectionManager
{
	Session session;
	File ircDataFile;
	
	public MockConnectionManager(File ircDataFile , String hostName)
	{
		session = new Session(new RequestedConnection
		(
				hostName,
				6667,
				new ProfileImpl("test" , "testnick" , "testnick1" , "testnick2")
		));
		
		this.ircDataFile = ircDataFile;
		
		session.setConnection(new MockConnection(this , null , session));
		session.connected();
		session.getConnection().loginSuccess();
		session.getConnection().setHostName(hostName);
		sessionMap.put("irc.freenode.net", session);
		socChanMap.put(null, session);
		
		
		IRCEventFactory.setManager(this);
		
	}
	
	public void start()
	{
		try
		{
			StringBuilder builder = new StringBuilder();
			FileReader reader = new FileReader(ircDataFile);
			char[] buff = new char[1024];
			int len = 0;
			while((len = reader.read(buff)) != -1)
			{
				builder.append(buff , 0 ,len);
			}
			String[] tokens = builder.toString().split("\r\n");
			for(final String token : tokens)
			{
				addToEventQueue(new IRCEvent()
				{

					public String getRawEventData()
					{
						return token;
					}

					public Session getSession()
					{
						return session;
					}

					public Type getType()
					{
						return Type.DEFAULT;
					}
				});
			}
			
			parseEvents();
			relayEvents();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Session getSession()
	{
		return session;
	}
	
}

