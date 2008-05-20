package jerklib;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.NickInUseEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.NickChangeEventImpl;
import jerklib.events.modes.ModeEvent;
import jerklib.listeners.IRCEventListener;
import static jerklib.events.IRCEvent.Type.*;

/**
 * Class that will only handle events that effect internal states/caches
 * Like channel nick lists. All events will be added to the ConnectionManager
 * for relaying.
 * 
 * @author mohadib
 *
 */
public class DefaultInternalEventHandler implements IRCEventListener
{
	private ConnectionManager manager;
	private Map<Type, IRCEventListener> stratMap = new HashMap<Type, IRCEventListener>();
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * @param manager 
	 */
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
	
	public IRCEventListener getEventHandler(Type type)
	{
		return stratMap.get(type);
	}
	
	
	public void receiveEvent(IRCEvent event)
	{
		IRCEventListener l = stratMap.get(event.getType());
		
		if(l != null)
		{
			l.receiveEvent(event);
		}
		else
		{
			Session session = event.getSession();
			String data = event.getRawEventData();
			String command = new EventToken(data).command();
		
			if(command.equals("PING"))
			{
				session.getConnection().pong(event);
			}
			else if(command.equals("PONG"))
			{
				session.getConnection().gotPong();
			}
		}
		
		manager.addToRelayList(event);
	}
	
	
	public void joinComplete(IRCEvent e)
	{
		JoinCompleteEvent jce = (JoinCompleteEvent)e;
		e.getSession().addChannel(jce.getChannel());
		jce.getSession().sayRaw("MODE " + jce.getChannel().getName());
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
	
	public void nickInUse(IRCEvent e)
	{
		Session session = e.getSession();
		if(!session.hasLoggedIn() && session.getShouldUseAltNicks())
		{
			Profile p = session.getRequestedConnection().getProfile();
			NickInUseEvent niu = (NickInUseEvent)e;
			String usedNick = niu.getInUseNick();
			String newNick = "";
			if(usedNick.equals(p.getFirstNick())) newNick = p.getSecondNick();
			else if(usedNick.equals(p.getSecondNick()))newNick = p.getThirdNick();
			if(newNick.length() > 0)
			{
				System.out.println("trying to change nick to " + newNick);
				session.changeNick(newNick);
			}
			else
			{
				System.err.println("Nicks exahusted");
			}
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
		String nick = token.arg(0);
		String profileNick = session.getNick();
		if(!nick.equalsIgnoreCase(profileNick))
		{
			Profile pi = (Profile)session.getRequestedConnection().getProfile();
			pi.setActualNick(nick);
			NickChangeEvent nce = new NickChangeEventImpl
			(
				token.data(),
				session,
				profileNick,
				nick,
				"",
				""
			);
			e.getSession().loginSuccess();
			manager.addToRelayList(nce);
		}
		
		ConnectionCompleteEvent ccEvent = (ConnectionCompleteEvent)e;
		session.getConnection().setHostName(ccEvent.getActualHostName());
		session.connected();
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
			me.getSession().updateUserModes(me.getModeAdjustments());
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
		
		stratMap.put(NICK_IN_USE, new IRCEventListener()
		{
			public void receiveEvent(IRCEvent e)
			{
				nickInUse(e);
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
