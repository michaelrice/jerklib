package jerklib.examples;

import java.util.logging.Logger;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.ServerInformation;
import jerklib.Session;
import jerklib.tasks.TaskImpl;
import jerklib.ServerInformation.ModeType;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;

/**
 * @author mohadib A simple example that demonsrates how to use JerkLib
 */
public class Example implements IRCEventListener
{
	Logger log = Logger.getLogger(this.getClass().getName());
	private ConnectionManager manager;

	public Example()
	{
		/*
		 * ConnectionManager takes a Profile to use for new connections. The profile
		 * will contain the users nick , real name , alt. nick 1 and. alt nick 2
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
		if (e.getType() == Type.CHANNEL_MESSAGE)
		{

			MessageEvent me = (MessageEvent) e;
			if (me.getMessage().equals("*server info"))
			{
				ServerInformation info = e.getSession().getServerInformation();
				me.getChannel().say("Name:" + info.getServerName());
				me.getChannel().say("IRCD:" + info.getIrcdString());
				me.getChannel().say("CaseMapping:" + info.getCaseMapping());

				String modes = "";

				for (String mode : info.getModes(ModeType.ALL))
				{
					modes += mode;
				}
				me.getChannel().say("Supported Modes:" + modes);
			}
		}
		else if (e.getType() == Type.CONNECT_COMPLETE)
		{
			/* connection to server is complete */
			log.info("Joining");
			// e.getSession().joinChannel("#sand-irc");
			e.getSession().join("#jerklib");
			// e.getSession().joinChannel("#ubuntu");
			// e.getSession().joinChannel("#debian");
			// e.getSession().joinChannel("#perl");
		}
		if (e.getType() == Type.NICK_IN_USE)
		{
			NickInUseEvent niu = (NickInUseEvent) e;
			log.info("Nick In Use " + niu.getInUseNick());
		}
		else if (e.getType() == Type.CHANNEL_MESSAGE)
		{
			MessageEvent me = (MessageEvent) e;
			if (me.getMessage().equals("*server info"))
			{
				ServerInformation info = e.getSession().getServerInformation();
				me.getChannel().say("Name:" + info.getServerName());
				me.getChannel().say("IRCD:" + info.getIrcdString());
				me.getChannel().say("CaseMapping:" + info.getCaseMapping());

				String modes = "";

				for (String mode : info.getModes(ModeType.ALL))
				{
					modes += mode;
				}
				me.getChannel().say("Supported Modes:" + modes);
			}
			else
			{
				System.err.println("NO MATCH " + me.getMessage());
			}
		}
		else if (e.getType() == Type.SERVER_INFORMATION)
		{
			ServerInformationEvent se = (ServerInformationEvent) e;
			ServerInformation info = se.getServerInformation();
			System.err.println("IRCD :" + info.getIrcdString());
			System.err.println("Name :" + info.getServerName());
			System.err.println("Case Mapping :" + info.getCaseMapping());
			System.err.println("Max Chan Name :" + info.getMaxChannelNameLength());

			for (String s : info.getChannelPrefixes())
			{
				log.info("Prefix:" + s);
			}

		}
		else if (e.getType() == Type.TOPIC)
		{
			log.info("TOPIC EVENT");
		}
		else if (e.getType() == Type.JOIN_COMPLETE)
		{
			log.info("JOIN COMPLETE");
			JoinCompleteEvent jce = (JoinCompleteEvent) e;
			if (jce.getChannel().getName().equals("#sand-irc"))
			{
				/* say hello and version number */
				e.getSession().notice(jce.getChannel().getName(), "Hello from Jerklib " + ConnectionManager.getVersion());
			}
		}
		else if (e.getType() == Type.MODE_EVENT)
		{
			ModeEvent me = (ModeEvent) e;
			log.info("MODE Set By:" + me.setBy());
			log.info("MODE Channel:" + me.getChannel().getName());
			for (String mode : me.getModeMap().keySet())
			{
				for (String target : me.getModeMap().get(mode))
				{
					log.info("MODE " + mode + "->" + target);
				}
			}
		}

	}

	public static void main(String[] args)
	{
		new Example();
	}
}
