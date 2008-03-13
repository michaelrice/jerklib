package jerklib;

import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.TaskImpl;

/**
 * @version $Revision$
 */
public class EventTestBase
{
    private Session session;
    protected MockConnectionManager man;

    public EventTestBase()
    {
        man = new MockConnectionManager("kubrick.freenode.net");

        man.parse(this.getClass().getResourceAsStream("/test1.data"));
        session = man.getSession();
        session.addIRCEventListener(new IRCEventListener()
        {
            public void receiveEvent(IRCEvent e)
            {
                allEvents++;
            }
        });
        session.onEvent(new TaskImpl("con_complete")
        {
            public void receiveEvent(IRCEvent e)
            {
                conComplete++;
            }
        }, Type.CONNECT_COMPLETE);
        session.onEvent(new TaskImpl("all")
        {
            public void receiveEvent(IRCEvent e)
            {
                events++;
            }
        });
    }

    protected int allEvents = 0;

    protected int conComplete = 0;

    protected int events = 0;

}
