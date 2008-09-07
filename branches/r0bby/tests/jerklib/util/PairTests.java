package jerklib.util;

import org.testng.annotations.Test;

/**
 * Created by IntelliJ IDEA. User: jottinger Date: Mar 10, 2008 Time: 10:19:58
 * AM
 */
public class PairTests
{
    @Test
    public void testPairEquals()
    {
        Pair<String, String> a, b = null;
        a = new Pair<String, String>("first", "second");
        assert (!a.equals(b));
        b = new Pair<String, String>("first", "fourth");
        assert (!a.equals(b));
        b = new Pair<String, String>(null, "fourth");
        assert (!a.equals(b));
        b = new Pair<String, String>("first", "second");
        assert (a.equals(b));
    }
}
