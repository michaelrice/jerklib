package jerklib;

/**
 * @version $Revision$
 */
public class EventTestBase
{
    protected Session session;
    protected MockConnectionManager man;

    public EventTestBase()
    {
        man = new MockConnectionManager("kubrick.freenode.net");

        man.parse(this.getClass().getResourceAsStream("/test1.data"));
        session = man.getSession();
    }

    protected int allEvents = 0;

    protected int conComplete = 0;

    protected int events = 0;

    protected void clearAll()
    {
        session.clearListeners();
    }
}
