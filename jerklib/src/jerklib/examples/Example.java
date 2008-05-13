package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;
import jerklib.listeners.IRCEventListener;
import jerklib.parsers.ConnectionCompleteParser;
import jerklib.parsers.DefaultInternalEventParser;
import jerklib.parsers.JoinParser;
import jerklib.tasks.TaskImpl;

/**
 * @author mohadib A simple example that demonsrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
    private ConnectionManager manager;
    //TODO: change this to your channel name as to not spam our channel!
    private static final String CHANNEL_TO_JOIN = "#ubuntu";

    public Example()
    {
          /*
           * ConnectionManager takes a Profile to use for new connections. The profile
           * will contain the users name , nick , alt. nick 1 and. alt nick 2
           */
        manager = new ConnectionManager(new Profile("scripy", "dibz", "dibz_", "dibz__"));

        DefaultInternalEventParser parser = (DefaultInternalEventParser)manager.getDefaultInternalEventParser();
        parser.removeAllParsers();
        parser.addParser("001", new ConnectionCompleteParser());
        parser.addParser("JOIN", new JoinParser());
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
      * All events are sent as IRCEvents. You can check its actual type and cast
      * it to a more specific type.
      */
    public void receiveEvent(IRCEvent e)
    {
        if (e.getType() == Type.CONNECT_COMPLETE)
        {
            /* connection to server is complete */
            //e.getSession().join(CHANNEL_TO_JOIN);
            e.getSession().join("#jerklib");
        	
        }
        else if (e.getType() == Type.CHANNEL_MESSAGE)
        {
        	MessageEvent me = (MessageEvent)e;
        	System.out.println(me.getNick() + ":" + me.getMessage());
        }
        else if (e.getType() == Type.JOIN_COMPLETE)
        {
            JoinCompleteEvent jce = (JoinCompleteEvent) e;
            e.getSession().sayRaw("MODE #jerklib");
            /* say hello and version number */
           // jce.getChannel().say("Hello from Jerklib " + ConnectionManager.getVersion());
        }
        else if(e.getType() == Type.MODE_EVENT)
        {
        	ModeEvent me = (ModeEvent)e;
        	for(ModeAdjustment adj : me.getModeAdjustments())
        	{
        		System.out.println("MODE " + adj.toString());
        	}
        }
        else
        {
        		System.out.println(e.getType() + " " + e.getRawEventData());
        }

    }

    public static void main(String[] args)
    {
        new Example();
    }
}
