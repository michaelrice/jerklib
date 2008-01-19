package jerklib.examples.jerkbot2;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.ProfileImpl;
import jerklib.events.listeners.IRCEventListener;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.examples.jerkbot2.operations.BotOperation;
import jerklib.examples.jerkbot2.operations.ChannelManagerOperation;
import jerklib.examples.jerkbot2.operations.QuitOperation;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;

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
        Profile profile = new ProfileImpl(login, nick, nick + new Random().nextInt(42), nick+ new Random().nextInt(42));
        manager = new ConnectionManager(profile);
        manager.requestConnection(server, port, profile).addIRCEventListener(this);
    }

    public void loadOperations() {
        operations.add(new QuitOperation("abc123"));
        operations.add(new ChannelManagerOperation());
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
        } else if (e.getType() == IRCEvent.Type.JOIN_COMPLETE) {
            JoinCompleteEvent event = (JoinCompleteEvent) e;
            event.getChannel().say("Hai 2u");
            event.getChannel().say("I am running jerklib version "+ConnectionManager.getVersion());
 

        }
    }

    public static void main(String[] args) {
        Jerkbot bot = new Jerkbot("jerkbot", "jerkbot", "irc.freenode.org", 6667);
        bot.loadOperations();
    }


}
