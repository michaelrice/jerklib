package jerklib.examples;

import jerklib.events.listeners.BaseListener;
import jerklib.events.MessageEvent;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;

public class BaseListenerExample extends BaseListener {
    public BaseListenerExample() {
        ConnectionManager manager = new ConnectionManager(new ProfileImpl("ble", "ble", "ble_", "ble__"));
		Session session = manager.requestConnection("irc.freenode.net");
		session.addIRCEventListener(this);
    }

    @Override
    protected void handleJoinCompleteEvent(JoinCompleteEvent event) {
        event.getSession().sayChannel(event.getChannel(), "Hello from BaseListenerExample");                
    }

    @Override
    protected void handleConnectComplete(ConnectionCompleteEvent event) {
        event.getSession().join("#jerklib");
    }

    @Override
    protected void handleChannelMessage(MessageEvent event) {
        log.info(event.getChannel()+":"+event.getNick()+":"+event.getMessage());
        if("now die".equalsIgnoreCase(event.getMessage())) {
            event.getSession().sayChannel(event.getChannel(), "Okay, fine, I'll die");
            System.exit(0);
    }
    }

    public static void main(String[] args) {
        new BaseListenerExample();
    }
}
