package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;
import jerklib.ConnectionManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NickOperation implements BotOperation {
      
    /**
     * This method is responsible for differentiating between whether
     * or not the bot was addressed.
     */
    public void handleMessage(IRCEvent e) {
        if (e instanceof ChannelMsgEvent) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String message = event.getMessage();
            Matcher m = Pattern.compile("^~nick\\s+(.*)$").matcher(message);
            if (m.matches()) {
                e.getSession().rawSay("NICK :"+m.group(1)+"\r\n");
            }

        }

    }

}

