package jerklib.events;

import jerklib.EventTestBase;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision$
 */
public class AllEventsTests extends EventTestBase
{
    @Test (timeOut = 17000)
    // 17 seconds at most!
    public void runEvents()
    {
        for (int i = 0; i < 10000; i++)
        {
            man.start();
        }
    }

    @Test (dependsOnMethods = "runEvents")
    public void testEventStuff()
    {
        assertTrue(allEvents == 590000);
    }

    @Test (dependsOnMethods = "runEvents")
    public void testTaskFiltering()
    {
        assertTrue(conComplete == 10000);
    }

    @Test (dependsOnMethods = "runEvents")
    public void testNonFilteredTasks()
    {
        assertTrue(events == 590000);
    }

}
