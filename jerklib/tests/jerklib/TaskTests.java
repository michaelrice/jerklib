package jerklib;

import junit.framework.TestCase;

/**
 * Created: Feb 3, 2008 5:13:16 PM
 *
 * @author <a href="mailto:robby.oconnor@gmail.com">Robert O'Connor</a>
 */
public class TaskTests extends TestCase {
    private ConnectionManager conMan;
    private Session session;


    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        conMan = new ConnectionManager
        (
                new ProfileImpl
                (
                    "DIBLET",
                    "DIBLET1",
                    "DIBLET2",
                    "DIBLET3"
                )
        );
        conMan.requestConnection("irc.freenode.net");
        session = conMan.getSession("irc.freenode.net");

        while(!session.isConnected())
        {
            Thread.sleep(500);
        }

    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        conMan.quit();
    }

    public void testTasks() {
        
    }

}
