package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.EventToken;
import jerklib.IRCEventFactory;
import jerklib.Session;
import jerklib.Token;
import jerklib.events.IRCEvent;
import jerklib.events.MessageEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.MessageEventImpl;

public class PrivMsgParser implements CommandParser
{
	public MessageEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token>tokens = token.getWordTokens();
		Session session = event.getSession();
		Type type = session.isChannelToken(tokens.get(2))?Type.CHANNEL_MESSAGE:Type.PRIVATE_MESSAGE;
		Channel chan = type == Type.CHANNEL_MESSAGE? session.getChannel(tokens.get(2).data):null;
		MessageEvent me =  new MessageEventImpl
		(
			chan,
			ParserUtil.getHostName(tokens.get(0)), 
			token.concatTokens(6).substring(1), 
			ParserUtil.getNick(tokens.get(0)),
			token.getData(), 
			session, 
			type, 
			ParserUtil.getUserName(tokens.get(0))
		);
		
		String msg = me.getMessage();
		if (msg.startsWith("\u0001"))
		{
			String ctcpString = msg.substring(1, msg.length() - 1);
			me = IRCEventFactory.ctcp(me, ctcpString);
		}
		
		return me;
	}
}
