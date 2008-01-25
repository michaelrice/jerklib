package jerklib.examples.jerkbot.operations;


import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This will allow you to make the bot quit via IRC.
 * It illustrates how to use PrivateMsgEvent.
 *
 * @author Robert O'Connor &lt;robby.oconnor@gmail.com&gt;
 */
public class QuitOperation implements BotOperation {
    private String password;
    private final Pattern p;

    public QuitOperation(String password) {
        this.password = password;
        p = Pattern.compile("^~quit\\s+(.*)$");
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
            Matcher m = p.matcher(message);
            if (m.matches()) {
                if(m.group(1).equals(password)) {
                    e.getSession().close(m.group(1));
                }
            }


        }
    }
}

