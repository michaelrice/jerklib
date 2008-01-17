package jerklib.util.misc;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTester {
    public static void main(String[] args) {
        Pattern p = Pattern.compile(".*\\s322\\s.*?\\s(.*?)\\s(.*?)\\s:(.*?)");
        Matcher m = p.matcher(":card.freenode.net 322 ronnoco #blender.de 6 :happy new year");

        if(m.matches()) {
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            System.out.println(m.group(3));

        }
    }
}
