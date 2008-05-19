package jerklib;

import jerklib.events.IRCEvent.Type;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeAdjustment.Action;
import jerklib.listeners.IRCEventListener;
import jerklib.parsers.InternalEventParser;
import jerklib.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;


/**
 * @author mohadib
 */
public class Session extends RequestGenerator
{

    private final List<String> channelNames = new ArrayList<String>();
    private final List<IRCEventListener> listenerList = new ArrayList<IRCEventListener>();
    private final Map<Type, List<Task>> taskMap = new HashMap<Type, List<Task>>();
    private final RequestedConnection rCon;
    private Connection con;
    private final ConnectionManager conman;
    private boolean rejoinOnKick = true, profileUpdating, isAway;
    private Profile tmpProfile;
    private long lastRetry = -1, lastResponse = System.currentTimeMillis();
    private ServerInformation serverInfo = new ServerInformation();
    private State state = State.DISCONNECTED;
    private InternalEventParser parser;
    private IRCEventListener internalEventHandler;
    private List<ModeAdjustment>userModes = new ArrayList<ModeAdjustment>();
    
    public InternalEventParser getInternalEventParser()
    {
    	return parser;
    }
    
    public void setInternalParser(InternalEventParser parser)
    {
    	this.parser = parser;
    }
    
    public void setInternalEventHandler(IRCEventListener handler)
    {
    	internalEventHandler = handler;
    }
    
    public IRCEventListener getInternalEventHandler()
    {
    	return internalEventHandler;
    }
    
    
    void updateUserModes(List<ModeAdjustment>modes)
    {
    	for(ModeAdjustment ma : modes)
    	{
    		updateUserMode(ma);
    	}
    }
    
    
    /**
     * If Action is MINUS and the same mode exists with a PLUS Action
     * then just remove the PLUS mode ModeAdjustment from the collection.
     * 
     * If Action is MINUS and the same mode with PLUS does not exist
     * then add the MINUS mode to the ModeAdjustment collection
     * 
     * if Action is PLUS and the same mode exists with a MINUS Action
     * then remove MINUS mode and add PLUS mode
     * 
     * If Action is PLUS and the same mode with MINUS does not exist
     * then just add PLUS mode to collection
     * 
     * @param mode
     */
    private void updateUserMode(ModeAdjustment mode)
    {
    	int index = indexOfMode(mode.getMode(), userModes);
    	
    	if(mode.getAction() == Action.MINUS)
    	{
    		if(index != -1)
    		{
    			ModeAdjustment ma = userModes.remove(index);
    			if(ma.getAction() == Action.MINUS) 
    				userModes.add(ma);
    		}
    		else
    		{
    			userModes.add(mode);
    		}
    	}
    	else
    	{
    		if(index != -1) userModes.remove(index);
    		userModes.add(mode);
    	}
    }
    
    
    private int indexOfMode(char mode , List<ModeAdjustment>modes)
    {
    	for(int i = 0 ; i < modes.size(); i++)
    	{
    		ModeAdjustment ma = modes.get(i);
    		if(ma.getMode() == mode) return i;
    	}
    	return -1;
    }
    

    public List<ModeAdjustment> getUserModes()
    {
    	return new ArrayList<ModeAdjustment>(userModes);
    }
    
    
    
    /* a Map to index currently joined channels by name */
    private final Map<String, Channel> channelMap = new HashMap<String, Channel>();

    public void sayChannel(Channel channel, String msg)
    {
        super.sayChannel(msg, channel);
    }

    enum State
    {
        CONNECTED,
        CONNECTING,
        HALF_CONNECTED,
        DISCONNECTED,
        MARKED_FOR_REMOVAL,
        NEED_TO_PING,
        PING_SENT,
        NEED_TO_RECONNECT
    }

    Session(RequestedConnection rCon , ConnectionManager conman)
    {
        this.rCon = rCon;
        this.conman = conman;
    }

    /* general methods */

    public boolean isConnected()
    {
        return state == State.CONNECTED;
    }

    public boolean isRejoinOnKick()
    {
        return rejoinOnKick;
    }

    public void setRejoinOnKick(boolean rejoin)
    {
        rejoinOnKick = rejoin;
    }

    public void close(String quitMessage)
    {
        if (con != null)
        {
            con.quit(quitMessage);
            conman.removeSession(this);
        }
    }

    public String getNick()
    {
        return getRequestedConnection().getProfile().getActualNick();
    }

    public void changeProfile(Profile profile)
    {
        tmpProfile = profile;
        profileUpdating = true;
        super.changeNick(tmpProfile.getActualNick());
    }

    public void changeNick(String newNick)
    {
    	tmpProfile = rCon.getProfile().clone();
    	tmpProfile.setActualNick(newNick);
    	tmpProfile.setFirstNick(newNick);
    	profileUpdating = true;
    	super.changeNick(newNick);
    }
    
    public boolean isProfileUpdating()
    {
        return profileUpdating;
    }

    public void kick(String userName, String reason, Channel channel)
    {
        if (reason == null || reason.length() == 0)
        {
            reason = getNick();
        }
        super.kick(userName, reason, channel);
    }

    public boolean isAway()
    {
        return isAway;
    }

    public void setAway(String message)
    {
        isAway = true;
        super.setAway(message);
    }

    public void unsetAway()
    {
        /* if we're not away let's not bother even delegating */
        if (isAway)
        {
            super.unSetAway();
            isAway = false;
        }
    }

    /* methods to get information about connection and server */

