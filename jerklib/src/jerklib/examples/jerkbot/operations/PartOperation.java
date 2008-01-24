package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartOperation implements BotOperation {
    /**
     * This method is responsible for differentiating between whether
     * or not the bot was addressed.
     */
    public void handleMessage(IRCEvent e) {
        if (e instanceof ChannelMsgEvent) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String message = event.getMessage();
            Matcher m = Pattern.compile("^~part\\s+(.*)$").matcher(message);
            if (m.matches()) {
                e.getSession().partChannel(m.group(1),"I was asked to leave");
            }

        }

    }

}

