package jerklib.examples.jerkbot.operations;

import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows you to make the bot speak or do an action.
 * @author Robert O'Connor &lt;robby.oconnor@gmail.com&gt;
 */
public class SayOperation implements BotOperation {
    private final Pattern p;
    public SayOperation() {
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
                e.getSession().channelSay(event.getChannel().getName(), m.group(1));
            }
            m = Pattern.compile("^~action\\s+(.*)$").matcher(message);
            if(m.matches()) {
                /* how to send an action -- this is NOT in the lib itself. */
                e.getSession().channelSay(event.getChannel().getName(),"\001ACTION "+m.group(1));
            }
            
        }

    }
}
