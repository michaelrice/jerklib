package jerklib;


/**
 * @version $Revision$
 */
/*
 * NOtes: to do this right i need to impl mock connection completly i Also need
 * 2 threads , one parsing,doing IO and another realying events Then on top we
 * need to add some calls to session. I think with those in place we can test MT
 * 
 * 
 * what can be tested because of this class: 1. event relaying 2. event parsing
 * 3. the IRCEventFactory 4. WriteListeners notify/get/add/remove 5. session
 * methods get/remove etc 6. all aspects of sesion can be tested
 * 
 * what cant be tested: 1. all the connection methods
 * makeCOnnections/checkConnections etc 2. doNetworkIO 3. The Connection class
 */
public class MockConnectionManager extends ConnectionManager
{

	public MockConnectionManager()
	{
		//IRCEventFactory.setManager(this);
	}

	public Session requestConnection(String hostName, int port, Profile profile , String inputFile , String outputFile)
	{
		Session session = new Session(new RequestedConnection(hostName , port , profile));
		
		InternalEventParser parser = new DefaultInternalEventParser();
		session.setInternalParser(parser);
		
		MockSocketChannel chan = new MockSocketChannel(null);
		MockConnection con = new MockConnection
		(
				this , 
				chan ,
				session,
				inputFile,
				outputFile
		);
		
		session.setConnection(con);
		session.connected();
		con.setHostName(hostName);
		
		sessionMap.put(hostName, session);
		socChanMap.put(chan, session);
		
		setDefaultProfile(profile);
		return session;
	}

	public void start(Session session)
	{
		((MockConnection)session.getConnection()).start();
		parseEvents();
		relayEvents();
		notifyWriteListeners();
	}
}
