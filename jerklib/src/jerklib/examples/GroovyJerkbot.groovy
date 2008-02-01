package jerklib.examples.jerkbot;

import jerklib.Profile
import jerklib.ProfileImpl
import jerklib.events.listeners.IRCEventListener
import jerklib.events.IRCEvent
import jerklib.events.JoinCompleteEvent
import jerklib.ConnectionManager
import jerklib.events.ChannelMsgEvent
import jerklib.events.PrivateMsgEvent
import jerklib.events.WhoEvent

/**
* Created: Jan 29, 2008 11:42:23 PM
* @author <a href="mailto:robby.oconnor@gmail.com">Robert O'Connor</a>
*/
class GroovyJerkbot implements IRCEventListener {
    def ConnectionManager manager

    GroovyJerkbot(String nick, String username, String hostname, int port) {
        def Profile profile = new ProfileImpl(username, nick, nick + new Random().nextInt(42),
                nick + new Random().nextInt(512))
        manager = new ConnectionManager(profile)
        manager.requestConnection(hostname, port).addIRCEventListener(this)

    }

    def static void main(String[] args) {
        def GroovyJerkbot bot = new GroovyJerkbot("jerkbot", "jerkbot", "irc.freenode.org", 6667)
    }

    def void recieveEvent(IRCEvent e) {
        if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
            e.getSession().joinChannel("#jerklib")
            e.getSession().who("mohadib")
        }
        else if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
            def JoinCompleteEvent event = (JoinCompleteEvent) e
            e.getSession().channelSay(event.getChannel().getName(), "Hai 2u")
        } else if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
            ChannelMsgEvent event = (ChannelMsgEvent) e
            // what does this is take the channel msg and match it to the pattern ~say foo
            def matcher = event.getMessage() =~ /^~say\s+(.*)$/
            if (matcher.matches()) {
                e.getSession().channelSay(event.getChannel().getName(), matcher.group(1))
            }else if(event.getMessage() ==~ /^~part.*$/) {
                e.getSession().partChannel(event.getChannel(),"I was asked to leave")
            } else {
                def whoMatcher = event.getMessage() =~ /^~who\s+(.*)$/
                if(whoMatcher.matches()) {
                    e.getSession().who(whoMatcher.group(1))
                }
            }

        } else if (e.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
            PrivateMsgEvent event = (PrivateMsgEvent)e
            if(event.getMessage() ==~ /^~quit.*$/) {
                e.getSession().close("I was asked to leave.")
            }
        } else if(e.getType() == IRCEvent.Type.WHO_EVENT) {
            WhoEvent event = (WhoEvent)e
            println(event.getChannel())
            println(event.getNick())
            println(event.getUserName())
            println(event.getRealName())
            println(event.getServerName())
            println(event.getHostName())
            println(event.isAway())
            println(e.getRawEventData())
        } else if(e.getType() == IRCEvent.Type.DEFAULT){
            System.out.println(e.getRawEventData());
        }

    }

}
