package jerklib.examples.jerkbot2.operations;


import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;

/**
 * This will allow you to make the bot quit via IRC.
 * It illustrates how to use PrivateMsgEvent.
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class QuitOperation implements BotOperation {
    private String password;

    public QuitOperation(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void handleMessage(IRCEvent e) {
        if(e.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
            // cast to the real type
            PrivateMsgEvent event = (PrivateMsgEvent)e;
            String msg = event.getMessage(); // message
            if(msg.startsWith("~quit")) {
                if(msg.substring("~quit ".length()).equals(password)) {
                    e.getSession().close("I was asked to leave");
                    System.exit(0); 
                }
            }

            

        }
    }
}
