package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.TaskImpl;

/**
 * @author mohadib A simple example that demonsrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
    private ConnectionManager manager;

    public Example()
    {
        /*
           * ConnectionManager takes a Profile to use for new connections. The profile
           * will contain the users name , nick , alt. nick 1 and. alt nick 2
           */
        manager = new ConnectionManager(new ProfileImpl("scripy", "dibz", "dibz_", "dibz__"));

        /*
           * One instance of ConnectionManager can connect to many IRC networks.
           * ConnectionManager#requestConnection(String) will return a Session object.
           * The Session is the main way users will interact with this library and IRC
           * networks
           */
        Session session = manager.requestConnection("irc.freenode.net");

        /*
           * JerkLib fires IRCEvents to notify users of the lib of incoming events
           * from a connected IRC server.
           */
        session.addIRCEventListener(this);

        /*
           * Tasks are a way to filter out events you do not care about. This Task
           * simply auto joins a channel to which we are invited. The parameter to the
           * TaskImpl constructor is the task name, it is how the task is identified
           * by jerklib internally.
           */
        session.onEvent(new TaskImpl("invite")
        {
            public void receiveEvent(IRCEvent e)
            {
                InviteEvent ie = (InviteEvent) e;
                e.getSession().join(ie.getChannelName());
            }
        }, Type.INVITE_EVENT);
    }

    /*
      * This method is for implementing an IRCEventListener. This method will be
      * called anytime Jerklib parses an event from the Session its attached to.
      * All events are sent as IRCEvents. You can check its actually type and cast
      * it to a more specific type.
      */
    public void receiveEvent(IRCEvent e)
    {
        if (e.getType() == Type.CONNECT_COMPLETE)
        {
            /* connection to server is complete */
            e.getSession().join("#jerklib");
        }
        else if (e.getType() == Type.CHANNEL_MESSAGE)
        {
        	MessageEvent me = (MessageEvent)e;
        	//System.out.println(me.getNick() + ":" + me.getMessage());
        }
        else if (e.getType() == Type.JOIN_COMPLETE)
        {
            JoinCompleteEvent jce = (JoinCompleteEvent) e;
            if (jce.getChannel().getName().equals("#jerklib"))
            {
                /* say hello and version number */
               jce.getChannel().say("Hello from Jerklib " + ConnectionManager.getVersion());
            }
        }
        else
        {
        	System.out.println(e.getRawEventData());
        }

    }

    public static void main(String[] args)
    {
        new Example();
    }
}
