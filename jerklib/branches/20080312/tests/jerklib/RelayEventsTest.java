hepackage jerklib;

import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jerklib.TestConnectionManager;
import jerklib.events.IRCEvent;
import jerklib.tasks.Task;

public class RelayEventsTest
{
  private static TestConnectionManager conMan;
  private static Session session;
  private static Connection connection;
  private static Profile profile = new ProfileImpl("DIBLET", "DIBLET1", "DIBLET2", "DIBLET3");

  @BeforeTest
  public void setup()
  {
    conMan = new TestConnectionManager(profile);
    IRCEventFactory.setManager(conMan);
    session=conMan.requestConnection("foo");
    try
    {
      conMan.connect(session);
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    connection=session.getConnection();
  }

  @AfterTest
  public void teardown()
  {
    connection = null;
  }

  Logger log=Logger.getLogger(this.getClass().getName());
  static boolean added=false;
  static Object lock=new Object();  
  @Test(dataProvider="eventProvider", threadPoolSize=10, invocationCount=400)
  public void testRelayEvent1(IRCEvent providedEvent)
  {
    assert session!=null;
    synchronized(lock) {    
      if (!added)
      {
        session.onEvent(new Task()
                        {
                          public void receiveEvent(IRCEvent e)
                          {
                          }
                        });
        added=true;
      }
    }
    conMan.addToRelayList(providedEvent);

    conMan.relayEvents();           
  }

  @DataProvider
  public Object[][] eventProvider() {
    IRCEvent event=null;
    switch ((int)(Math.random()*5))
    {
    case 0:
      event=IRCEventFactory.kick(":mohadib!~mohadib@67.41.102.162 KICK #test scab :bye!", connection);
      break;
    case 1:
      event=IRCEventFactory.connectionComplete(":irc.nmglug.org 001 DIBLET1 :Welcome to the nmglug.org", connection);
      break;
    case 2:
      event=IRCEventFactory.nickChange(":raving!n=raving@74.195.43.119 NICK :Sir_Fawnpug", connection);
      break;
    case 3:
      event=IRCEventFactory.regularJoin(":r0bby!n=wakawaka@guifications/user/r0bby JOIN :#test", connection);
      break;
    case 4:
      event=IRCEventFactory.notice(":DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #test :test", connection);
      break;
    }
    return new Object[][] { new Object[] { event}};
  }
}