package jerklib;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jerklib.ServerInformation.ModeType;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.NickChangeEventImpl;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;
import jerklib.listeners.IRCEventListener;
import jerklib.parsers.NoticeParser;
import jerklib.tokens.EventToken;
import static jerklib.events.IRCEvent.Type.*;

public class DefaultInternalEventHandler implements IRCEventListener
{
	private ConnectionManager manager;
	private Map<Type, IRCEventListener> stratMap = new HashMap<Type, IRCEventListener>();
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public DefaultInternalEventHandler(ConnectionManager manager)
	{
		this.manager = manager;
		initStratMap();
	}
	
	
	public void addEventHandler(Type type , IRCEventListener listener)
	{
		stratMap.put(type, listener);
	}
	
	public boolean removeEventHandler(Type type)
	{
		return stratMap.remove(type) != null;
	}
	
	
	public void receiveEvent(IRCEvent event)
	{

		IRCEventListener l = stratMap.get(event.getType());
		if(l != null)
		{
			l.receiveEvent(event);
			manager.addToRelayList(event);
			return;
		}
		
		Session session = event.getSession();
		String data = event.getRawEventData();
		EventToken eventToken = new EventToken(data);

		
			if (data.matches("^PING.*"))
			{
				session.getConnection().pong(event);
			}
			else if (data.matches(".*PONG.*"))
			{
				session.getConnection().gotPong();
			}
			else if (data.matches("^NOTICE\\s+(.*$)$"))
			{
				event = new NoticeParser().createEvent(eventToken, event);
			}
			
			manager.addToRelayList(event);
	}
	
	
	public void joinComplete(IRCEvent e)
	{
		JoinCompleteEvent jce = (JoinCompleteEvent)e;
		e.getSession().addChannel(jce.getChannel());
	}
	
	public void join(IRCEvent e)
	{
		JoinEvent je = (JoinEvent)e;
		je.getChannel().addNick(je.getNick());
	}
	
	public void quit(IRCEvent e)
	{
		QuitEvent qe = (QuitEvent)e;
		e.getSession().removeNickFromAllChannels(qe.getNick());
	}
	
	public void part(IRCEvent e)
	{
		PartEvent pe = (PartEvent)e;
		if(!pe.getChannel().removeNick(pe.getWho()))
		{
			log.severe("Could Not remove nick " + pe.getWho() + " from " + pe.getChannelName());
		}
		if (pe.getWho().equalsIgnoreCase(e.getSession().getNick()))
		{
			pe.getSession().removeChannel(pe.getChannel());
		}
	}
	
	public void nick(IRCEvent e)
	{
		NickChangeEvent nce = (NickChangeEvent)e;
		e.getSession().nickChanged(nce.getOldNick(), nce.getNewNick());
		if(nce.getOldNick().equals(e.getSession().getNick()))
		{
			nce.getSession().updateProfileSuccessfully(true);
		}
	}
	
	public void kick(IRCEvent e)
	{
		KickEvent ke = (KickEvent)e;
		if (!ke.getChannel().removeNick(ke.getWho()))
		{
			log.info("COULD NOT REMOVE NICK " + ke.getWho() + " from channel " + ke.getChannel().getName());
		}

		Session session = e.getSession();
		if (ke.getWho().equals(session.getNick()))
		{
			session.removeChannel(ke.getChannel());
			if (session.isRejoinOnKick())
			{
				session.join(ke.getChannel().getName());
			}
		}
	}
	
	public void connectionComplete(IRCEvent e)
	{
		/* sometimes the server will change the nick when connecting
		 * for instance , if the nick is too long it will be trunckated
		 * need to check if this happend and send a nick update event
		 */
		EventToken token = new EventToken(e.getRawEventData());
		Session session = e.getSession();
		String nick = token.getWordTokens().get(2).data;
		String profileNick = session.getNick();
		if(!nick.equalsIgnoreCase(profileNick))
		{
			Profile pi = (Profile)session.getRequestedConnection().getProfile();
			pi.setActualNick(nick);
			NickChangeEvent nce = new NickChangeEventImpl
			(
				token.getData(),
				session,
				profileNick,
				nick,
				"",
				""
			);
			manager.addToRelayList(nce);
		}
		
		ConnectionCompleteEvent ccEvent = (ConnectionCompleteEvent)e;
		session.getConnection().loginSuccess();
		session.getConnection().setHostName(ccEvent.getActualHostName());
	}
	
	
	/*
	 *handle channel and user modes
	 */
	public void mode(IRCEvent event)
	{
	
		ModeEvent me = (ModeEvent)event;
		if(me.getModeType() == ModeEvent.ModeType.CHANNEL)
		{
			// update channel modes
			me.getChannel().updateModes(me.getModeAdjustments());
		}
		else
		{
			//user mode
		}
	}
	
	
	
	private void initStratMap()
	{
		stratMap.put(CONNECT_COMPLETE, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				connectionComplete(e);
			}
		});
		
		stratMap.put(JOIN_COMPLETE, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				joinComplete(e);
			}
		});
		
		stratMap.put(JOIN, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				join(e);
			}
		});
		
		stratMap.put(QUIT, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				quit(e);
			}
		});
		
		stratMap.put(PART, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				part(e);
			}
		});
		
		stratMap.put(NICK_CHANGE, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				nick(e);
			}
		});
		
		stratMap.put(KICK_EVENT, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				kick(e);
			}
		});
		
		stratMap.put(MODE_EVENT, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				mode(e);
			}
		});
	}
}
