package jerklib.examples.jerkbot.operations;


import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This will allow you to make the bot quit via IRC.
 * It illustrates how to use PrivateMsgEvent.
 *
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
        if (e.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
            // cast to the real type
            PrivateMsgEvent event = (PrivateMsgEvent) e;
            String message = event.getMessage(); // message
            Matcher m = Pattern.compile("^~quit\\s(.*)/q $").matcher(message);
            if (m.matches()) {
                e.getSession().close(m.group(1));
            }


        }
    }
}

