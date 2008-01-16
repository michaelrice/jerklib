package jerklib.examples.jerkbot2.src.jerkbot;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.ProfileImpl;
import jerklib.events.listeners.IRCEventListener;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.examples.jerkbot2.src.jerkbot.operations.BotOperation;
import jerklib.examples.jerkbot2.src.jerkbot.operations.ChannelManager;
import jerklib.examples.jerkbot2.src.jerkbot.operations.Quit;

import java.util.List;
import java.util.LinkedList;


/**
 * This an the main class and entry point.
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class Jerkbot implements IRCEventListener {
    private ConnectionManager manager;
    private List<BotOperation> operations = new LinkedList<BotOperation>();

    /**
     * This is used to actually connect the bot to the network.
     * It is also used to register the event listener with
     * the ConnectionManager.
     *
     * @param nick   the nick the bot will use.
     * @param login  the bot's "login" also known as the ident field.
     * @param server the server the bot will connect to.
     * @param port   the port of the server to connect to.
     */
    public Jerkbot(String nick, String login, String server, int port) {
        Profile profile = new ProfileImpl(login, nick, nick + "1", nick + "2");
        manager = new ConnectionManager(profile);
        manager.requestConnection(server, port, profile).addIRCEventListener(this);
    }

    public void loadOperations() {
        operations.add(new Quit("abc123"));
        operations.add(new ChannelManager()); 
    }


    /**
     * Process the events sent to us by Jerklib.
     *
     * @param e the event we receieved.
     */
    public void recieveEvent(IRCEvent e) {
        for(BotOperation operation : operations) {
            operation.handleMessage(e);
        }
        if (e.getType() == IRCEvent.Type.CONNECT_COMPLETE) {
            e.getSession().joinChannel("#jerklib");
        }
        if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
            JoinCompleteEvent event = (JoinCompleteEvent) e;
            e.getSession().channelSay(event.getChannel().getName(), "Hai 2u");
        }
    }

    public static void main(String[] args) {
        Jerkbot bot = new Jerkbot("ronnoco", "ronnoco", "irc.freenode.org", 6667);
        bot.loadOperations();
    }


}