    public ServerInformation getServerInformation()
    {
        return serverInfo;
    }

    public RequestedConnection getRequestedConnection()
    {
        return rCon;
    }

    public String getConnectedHostName()
    {
        return con.getHostName();
    }

    /* methods for adding/removing IRCEventListeners and Tasks */
    public void addIRCEventListener(IRCEventListener listener)
    {
        listenerList.add(listener);
    }

    public void removeIRCEventListener(IRCEventListener listener)
    {
        listenerList.remove(listener);
    }

    public Collection<IRCEventListener> getIRCEventListeners()
    {
        return Collections.unmodifiableCollection(listenerList);
    }

    /**
     * Chances are you'll never need this. (But tests will.)
     */
    protected void clearListeners()
    {

        listenerList.clear();
    }


    public void onEvent(Task task)
    {
        // null means task should be notified of all Events
        onEvent(task, null);
    }

    public void onEvent(Task task, Type type)
    {
        synchronized (taskMap)
        {
            if (!taskMap.containsKey(type))
            {
                List<Task> tasks = new ArrayList<Task>();
                tasks.add(task);
                taskMap.put(type, tasks);
            }
            else
            {
                if (!taskMap.get(type).contains(task))
                {
                    taskMap.get(type).add(task);
                }
            }
        }
    }

    Map<Type, List<Task>> getTasks()
    {
        return new HashMap<Type, List<Task>>(taskMap);
    }

    /**
     * Chances are you'll never need this. (But testing does.)
     */
    protected void clearTasks()
    {
        taskMap.clear();
    }

    public void removeTask(Task t)
    {
        synchronized (taskMap)
        {
            for (Iterator<Type> it = taskMap.keySet().iterator(); it.hasNext();)
            {
                List<Task> tasks = taskMap.get(it.next());
                if (tasks != null)
                {
                    tasks.remove(t);
                }
            }
        }
    }

    void updateProfileSuccessfully(boolean success)
    {
        if (success)
        {
            rCon.setProfile(tmpProfile);
        }
        tmpProfile = null;
        profileUpdating = false;
    }

    /* Channel methods */

    public List<Channel> getChannels()
    {
        return Collections.unmodifiableList(new ArrayList<Channel>(channelMap.values()));
    }

    public Channel getChannel(String channelName)
    {
        Channel chan = channelMap.get(channelName);
        return chan == null? channelMap.get(channelName.toLowerCase()) : chan;
    }

    public List<String> getChannelNames()
    {
        return Collections.unmodifiableList(channelNames);
    }

    void addChannel(Channel channel)
    {
        channelMap.put(channel.getName(), channel);
    }


    void removeChannel(Channel channel)
    {
        channelMap.remove(channel.getName());
    }


    void nickChanged(String oldNick, String newNick)
    {
        /*
          if (log.isLoggable(Level.INFO))
          {
              log.info("Looking for " + oldNick);
          }
          */
        synchronized (channelMap)
        {
            for (Channel chan : channelMap.values())
            {
                if (chan.getNicks().contains(oldNick))
                {
                    //	log.severe("Found nick in " + chan.getName());
                    chan.nickChanged(oldNick, newNick);
                }
            }
        }
    }

    public List<Channel> removeNickFromAllChannels(String nick)
    {
        List<Channel> returnList = new ArrayList<Channel>();
        for (Channel chan : channelMap.values())
        {
            if (chan.removeNick(nick))
            {
                returnList.add(chan);
            }
        }
        return returnList;
    }

    /* methods to track connection attempts */

    long getLastRetry()
    {
        return lastRetry;
    }

    void retried()
    {
        lastRetry = System.currentTimeMillis();
    }

    /* methods to get/set Connection object */
    void setConnection(Connection con)
    {
        this.con = con;
        super.setConnection(con);
    }

    Connection getConnection()
    {
        return con;
    }

    /* Methods to get and set the state of the session */

    void gotResponse()
    {
        lastResponse = System.currentTimeMillis();
        state = State.CONNECTED;
    }

    void pingSent()
    {
        state = State.PING_SENT;
    }

    void disconnected()
    {
        state = State.DISCONNECTED;
        if (con != null)
        {
            con.quit("");
            con = null;
        }
        channelNames.clear();
    }

    void connected()
    {
        gotResponse();
    }

    void connecting()
    {
        state = State.CONNECTING;
    }

    void halfConnected()
    {
        state = State.HALF_CONNECTED;
    }

    void markForRemoval()
    {
        state = State.MARKED_FOR_REMOVAL;
    }

    State getState()
    {
        long current = System.currentTimeMillis();
        
        if(state == State.DISCONNECTED) return state;
        
        if (current - lastResponse > 300000 && state == State.NEED_TO_PING)
        {
            state = State.NEED_TO_RECONNECT;
        }
        else if (current - lastResponse > 200000 && state != State.PING_SENT)
        {
            state = State.NEED_TO_PING;
        }

        return state;
    }

    
  	public boolean isChannelToken(String token)
  	{
  		ServerInformation serverInfo = getServerInformation();
  		String[] chanPrefixes = serverInfo.getChannelPrefixes();
  		for (String prefix : chanPrefixes)
  		{
  			if (token.startsWith(prefix)) { return true; }
  		}
  		return false;
  	}
    
    public int hashCode()
    {
        return rCon.hashCode();
    }

    public boolean equals(Object o)
    {
        if (o instanceof Session && o.hashCode() == hashCode())
        {
            return ((Session) o).getRequestedConnection().equals(rCon);
        }
        return false;
    }

}
