package jerklib.examples.jerkbot.operations;

import jerklib.Channel;
import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This plugin illustrates how to use the channel-related events.
 *
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class ListUserOperation implements BotOperation {

    public ListUserOperation() {
    }

    public void handleMessage(IRCEvent e) {
        if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String message = event.getMessage(); // the message
            Matcher m = Pattern.compile("^~users.*$").matcher(message);
            if (m.matches()) {
                StringBuilder sb = new StringBuilder();
                Channel c = event.getChannel();
                List<String> users = c.getNicks(); // users in the channel.
                for (String user : users) {
                    sb.append(user).append(" ");
                }
                e.getSession().channelSay(c.getName(), "Users in " + c.getName() + " are: " + sb.toString());
            }
        }
    }
}
