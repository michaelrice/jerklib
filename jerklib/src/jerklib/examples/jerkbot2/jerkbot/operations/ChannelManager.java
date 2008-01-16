package jerklib.examples.jerkbot2.jerkbot.operations;

import jerklib.Channel;
import jerklib.events.IRCEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.List;

/**
 * This plugin illustrates how to use the channel-related events.
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class ChannelManager implements BotOperation {
   
    public ChannelManager() {
    }

    public void handleMessage(IRCEvent e) {
         if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String msg = event.getMessage(); // the message
            if (msg.startsWith("~users")) {
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
