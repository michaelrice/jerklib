package jerklib;

import jerklib.events.IRCEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @version $Revision$
 */
/*
 * NOtes: to do this right i need to impl mock connection completly
 * i Also need 2 threads , one parsing,doing IO and another realying events
 * Then on top we need to add some calls to session. I think with those in place
 * we can test MT
 * 
 * 
 * what can be tested because of this class:
 * 	1. event relaying
 *  2. event parsing
 *  3. the IRCEventFactory
 *  4. WriteListeners notify/get/add/remove
 *  5. session methods get/remove etc
 *  6. all aspects of sesion can be tested
 *  
 *  what cant be tested:
 *  	1. all the connection methods makeCOnnections/checkConnections etc
 *  	2. doNetworkIO
 * 		3. The Connection class
 */
public class MockConnectionManager extends ConnectionManager
{
    Session session;

    public MockConnectionManager(String hostName)
    {
        session = new Session(new RequestedConnection
                (
                        hostName,
                        6667,
                        new ProfileImpl("test", "testnick", "testnick1", "testnick2")
                ));


        session.setConnection(new MockConnection(this, null, session));
        session.connected();
        session.getConnection().loginSuccess();
        session.getConnection().setHostName(hostName);
        sessionMap.put("irc.freenode.net", session);
        socChanMap.put(null, session);


        IRCEventFactory.setManager(this);

    }

    List<IRCEvent> events = new ArrayList<IRCEvent>();

    public void start()
    {
        for (IRCEvent event : events)
        {
            addToEventQueue(event);
        }
        parseEvents();
        relayEvents();
    }

    public void parse(InputStream ircData)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(ircData));
        try
        {
            StringBuilder builder = new StringBuilder();
            String token = null;
            while ((token = br.readLine()) != null)
            {
                final String token1 = token;
                events.add(new IRCEvent()
                {

                    public String getRawEventData()
                    {
                        return token1;
                    }

                    public Session getSession()
                    {
                        return session;
                    }

                    public Type getType()
                    {
                        return Type.DEFAULT;
                    }
                });
            }
        }
        catch (IOException e)
        {
            throw new Error(e); // die horribly. What's wrong with you, you can't build a test properly?
        }
    }

    public Session getSession()
    {
        return session;
    }

}

