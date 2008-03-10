package jerklib.events.listeners;

import java.util.logging.Logger;

import jerklib.events.*;
import jerklib.events.IRCEvent.Type;

public abstract class BaseListener implements IRCEventListener {
	Logger log=Logger.getLogger(this.getClass().getName());

	@Override
	public void receiveEvent(IRCEvent e) {
		Type t=e.getType();
		if(Type.AWAY_EVENT.equals(t)) {
			handleAwayEvent((AwayEvent) e);
		}
		if(Type.CHANNEL_LIST_EVENT.equals(t)) {
			handleChannelListEvent((ChannelListEvent)e);
		}
        if(Type.CHANNEL_MESSAGE.equals(t)) {
            handleChannelMessage((MessageEvent)e);
        }
        if(Type.CONNECT_COMPLETE.equals(t)) {
            handleConnectComplete((ConnectionCompleteEvent)e);
        }
        if(Type.CTCP_EVENT.equals(t)) {
            handleCtcpEvent((CtcpEvent)e);
        }
        if(Type.ERROR.equals(t)) {
            handleErrorEvent((ErrorEvent)e);
        }
        if(Type.INVITE_EVENT.equals(t)) {
            handleInviteEvent((InviteEvent)e);
        }
        if(Type.JOIN.equals(t)) {
            handleJoinEvent((JoinEvent)e);
        }
        if(Type.JOIN_COMPLETE.equals(t)) {
            handleJoinCompleteEvent((JoinCompleteEvent)e);
        }
        if(Type.KICK_EVENT.equals(t)) {
            handleKickEvent((KickEvent)e);
        }
        if(Type.MODE_EVENT.equals(t)) {
            handleModeEvent((ModeEvent)e);
        }
        if(Type.MOTD.equals(t)) {
            handleMotdEvent((MotdEvent)e);
        }
        
    }

    private void handleMotdEvent(MotdEvent event) {
    }

    private void handleModeEvent(ModeEvent event) {
    }

    private void handleKickEvent(KickEvent event) {
    }

    private void handleJoinCompleteEvent(JoinCompleteEvent event) {
    }

    private void handleJoinEvent(JoinEvent event) {        
    }

    private void handleInviteEvent(InviteEvent event) {
    }

    private void handleErrorEvent(ErrorEvent error) {
    }

    private void handleCtcpEvent(CtcpEvent event) {        
    }

    private void handleConnectComplete(ConnectionCompleteEvent event) {        
    }

    private void handleChannelMessage(MessageEvent event) {        
    }

    private void handleChannelListEvent(ChannelListEvent e) {
	}

	public void handleAwayEvent(AwayEvent e) {
	}


}
