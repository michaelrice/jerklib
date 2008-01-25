package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adds the ability to make the bot part the channel. 
 * @author Robert O'Connor &lt;robby.oconnot@gmail.com&gt;
 */
public class PartOperation implements BotOperation {
    private final Pattern p;
    public PartOperation() {
        p = Pattern.compile("^~part\\s+(.*)$");
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
                e.getSession().partChannel(m.group(1),"I was asked to leave");
            }

        }

    }

}

