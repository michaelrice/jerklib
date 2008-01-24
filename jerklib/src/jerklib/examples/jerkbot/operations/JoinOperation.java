package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinOperation implements BotOperation {
    private final Pattern p;
    public JoinOperation() {
        p = Pattern.compile("^~join\\s+(.*)$");
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
                e.getSession().joinChannel(m.group(1));
            }

        }

    }

}
