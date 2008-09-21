package jerklib;

import jerklib.Session.State;
import jerklib.events.ErrorEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.UnresolvedHostnameErrorEventImpl;
import jerklib.events.listeners.IRCEventListener;
import jerklib.events.listeners.WriteRequestListener;
import jerklib.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jason Davis <p/> This class is used to control all Connections. <p/> Request new connections with this class
 */
public class ConnectionManager
{
	Logger log = Logger.getLogger(this.getClass().getName());
	public static boolean debug;
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
			version = "unknown";
		}
	}

	/**
	 * Jerklib will answer CTCP VERSION requests automatically. This method allows users to prepend a string to the version rely.
	 * 
	 * @param version
	 *          The version string to use
	 */
	public static void setVersionString(String version)
	{
		if (version == null)
			return;
		extendedVersion = version;
	}

	/**
	 * gets the version string of library
	 * 
	 * @return Version string
	 */
	public static String getVersion()
	{
		if (extendedVersion.length() > 0)
		{
			return extendedVersion + " : " + version;
		}
		return version;
	}

	/* maps to index sessions by name and socketchannel */
	protected final Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<String, Session>());
	protected final Map<SocketChannel, Session> socChanMap = Collections.synchronizedMap(new HashMap<SocketChannel, Session>());

	/* event listener lists */
	private final List<WriteRequestListener> writeListeners = Collections.synchronizedList(new ArrayList<WriteRequestListener>(1));

	/* event queues */
	private final ConcurrentLinkedQueue<IRCEvent> eventQueue = new ConcurrentLinkedQueue<IRCEvent>();
	private final ConcurrentLinkedQueue<IRCEvent> relayQueue_ = new ConcurrentLinkedQueue<IRCEvent>();
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
	protected Selector selector;

	/**
	 * Only constructor - Takes a profile to use as default profile for new Connections
	 * 
	 * @param defaultProfile
	 *          default user profile
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
		for (Session ses : sessionMap.values())
		{
			sessions.add(ses);
		}
		return Collections.unmodifiableList(sessions);
	}

	/**
	 * gets a session by name
	 * 
	 * @param name
	 *          session name - the hostname of the server this session is for
	 * @return Session or null if no Session with name exists
	 */
	public Session getSession(String name)
	{
		return sessionMap.get(name);
	}

	/**
	 * Adds a listener to be notified of all writes
	 * 
	 * @param listener
	 *          listener to be notified
	 */
	public void addWriteRequestListener(WriteRequestListener listener)
	{
		writeListeners.add(listener);
	}

	/**
	 * gets an unmodifiable list of WriteListeners
	 * 
	 * @return listeners
	 */
	public List<WriteRequestListener> getWriteListeners()
	{
		return Collections.unmodifiableList(writeListeners);
	}

	/**
	 * request a new connection to a host with the default port of 6667
	 * 
	 * @param hostName
	 *          DNS name of host to connect to
	 * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName)
	{
		return requestConnection(hostName, 6667);
	}

	/**
	 * request a new connection to a host
	 * 
	 * @param hostName
	 *          DNS name of host to connect to
	 * @param port
	 *          port to use for connection
	 * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName, int port)
	{
		try
		{
			return requestConnection(hostName, port, defaultProfile.clone());
		}
		catch (CloneNotSupportedException e)
		{
			log.log(Level.SEVERE, "Profile wasn't cloneable", e);
			throw new Error("Profile was not cloneable: WTF?", e);
		}
	}

	/**
	 * request a new connection to a host
	 * 
	 * @param hostName
	 *          DNS name of host to connect to
	 * @param port
	 *          port to use for connection
	 * @param profile
	 *          profile to use for this connection
	 * @return the {@link Session} for this connection
	 */
	public Session requestConnection(String hostName, int port, Profile profile)
	{
		if (sessionMap.containsKey(hostName))
		{
			throw new IllegalArgumentException("Duplicate hostnames are not allowed");
		}

		RequestedConnection rCon = new RequestedConnection(hostName, port, profile);
		Session session = new Session(rCon);

		if (sessionMap.containsValue(session))
		{
			throw new IllegalArgumentException("Already connected to " + hostName + " on same port with same profile");
		}

		sessionMap.put(hostName, session);

		return session;
	}

	/**
	 * Closes all connections and shuts down manager
	 * 
	 * @param quitMsg
	 *          quit message
	 */
	public synchronized void quit(String quitMsg)
	{

		loopTimer.cancel();

		dispatchTimer.cancel();

		for (Session session : sessionMap.values())
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
	 * Closes all Sessions and exits library
	 */
	public synchronized void quit()
	{
		quit("");
	}

	/**
	 * gets the default profile used for new connections
	 * 
	 * @return default profile
	 */
	public Profile getDefaultProfile()
	{
		return defaultProfile;
	}

	/**
	 * sets the default profile to use for new connections
	 * 
	 * @param profile
	 *          default profile to use for connections
	 */
	public void setDefaultProfile(Profile profile)
	{
		this.defaultProfile = profile;
	}

	void removeSession(Session session)
	{
		sessionMap.remove(session.getRequestedConnection().getHostName());
		for (Iterator<Session> it = socChanMap.values().iterator(); it.hasNext();)
		{
			if (it.next().equals(session))
			{
				it.remove();
				return;
			}
		}
	}

	Session getSessionFor(Connection con)
	{
		for (Session session : sessionMap.values())
		{
			if (session.getConnection() == con)
				return session;
		}
		return null;
	}

	void addToEventQueue(IRCEvent event)
	{
		eventQueue.add(event);
	}

	void addToRelayList(IRCEvent event)
	{
		if (event == null)
		{
			new Exception().printStackTrace();
			quit("Null Pointers ?? In my Code??! :(");
			return;
		}
		assert relayQueue_.add(event);
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

		loopTimer.schedule(loopTask, 0, 200);

		dispatchTimer.schedule(dispatchTask, 0, 200);
	}

	private void doNetworkIO()
	{
		try
		{
			if (selector.selectNow() > 0)
			{
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext())
				{
					SelectionKey key = it.next();

					it.remove();

					if (!key.isValid())
					{
						continue;
					}

					if (key.isReadable())
					{
						socChanMap.get(key.channel()).getConnection().read();
					}
					if (key.isWritable())
					{
						socChanMap.get(key.channel()).getConnection().doWrites();
					}
					if (key.isConnectable())
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
		SocketChannel chan = (SocketChannel) key.channel();
		Session session = socChanMap.get(chan);

		if (chan.isConnectionPending())
		{
			try
			{
				if (session.getConnection().finishConnect())
				{
					session.halfConnected();
					session.getConnection().login();
				}
				else
				{
					session.connecting();
				}
			}
			catch (IOException e)
			{
				session.markForRemoval();
				key.cancel();
				e.printStackTrace();
			}
		}
	}

	private void checkServerConnections()
	{
		synchronized (sessionMap)
		{
			for (Iterator<Session> it = sessionMap.values().iterator(); it.hasNext();)
			{
				Session session = it.next();
				State state = session.getState();

				if (state == State.MARKED_FOR_REMOVAL)
				{
					it.remove();
				}
				else if (state == State.NEED_TO_PING)
				{
					session.getConnection().ping();
				}
			}
		}
	}

	private void parseEvents()
	{
		IRCEvent event = null;
		while ((event = eventQueue.poll()) != null)
		{
			parser.parseEvent(event);
		}
	}

	void relayEvents()
	{
		// List<IRCEvent> events = new ArrayList<IRCEvent>();
		// List<IRCEventListener> templisteners = new ArrayList<IRCEventListener>();
		IRCEvent event = null;

		while ((event = relayQueue_.poll()) != null)
		{
			Session s = event.getSession();

			// if session is null , this means the session has been removed or
			// quit() in Session has been called , but not before a few
			// events could queue up for that session. So we should continue
			// to the next event
			if (s == null)
				continue;

			final Collection<IRCEventListener> listeners = s.getIRCEventListeners();

			List<Task> typeTasks = s.getTasks().get(event.getType());
			if (typeTasks == null)
			{
				typeTasks = new ArrayList<Task>();
			}
			List<Task> taskCopy = new ArrayList<Task>(typeTasks);

			List<Task> nullTasks = s.getTasks().get(Type.DEFAULT);
			if (nullTasks != null)
			{
				taskCopy.addAll(nullTasks);
			}

			for (Task t : taskCopy)
			{
				// could put code here to catch exceptions caused
				// by lib users , this would keep the lib from crashing
				// for outside reasons.
				if (!t.isCancelled())
				{
					try
					{
						t.receiveEvent(event);
					}
					catch (Exception e)
					{
						System.err.println("jerklib:Cought Client Exception");
						e.printStackTrace();
					}
				}
			}

			for (IRCEventListener listener : listeners)
			{
				listener.receiveEvent(event);
			}
			// tempTasks.clear();
		}
	}

	private void notifyWriteListeners()
	{
		List<WriteRequestListener> list = new ArrayList<WriteRequestListener>();
		List<WriteRequest> wRequests = new ArrayList<WriteRequest>();

		synchronized (requestForWriteListenerEventQueue)
		{
			if (requestForWriteListenerEventQueue.isEmpty())
				return;
			wRequests.addAll(requestForWriteListenerEventQueue);
			requestForWriteListenerEventQueue.clear();
		}

		synchronized (writeListeners)
		{
			list.addAll(writeListeners);
		}

		for (WriteRequestListener listener : list)
		{
			for (WriteRequest request : wRequests)
			{
				listener.receiveEvent(request);
			}
		}

	}

	void makeConnections()
	{
		synchronized (sessionMap)
		{
			for (Iterator<Session> it = sessionMap.values().iterator(); it.hasNext();)
			{
				Session session = it.next();

				State state = session.getState();
				if (state == State.NEED_TO_RECONNECT)
				{
					session.disconnected();
				}

				if (state == State.DISCONNECTED)
				{
					long last = session.getLastRetry();
					long current = System.currentTimeMillis();
					if (last > 0 && current - last < 10000)
					{
						continue;
					}

					try
					{
						System.err.println("Trying to connect to " + session.getRequestedConnection().getHostName());
						session.retried();
						connect(session);
					}
					catch (UnresolvedAddressException e)
					{
						ErrorEvent error = new UnresolvedHostnameErrorEventImpl(session, e.getMessage(), session.getRequestedConnection().getHostName(), e);
						addToRelayList(error);
						session.markForRemoval();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						session.disconnected();
					}
				}
			}
		}
	}

	void connect(Session session) throws IOException
	{
		SocketChannel sChannel = SocketChannel.open();

		sChannel.configureBlocking(false);

		sChannel.connect(new InetSocketAddress(session.getRequestedConnection().getHostName(), session.getRequestedConnection().getPort()));

		sChannel.register(selector, sChannel.validOps());

		Connection con = new Connection(this, sChannel, session);
		session.setConnection(con);

		socChanMap.put(sChannel, session);
	}

}