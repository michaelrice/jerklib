package jerklib.util.misc;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Use this class to test out your regexes w/o breaking things
 * 
 * @author Robert O'Connor <robby.oconnor@gmail.com>
 */
public class RegexTester {
    public static void main(String[] args) {
        // we'll ignore 321 since it's just basically misc. data that isn't relevant to us.
        Pattern p = Pattern.compile(".+?\\s322\\s.+?\\s(.+?)\\s(\\d+)\\s:(.+?)");
        Matcher m = p.matcher(":card.freenode.net 322 ronnoco #blender.de 6 :happy new year");

        if(m.matches()) {
            System.out.println(m.group(1));  // channel
            System.out.println(m.group(2)); // number of users
            System.out.println(m.group(3)); // topic 

        }
        // reset our Pattern and Matcher references to null references to prepare for the next regex
        p = null;
        m = null;
        // check for end of /list
        //^:(.*?)\\!\\S+\\s+KICK\\s+(\\S+)\\s+(\\S+)\\s+:?(.*)
        p = Pattern.compile("^:\\S+\\s323\\s\\S+\\s:.*$");
        m = p.matcher(":card.freenode.net 323 r0bby___ :End of /LIST");

        // does it match?!?!?
        System.out.println(m.matches());

    }
}
