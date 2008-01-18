package jerklib.examples.jerkbot2.operations;

import jerklib.events.IRCEvent;


public interface BotOperation {

    /**
     * This method is responsible for differentiating between whether
     * or not the bot was addressed.
     * 
     */
    public void handleMessage(IRCEvent e);
}
