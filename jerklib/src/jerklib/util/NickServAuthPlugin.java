package jerklib.util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;
import jerklib.events.modes.ModeAdjustment.Action;
import jerklib.tasks.TaskImpl;

public class NickServAuthPlugin extends TaskImpl
{
	private final Session session;
	private final String pass;
	private final char identMode;
	private final List<String> channels;
	private boolean authed;
	
	public NickServAuthPlugin
	(
		String pass , 
		char identMode,
		Session session,
		List<String>channels
	)
	{
		super("NickServAuth");
		this.pass = pass;
		this.identMode = identMode;
		this.session = session;
		this.channels = channels;
	}
	
	public void receiveEvent(IRCEvent e)
	{
		if(e.getType() == Type.CONNECT_COMPLETE)connectionComplete(e);
		else if(e.getType() == Type.MODE_EVENT)mode(e);
	}
	
	public void mode(IRCEvent e)
	{
		ModeEvent me = (ModeEvent)e;
		if(me.getModeType() == ModeEvent.ModeType.USER)
		{
			for(ModeAdjustment ma : me.getModeAdjustments())
			{
				if(ma.getMode() == identMode && ma.getAction() == Action.PLUS)
				{
					authed = true;
					joinChannels();
					taskComplete(new Boolean(true));
				}
			}
		}
	}
	
	private void connectionComplete(IRCEvent e)
	{
		authed = false;
		e.getSession().sayPrivate( "nickserv" , "identify " + pass);
		final Timer t = new Timer();
		t.schedule(new TimerTask()
		{
			public void run()
			{
				if(!authed)
				{
					taskComplete(new Boolean(false));
				}
				this.cancel();
				t.cancel();
			}
		}, 40000 );
	}
	
	private void joinChannels()
	{
		for(String name : channels)
		{
			session.join(name);
		}
	}
	
}
