package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwayOperation implements BotOperation {
    /**
     * This method is responsible for differentiating between whether
     * or not the bot was addressed.
     */
    public void handleMessage(IRCEvent e) {
        if (e instanceof ChannelMsgEvent) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String message = event.getMessage();
            Matcher m = Pattern.compile("^~away\\s+(.*)$").matcher(message);
            if (m.matches()) {
                //~away I'm doing the hokey-pokey! 
                e.getSession().setAway(m.group(1));
            }else if(message.matches("^~away.*$")) {
                // ~away with no arguments
                e.getSession().unsetAway();
            }


        }

    }

}
