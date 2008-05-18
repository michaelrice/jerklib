package jerklib.parsers;

import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ConnectionCompleteEventImpl;
import jerklib.tokens.EventToken;

public class ConnectionCompleteParser implements CommandParser
{
	
	/* :irc.nmglug.org 001 namnar :Welcome to the nmglug.org 
	 	
	 	Lets user know channels can now be joined etc.
	 	
	  Lets user update *records* 
	  A requested connection to irc.freenode.net might actually
	  connect to kubrick.freenode.net etc 
	*/
	
	public ConnectionCompleteEvent createEvent(EventToken token, IRCEvent event)
	{
		return new ConnectionCompleteEventImpl
		(
				token.getData(), 
				token.getPrefix(), // actual hostname
				event.getSession(), 
				event.getSession().getConnectedHostName() // requested hostname
		);
	}
}
