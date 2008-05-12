package jerklib;

import jerklib.Session.State;
import jerklib.events.ErrorEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.UnresolvedHostnameErrorEventImpl;
import jerklib.listeners.IRCEventListener;
import jerklib.listeners.WriteRequestListener;
import jerklib.parsers.DefaultInternalEventParser;
import jerklib.parsers.InternalEventParser;
import jerklib.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Collection;


/**
 * @author Jason Davis
 *         <p/>
 *         This class is used to control all Connections.
 *         <p/>
 *         Request new connections with this class
 */
public class ConnectionManager
{

    public static boolean debug,dontParse;
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
        if (version == null)
        {
            return;
        }
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
    final Map<String, Session> sessionMap = Collections.synchronizedMap(new HashMap<String, Session>());
    final Map<SocketChannel, Session> socChanMap = Collections.synchronizedMap(new HashMap<SocketChannel, Session>());

    /* event listener lists */
    private final List<WriteRequestListener> writeListeners = Collections.synchronizedList(new ArrayList<WriteRequestListener>(1));

    /* event queues */
    private final List<IRCEvent> eventQueue = new ArrayList<IRCEvent>();
    private final List<IRCEvent> relayQueue = new ArrayList<IRCEvent>();
    private final List<WriteRequest> requestForWriteListenerEventQueue = new ArrayList<WriteRequest>();

    /* internal event parser */
   // private InternalEventParser parser = new InternalEventParserImpl(this);
    private IRCEventListener internalEventHandler = new DefaultInternalEventHandler(this);
    private InternalEventParser internalEventParser = new DefaultInternalEventParser();
    
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
     * @see Profile
     */
    public ConnectionManager(Profile defaultProfile)
    {
        this.defaultProfile = defaultProfile;

        
        //if(dontParse)parser = new MinimalEventParser(this);
        
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

    /* this ctor is for testing purposes only */
    ConnectionManager()
    {
    }


    /**
     * get a list of Sessions
     *
     * @return Session list
     */
    public List<Session> getSessions()
    {
        return Collections.unmodifiableList(new ArrayList<Session>(sessionMap.values()));
    }

    /**
     * gets a session by name
     *
     * @param name session name - the hostname of the server this session is for
     * @return Session or null if no Session with name exists
     */
    public Session getSession(String name)
    {
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
     * @param hostName DNS name of host to connect to
     * @return the {@link Session} for this connection
     */
    public Session requestConnection(String hostName)
    {
        return requestConnection(hostName, 6667);
    }

    /**
     * request a new connection to a host
     *
     * @param hostName DNS name of host to connect to
     * @param port     port to use for connection
     * @return the {@link Session} for this connection
     */
    public Session requestConnection(String hostName, int port)
    {
        return requestConnection(hostName, port, defaultProfile.clone());
    }

    /**
     * request a new connection to a host
     *
     * @param hostName DNS name of host to connect to
     * @param port     port to use for connection
     * @param profile  profile to use for this connection
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
        
        session.setInternalParser(internalEventParser);
        
        sessionMap.put(hostName, session);

        return session;
    }

    /**
     * Closes all connections and shuts down manager
     *
     * @param quitMsg quit message
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
     * @param profile default profile to use for connections
     */
    public void setDefaultProfile(Profile profile)
    {
        this.defaultProfile = profile;
    }

    
    /**
     * Sets the InternalEventParser implementation to use
     * @param parser
     */
    public void setDefaultInternalEventHandler(IRCEventListener handler)
    {
    	internalEventHandler = handler;
    }
    
    public void setDefaultInternalEventParser(InternalEventParser parser)
    {
    	internalEventParser = parser;
    }
    
    public InternalEventParser getDefaultInternalEventParser()
    {
    	return internalEventParser;
    }
    
    public IRCEventListener getDefaultEventHandler()
    {
    	return internalEventHandler;
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
            {
                return session;
            }
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
        relayQueue.add(event);
    }


    void startMainLoop()
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

    void doNetworkIO()
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

    void finishConnection(SelectionKey key)
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

    void checkServerConnections()
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

    void parseEvents()
    {
        synchronized (eventQueue)
        {
            if (eventQueue.isEmpty())
            {
                return;
            }
            for (IRCEvent event : eventQueue)
            {
                IRCEvent newEvent = event.getSession().getInternalEventParser().receiveEvent(event);
                internalEventHandler.receiveEvent(newEvent);
            }
            eventQueue.clear();
        }
    }


    Map<Type, List<Task>> removeCanceled(Session session)
    {
        Map<Type, List<Task>> tasks = session.getTasks();
        synchronized (tasks)
        {
            for (Iterator<List<Task>> it = tasks.values().iterator(); it.hasNext();)
            {
                List<Task> thisTasks = it.next();
                for (Iterator<Task> x = thisTasks.iterator(); x.hasNext();)
                {
                    Task rmTask = x.next();
                    if (rmTask.isCanceled())
                    {
                        x.remove();
                    }
                }
            }
        }
        return tasks;
    }

    void relayEvents()
    {
        List<IRCEvent> events = new ArrayList<IRCEvent>();
        List<IRCEventListener> templisteners = new ArrayList<IRCEventListener>();
        Map<Type, List<Task>> tempTasks = new HashMap<Type, List<Task>>();

        synchronized (relayQueue)
        {
            events.addAll(relayQueue);
            relayQueue.clear();
        }


        for (IRCEvent event : events)
        {
            Session s = event.getSession();

            //if session is null , this means the session has been removed or
            // quit() in Session has been called , but not before a few
            //events could queue up for that session. So we should continue
            //to the next event
            if (s == null)
            {
                continue;
            }

            Collection<IRCEventListener> listeners = s.getIRCEventListeners();
            synchronized (listeners)
            {
                templisteners.addAll(listeners);
            }

            tempTasks.putAll(removeCanceled(s));

            List<Task> typeTasks = tempTasks.get(event.getType());
            if (typeTasks != null)
            {
                templisteners.addAll(typeTasks);
            }


            List<Task> nullTasks = tempTasks.get(null);
            if (nullTasks != null)
            {
                templisteners.addAll(nullTasks);
            }


            for (IRCEventListener listener : templisteners)
            {
                try
                {
                    listener.receiveEvent(event);
                }
                catch (Exception e)
                {
                    System.err.println("jerklib:Cought Client Exception");
                    e.printStackTrace();
                }
            }

            templisteners.clear();
            tempTasks.clear();
        }
    }

    void notifyWriteListeners()
    {
        List<WriteRequestListener> list = new ArrayList<WriteRequestListener>();
        List<WriteRequest> wRequests = new ArrayList<WriteRequest>();

        synchronized (requestForWriteListenerEventQueue)
        {
            if (requestForWriteListenerEventQueue.isEmpty())
            {
                return;
            }
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
                        //System.err.println("Trying to connect to " + session.getRequestedConnection().getHostName());
                        session.retried();
                        connect(session);
                    }
                    catch (UnresolvedAddressException e)
                    {
                        ErrorEvent error = new UnresolvedHostnameErrorEventImpl
                                (
                                        session,
                                        e.getMessage(),
                                        session.getRequestedConnection().getHostName(),
                                        e
                                );
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

        sChannel.connect
                (
                        new InetSocketAddress
                                (
                                        session.getRequestedConnection().getHostName(),
                                        session.getRequestedConnection().getPort()
                                )
                );

        sChannel.register(selector, sChannel.validOps());

        Connection con = new Connection(this, sChannel, session);
        session.setConnection(con);

        socChanMap.put(sChannel, session);
    }


}


