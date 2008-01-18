package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ModeEvent;
import jerklib.events.TopicEvent;
import jerklib.events.PrivateMsgEvent;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.InviteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.listeners.IRCEventListener;

import java.text.SimpleDateFormat;


public class Example implements IRCEventListener
{
	ConnectionManager manager;
	
	public Example()
	{
		
		manager = new ConnectionManager(new ProfileImpl("scripy" , "scripy" , "scripy1" , "scrippy2"));
		
		Session session = manager.requestConnection("irc.freenode.net");
		
		session.addIRCEventListener(this);
		session.setRejoinOnKick(true);
		session.setRejoinOnReconnect(true);
	}

	public void recieveEvent(IRCEvent e)
	{
		if(e.getType() == IRCEvent.Type.DEFAULT)
		{
			System.err.println(e.getRawEventData());
		}
		else if(e.getType() == IRCEvent.Type.READY_TO_JOIN)
		{
			//e.getSession().whois("mohadib");
			e.getSession().joinChannel("#jerklib2");
			e.getSession().joinChannel("#jerklib3");
            e.getSession().joinChannel("#jerklib");
            /*e.getSession().joinChannel("#ubuntu");
			e.getSession().joinChannel("#debian");
			e.getSession().joinChannel("#perl");*/
            //e.getSession().rawSay("LIST ##swing");
        }
		else if(e.getType() == IRCEvent.Type.JOIN_COMPLETE)
		{
            JoinCompleteEvent event = (JoinCompleteEvent)e;
            e.getSession().channelSay(event.getChannel().getName(),"Hai 2u");
        }
        else if(e.getType() == IRCEvent.Type.INVITE_EVENT) {
            InviteEvent event = (InviteEvent)e;
            e.getSession().joinChannel(event.getChannel());
            e.getSession().channelSay(event.getChannel(),"You rang?!?");
        }
        else if(e.getType() == IRCEvent.Type.TOPIC)
		{
			TopicEvent te = (TopicEvent)e;
            System.out.println("Topic for " + te.getChannel().getName() + " " + te.getTopic() + " set by " + te.getSetBy() + " set when:" + te.getSetWhen());
		}
		else if(e.getType() == IRCEvent.Type.CHANNEL_MESSAGE)
		{
			System.out.println("Good Chan Msg: " +e.getRawEventData());
            ChannelMsgEvent event = (ChannelMsgEvent)e;
            System.out.println("Nick: "+event.getNick());
            System.out.println("User Name: "+event.getUserName());
            System.out.println("Host Name: "+event.getHostName());
            System.out.println("Channel Name: "+event.getChannel().getName());
            System.out.println("Message: "+event.getMessage());
            if(event.getMessage().startsWith("~say")) {
                e.getSession().channelSay(event.getChannel().getName(),event.getMessage().substring("~say".length()));
            }
        }
		else if(e.getType() == IRCEvent.Type.PRIVATE_MESSAGE)
		{
			System.out.println("Good Prv: " +e.getRawEventData());
            PrivateMsgEvent event = (PrivateMsgEvent)e;
            System.out.println("Host Name: "+event.getHostName());
            System.out.println("User Name: "+event.getUserName());
        }
		else if(e.getType() == IRCEvent.Type.MODE_EVENT)
		{
			ModeEvent me = (ModeEvent)e;
			if(me.getUser().equals(e.getSession().getNick()) && me.getMode().equalsIgnoreCase("+o"))
			{
				me.getChannel().setTopic("A WINNAR IS ME");
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Example();
	}
	
}
