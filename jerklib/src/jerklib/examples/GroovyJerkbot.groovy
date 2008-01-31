package jerklib.examples

import jerklib.Profile
import jerklib.ProfileImpl
import jerklib.events.listeners.IRCEventListener
import jerklib.events.IRCEvent
import jerklib.events.JoinCompleteEvent
import jerklib.ConnectionManager
import jerklib.events.ChannelMsgEvent
import jerklib.events.PrivateMsgEvent

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

    public static void main(String[] args) {
        def GroovyJerkbot bot = new GroovyJerkbot("jerkbot", "jerkbot", "irc.freenode.org", 6667)
    }

    public void recieveEvent(IRCEvent e) {
        if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
            e.getSession().joinChannel("#jerklib")
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
            }

        } else if (e.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
            PrivateMsgEvent event = (PrivateMsgEvent)e            
            if(event.getMessage() ==~ /^~quit.*$/) {
                e.getSession().close("I was asked to leave.") 
            }
        }

    }

}
