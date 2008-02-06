package jerklib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import jerklib.ConnectionState.PingState;
import jerklib.Session.State;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.events.listeners.WriteRequestListener;
import jerklib.tasks.Task;


/**
 * @author Jason Davis
 * 
 *  This class is used to control all Connections.
 *  
 *  Request new connections with this class
 *
 */
public class ConnectionManager
{
	
	private static String version = "0.3 or greater";
	private static String extendedVersion = "";
	static
	{
		Properties props = new Properties();
		try
		{
			props.load(ConnectionManager.class.getResourceAsStream("/jerklib/JerkLib.Properties"));
			version = props.getProperty("version");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Jerklib will answer CTCP VERSION requests automatically.
	 * This method allows users to prepend a string to the version
	 * rely.
	 * 
	 * @param version The version string to use
	 */
	public static void setVersionString(String version)
	{
		if(version == null) return;
		extendedVersion = version;
	}
	
	
	/**
	 * gets the version string of library
	 * @return Version string
	 */
	public static String getVersion()
	{
		if(extendedVersion.length() > 0)
		{
			return extendedVersion + " : " + version;
		}
		return version;
	}
	
	/* maps to index sessions by name and socketchannel */
	private final Map<String, SessionImpl> sessionMap = Collections.synchronizedMap(new HashMap<String, SessionImpl>());
	private final Map<SocketChannel ,SessionImpl> socChanMap = Collections.synchronizedMap(new HashMap<SocketChannel, SessionImpl>());
	
	/* event listener lists */
	private final List<WriteRequestListener> writeListeners = Collections.synchronizedList(new ArrayList<WriteRequestListener>(1));
	
	/* event queues */
	private final List<IRCEvent> eventQueue = new ArrayList<IRCEvent>();
	private final List<IRCEvent> relayQueue = new ArrayList<IRCEvent>();
	private final List<WriteRequest> requestForWriteListenerEventQueue = new ArrayList<WriteRequest>();
	
	/* internal event parser */
	private final InternalEventParser parser = new InternalEventParser(this);
	
	/* main loop timer */
	private Timer loopTimer;
	
	/* event dispatch timer */
	private Timer dispatchTimer;
	
	/* default user profile to use for new connections */
	private Profile defaultProfile;
	
	/* NIO Selector */
	private Selector selector;
	
	
	/**
	 * Only constructor - Takes a profile to use as
	 * default profile for new Connections
	 * 
	 * @param defaultProfile default user profile
	 * @see Profile
	 * @see ProfileImpl
	 */
	public ConnectionManager(Profile defaultProfile)
	{
		this.defaultProfile = defaultProfile;
		
		IRCEventFactory.setManager(this);
		
		try
		{
			selector = Selector.open();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		startMainLoop();
	}

	/**
	 * get a list of Sessions
	 * 
	 * @return Session list
	 */
	public List<Session> getSessions()
	{
		List<Session> sessions = new ArrayList<Session>(sessionMap.size());
		for(Session ses : sessionMap.values())
		{
			sessions.add(ses);
		}
		return Collections.unmodifiableList(sessions);
	}
	
	/**
	 * gets a session by name
	 * 
	 * @param name session name - the hostname of the server this session is for
	 * @return Session or null if no Session with name exists
	 */
	public Session getSession(String name)
	{
		//if(sessionMap.containsKey(name)) return sessionMap.get(name).getSession();
		//returning actual InternalSession to make testing easier
		return sessionMap.get(name);
		
	}

	/**
	 * Adds a listener to be notified of all writes
	 * 
	 * @param listener listener to be notified
	 */
	public void addWriteRequestListener(WriteRequestListener listener)
	{
		writeListeners.add(listener);
	}
	
	/**
	 *  gets an unmodifiable list of WriteListeners
	 * 
	 * @return listeners
	 */
	public List<WriteRequestListener> getWriteListeners()
	{
		return Collections.unmodifiableList(writeListeners);
	}
	

	
	/**
	 * request a new connection to a host with the default port of 6667
	 * @param hostName DNS name of host to connect to
     * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName)
	{
		return requestConnection(hostName , 6667);
	}
	
	/**
	 * request a new connection to a host
	 * @param hostName DNS name of host to connect to
	 * @param port port to use for connection
     * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName , int port)
	{
		return requestConnection(hostName , port , defaultProfile);
	}
	
	/**
	 * request a new connection to a host
	 * @param hostName  DNS name of host to connect to
	 * @param port port to use for connection
	 * @param profile profile to use for this connection
     * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName , int port , Profile profile)
	{
		if(sessionMap.containsKey(hostName))
		{
			throw new IllegalArgumentException("Duplicate hostnames are not allowed");
		}

		RequestedConnection rCon = new RequestedConnection(hostName , port , profile);
		SessionImpl session = new SessionImpl(rCon);
		
		if(sessionMap.containsValue(session))
		{
			throw new IllegalArgumentException("Already connected to " + hostName + " on same port with same profile");
		}
		
		sessionMap.put(hostName, session);
		
		return session;
	}
	
	/**
	 * Closes all connections and shuts down manager
	 * 
	 * @param quitMsg  quit message
	 */
	public synchronized void quit(String quitMsg)
	{
		
		loopTimer.cancel();
		
		dispatchTimer.cancel();
		
		for(Session session : sessionMap.values())
		{
			session.close(quitMsg);
		}
		
		sessionMap.clear();
		
		socChanMap.clear();
		
		try
		{
			selector.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 *Closes all Sessions and exits library 
	 */
	public synchronized void quit()
	{
		quit("");
	}
	
	/**
	 * gets the default profile used for new connections
	 * @return default profile
	 */
	public Profile getDefaultProfile()
	{
		return defaultProfile;
	}
	
	/**
	 * sets the default profile to use for new connections
	 * @param profile default profile to use for connections
	 */
	public void setDefaultProfile(Profile profile)
	{
		this.defaultProfile = profile;
	}
	
	void removeSession(Session session)
	{
		sessionMap.remove(session.getRequestedConnection().getHostName());
		for(Iterator<SessionImpl>it = socChanMap.values().iterator(); it.hasNext();)
		{
			if(it.next().equals(session))
			{
				it.remove();
				return;
			}
		}
	}
	
	SessionImpl getSessionFor(Connection con)
	{
		for(SessionImpl session : sessionMap.values())
		{
			if(session.getConnection() == con) return session;
		}
		return null;
	}
	
	void addToEventQueue(IRCEvent event)
	{
		eventQueue.add(event);
	}

	void addToRelayList(IRCEvent event)
	{
		if(event == null)
		{
			new Exception().printStackTrace();
			quit("Null Pointers In my Code??! :(");
			return;
		}
		relayQueue.add(event);
	}
	
	
	private void startMainLoop()
	{
		dispatchTimer = new Timer();
		
		loopTimer = new Timer();
		
		TimerTask dispatchTask = new TimerTask()
		{
			public void run()
			{
				relayEvents();
				notifyWriteListeners();
			}
		};
		
		TimerTask loopTask = new TimerTask()
		{
			public void run()
			{
				makeConnections();
				doNetworkIO();
				parseEvents();
				checkServerConnections();
			}
		};
		
		loopTimer.schedule(loopTask, 0 , 200);
		
		dispatchTimer.schedule(dispatchTask, 0 , 200);
	}
	
	private void doNetworkIO()
	{
		try
		{
			if(selector.selectNow() > 0)
			{
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while(it.hasNext())
				{
					SelectionKey key = it.next(); 
					
					it.remove();
					
					if(!key.isValid())
					{
						continue;
					}
					
					if(key.isReadable())
					{
						socChanMap.get(key.channel()).getConnection().read();
					}
					else if(key.isWritable())
					{
						socChanMap.get(key.channel()).getConnection().doWrites();
					}
					else if(key.isConnectable())
					{
						finishConnection(key);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void finishConnection(SelectionKey key)
	{
		SocketChannel chan = (SocketChannel)key.channel();
		SessionImpl session = socChanMap.get(chan);
		
		if(chan.isConnectionPending())
		{
			try
			{
				if(session.getConnection().finishConnect())
				{
					session.setConnectionState(Session.State.HALF_CONNECTED);
					session.getConnection().login();
				}
				else
				{
					session.setConnectionState(Session.State.CONNECTING);
				}
			}
			catch (IOException e)
			{
				session.setConnectionState(State.DISCONNECTED);
				key.cancel();
				e.printStackTrace();
			}
		}
	}
	
	private void checkServerConnections()
	{
		synchronized (sessionMap)
		{
			for(SessionImpl session : sessionMap.values())
			{
				ConnectionState state = session.getConnectionState();
				
				if(state == null || state.getConState() != State.CONNECTED)
				{
					continue;
				}
				
				if(state.getPingState() == PingState.NEED_TO_PING)
				{
					session.getConnection().ping();
				}
			}
		}
	}
	
	private void parseEvents()
	{
		synchronized (eventQueue) 
		{
			if(eventQueue.isEmpty()) return;
			for(IRCEvent event : eventQueue)
			{
				parser.parseEvent(event);
			}
			eventQueue.clear();
		}
	}	
	
	private void relayEvents()
	{
		List<IRCEvent> events = new ArrayList<IRCEvent>();
		List<IRCEventListener>templisteners = new ArrayList<IRCEventListener>();
		Map<Type, List<Task>>tempTasks = new HashMap<Type, List<Task>>();
		
		synchronized (relayQueue)
		{
			events.addAll(relayQueue);
			relayQueue.clear();
		}
		

		for(IRCEvent event : events)
		{
			Session s = event.getSession();
			
			//if session is null , this means the session has been removed or
			// quit() in Session has been called , but not before a few
			//events could queue up for that session. So we should continue
			//to the next event
			if(s == null) continue;
			
			Map<Type, List<Task>> tasks = ((SessionImpl)s).getTasks(); 
			synchronized (tasks)
			{
				for(Iterator<List<Task>>it = tasks.values().iterator(); it.hasNext();)
				{
					List<Task> thisTasks = it.next();
					for(Iterator<Task>x = thisTasks.iterator(); x.hasNext();)
					{
						Task rmTask = x.next();
						if(rmTask.isCanceled())
						{
							x.remove();
						}
					}
				}
				tempTasks.putAll(tasks);
			}
			
			Collection<IRCEventListener> listeners = s.getIRCEventListeners();
			synchronized (listeners)
			{
				templisteners.addAll(listeners);
			}
			
			List<Task> typeTasks = tempTasks.get(event.getType());
			if(typeTasks == null) typeTasks = new ArrayList<Task>();
			
			List<Task> taskCopy = new ArrayList<Task>(typeTasks);
			
			List<Task>nullTasks = tempTasks.get(null);
			if(nullTasks != null)
			{
				taskCopy.addAll(nullTasks);
			}
			
			for(Task t : taskCopy)
			{
				//could put code here to catch exceptions caused
				//by lib users , this would keep the lib from crashing
				//for outside reasons.
				try
				{
					t.receiveEvent(event);
				}catch (Exception e) 
				{
					System.err.println("jerklib:Cought Client Exception");
					e.printStackTrace();
				}
			}
			
			for(IRCEventListener listener : templisteners)
			{
				//tasks above might do something stupid like set
				//event to null...
				if(event != null)listener.receiveEvent(event);
			}
			templisteners.clear();
			tempTasks.clear();
		}
	}
	
	private void notifyWriteListeners()
	{
		List<WriteRequestListener> list = new ArrayList<WriteRequestListener>();
		List<WriteRequest> wRequests = new ArrayList<WriteRequest>();
		
		synchronized (requestForWriteListenerEventQueue)
		{
			if(requestForWriteListenerEventQueue.isEmpty()) return;
			wRequests.addAll(requestForWriteListenerEventQueue);
			requestForWriteListenerEventQueue.clear();
		}
		
		synchronized (writeListeners)
		{
			list.addAll(writeListeners);
		}
		
		for(WriteRequestListener listener : list)
		{
			for(WriteRequest request : wRequests)
			{
				listener.receiveEvent(request);
			}
		}
		
	}
	
	private void makeConnections()
	{
		synchronized (sessionMap)
		{
			for(SessionImpl session : sessionMap.values())
			{
				if(session.getConnectionState() == null || 
						session.getConnectionState().getConState() == Session.State.DISCONNECTED)
				{
					
					long last = session.getLastRetry();
					long current = System.currentTimeMillis();
					if(last > 0 && current - last < 10000)
					{
						continue;
					}
					
					try
					{
						System.err.println("Trying to connect to " + session.getRequestedConnection().getHostName());
						session.retried();
						connect(session);
					}
					catch(UnresolvedAddressException e)
					{
						session.setConnectionState(Session.State.DISCONNECTED);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						session.setConnectionState(Session.State.DISCONNECTED);
					}
				}
			}
		}
	}
	
	private void connect(SessionImpl session) throws IOException
	{
    SocketChannel sChannel = SocketChannel.open();
    
    sChannel.configureBlocking(false);

    
    InetSocketAddress isa = new InetSocketAddress
		(
				session.getRequestedConnection().getHostName(),  
				session.getRequestedConnection().getPort()
		);
    
    
    sChannel.connect
    (
    		isa
    );
    
    sChannel.register(selector , sChannel.validOps());
    
    Connection con = new Connection(this , sChannel);
    session.setConnection(con);
    
    socChanMap.put(sChannel, session);
	}
}


