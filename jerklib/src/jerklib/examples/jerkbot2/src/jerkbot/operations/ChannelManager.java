package jerkbot.operations;

import jerklib.Channel;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.ChannelMsgEvent;

import java.util.Collection;
import java.util.List;

/**
 * This plugin illustrates how to use the channel-related events.
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class ChannelManager implements BotOperation {
    private Collection<Channel> channels;

    public ChannelManager() {
    }

    public void handleMessage(IRCEvent e) {
        if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
            JoinCompleteEvent event = (JoinCompleteEvent) e;
            channels = e.getSession().getChannels();
        } else if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
            ChannelMsgEvent event = (ChannelMsgEvent) e;
            String msg = event.getMessage(); // the message
            if (msg.startsWith("~join")) {
                String channel = msg.substring("~join ".length()); // name of the channel.
                if (!channels.contains(e.getSession().getChannel(channel))) {
                    e.getSession().joinChannel(msg.substring("~join ".length()));
                }
            } else if (msg.startsWith("~part")) {
                // since this is issued in the channel -- we assume it exists.
                e.getSession().partChannel(event.getChannel(), "I was asked to leave.");
            } else if (msg.startsWith("~users")) {
                StringBuilder sb = new StringBuilder();
                Channel c = event.getChannel();
                List<String> users = c.getNicks(); // users in the channel.
                for (String user : users) {
                    sb.append(user + " ");
                }
                e.getSession().channelSay(c.getName(), "Users in " + c.getName() + " are: " + sb.toString());
            }
        }
    }
}
