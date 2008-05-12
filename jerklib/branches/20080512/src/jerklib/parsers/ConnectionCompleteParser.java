package jerklib.parsers;

import java.util.List;

import jerklib.EventToken;
import jerklib.Token;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ConnectionCompleteEventImpl;

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
		List<Token> tokens = token.getWordTokens();
		
		return new ConnectionCompleteEventImpl
		(
				token.getData(), 
				tokens.get(0).data.substring(1).toLowerCase(), // actual hostname
				event.getSession(), 
				event.getSession().getConnectedHostName() // requested hostname
		);
	}
}
