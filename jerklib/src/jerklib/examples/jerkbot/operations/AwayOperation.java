package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class utilizes the new away functionality in jerklib
 * @author Robert O'Connor
 */
public class AwayOperation implements BotOperation {
    private final Pattern p;
    public AwayOperation() {
        p = Pattern.compile("^~say\\s+(.*)$");
    }

    /**
     * This method is responsible for differentiating between whether
     * or not the bot was addressed.
     */
    public void handleMessage(IRCEvent e) {
        if (e instanceof ChannelMsgEvent) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String message = event.getMessage();
            Matcher m = p.matcher(message);
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
