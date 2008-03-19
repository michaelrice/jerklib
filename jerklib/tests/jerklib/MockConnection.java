package jerklib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import jerklib.events.IRCEvent;

/**
 * @version $Revision$
 */
public class MockConnection extends Connection
{

	private InputStreamReader reader;
	private FileOutputStream writer;
	private Session session;
	private List<IRCEvent> events = new ArrayList<IRCEvent>();
	private ConnectionManager manager;
	
    MockConnection
    (
    	ConnectionManager manager, 
    	SocketChannel socChannel, 
    	Session session,
    	String inputFilePath,
    	String outputFilePath
    )
    {
        super(manager, socChannel, session);
        this.session = session;
        this.manager = manager;
        setInputFile(inputFilePath);
        setOutputFile(outputFilePath);
    }
    
    public void setInputFile(String path)
    {
    	reader = new InputStreamReader(MockConnectionManager.class.getResourceAsStream(path));
    	parse();
    }
    
    public void setOutputFile(String path)
    {
    	 try
				{
					writer = new FileOutputStream(path , false);
				}
				catch (FileNotFoundException e)
				{
					throw new Error(e);
				}
    }
    
    
    public void start()
    {
    	for(IRCEvent event : events)
    	{
    		manager.addToEventQueue(event);
    	}
    }
    
    
    public int doWrites()
    {
    	int amount = 0;
    	
    	for(WriteRequest request : writeRequests)
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
            if (!data.endsWith("\r\n"))
            {
                data += "\r\n";
            }
        }
        
        try
				{
					writer.write(data.getBytes());
					amount += data.getBytes().length;
					fireWriteEvent(request);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
    	}
    	return amount;
    }
    
    
    public void parse()
    {
    	events.clear();
  		try
  		{
  			BufferedReader br = new BufferedReader(reader);
  			String token = null;
  			while ((token = br.readLine()) != null)
  			{
  				//skip comments
  				if(!token.startsWith("#"))
  				{
  					final String token1 = token;
    				events.add(new IRCEvent()
    				{

    					public String getRawEventData()
    					{
    						return token1;
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
  			}
  		}
  		catch (IOException e)
  		{
  			throw new Error(e); // die horribly. What's wrong with you, you can't build a test properly?
  		}
    }
    
}
