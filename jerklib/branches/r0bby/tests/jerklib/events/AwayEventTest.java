package jerklib.events;

import jerklib.EventTestBase;
import jerklib.tasks.TaskImpl;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;



/**
 * Created: Mar 18, 2008 9:50:23 PM
 *
 * @author <a href="mailto:robby.oconnor@gmail.com">Robert O'Connor</a>
 */
public class AwayEventTest extends EventTestBase
{
	private List<AwayEvent> events = new ArrayList<AwayEvent>();

	public AwayEventTest()
	{
        super("/away.data", System.getProperty("user.home") + File.separator + "jerklib.tests.user.ouput");        
    }

	@BeforeTest
	public void init()
	{
		createSession();
		addServerInfo(ServerInfo.ALL);
		session.onEvent(new TaskImpl("away")
		{
			public void receiveEvent(IRCEvent e)
			{
				events.add((AwayEvent)e);
			}
        }, IRCEvent.Type.AWAY_EVENT);

		conMan.start(session);
	}

	@Test
    public void testBahamutRemoteUserIsAway()
    {
        AwayEvent event = events.get(0);
        assertFalse(event.isYou());
        assertTrue(event.isAway());
        assertEquals(event.getNick(),"mohadib");
        assertEquals(event.getAwayMessage(),"NO");

    }

    @Test
	public void testBahamutUserWentAway()
	{
		AwayEvent event = events.get(1);
        assertTrue(event.isYou());
        assertTrue(event.isAway());
        
    }

    @Test
    public void testBahamutUserReturnedFromAway()
    {
        AwayEvent event = events.get(2);
        assertFalse(event.isAway());
        assertTrue(event.isYou()); 
    }

    @Test
    public void testHyperionUserIsAway()
    {
        AwayEvent event = events.get(3);
        assertFalse(event.isYou());
        assertTrue(event.isAway());
        assertEquals(event.getNick(),"SwingBot");
        assertEquals(event.getAwayMessage(),"FUCKER"); 
    }

    @Test
    public void testHyperionUserWentAway()
    {
        AwayEvent event = events.get(4);
        assertTrue(event.isYou());
        assertTrue(event.isAway());
    }

    @Test
    public void testHyperionUserReturnedFromAway()
    {
        AwayEvent event = events.get(5);
        assertTrue(event.isYou());
        assertFalse(event.isAway());
    }

    @Test
    public void testQuakeNetUserIsAway()
    {
        AwayEvent event = events.get(6);
        assertFalse(event.isYou());
        assertTrue(event.isAway());
        assertEquals(event.getNick(),"mohadib");
        assertEquals(event.getAwayMessage(),"HEYE");
    }

    @Test
    public void testQuakeNetUserWentAway()
    {
        AwayEvent event = events.get(7);
        assertTrue(event.isYou());
        assertTrue(event.isAway());
    }

    @Test
    public void testQuakeNetUserReturnedFromAway()
    {
        AwayEvent event = events.get(8);
        assertTrue(event.isYou());
        assertFalse(event.isAway());
    }

    @Test
    public void testUnrealUserIsAway()
    {
        AwayEvent event = events.get(9);
        assertFalse(event.isYou());
        assertTrue(event.isAway());
        assertEquals(event.getNick(),"mohadib");
        assertEquals(event.getAwayMessage(),"HI");
    }

    @Test
    public void testUnrealUserWentAway()
    {
        AwayEvent event = events.get(10);
        assertTrue(event.isYou());
        assertTrue(event.isAway());
    }

    @Test
    public void testUnrealUserReturnedFromAway()
    {
        AwayEvent event = events.get(11);
        assertTrue(event.isYou());
        assertFalse(event.isAway());    
    }
}
