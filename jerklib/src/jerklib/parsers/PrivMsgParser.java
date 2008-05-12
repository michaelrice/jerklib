package jerklib.parsers;

import java.util.List;

import jerklib.Channel;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.MessageEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.dcc.DccEventFactory;
import jerklib.events.impl.CtcpEventImpl;
import jerklib.events.impl.MessageEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

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
			TokenUtil.getHostName(tokens.get(0)), 
			token.concatTokens(6).substring(1), 
			TokenUtil.getNick(tokens.get(0)),
			token.getData(), 
			session, 
			type, 
			TokenUtil.getUserName(tokens.get(0))
		);
		
		String msg = me.getMessage();
		if (msg.startsWith("\u0001"))
		{
			String ctcpString = msg.substring(1, msg.length() - 1);
			if (ctcpString.startsWith("DCC "))
			{
				me = DccEventFactory.dcc(me, ctcpString);
			}
			else
			{
				return new CtcpEventImpl
				(
					ctcpString, 
					me.getHostName(), 
					me.getMessage(), 
					me.getNick(), 
					me.getUserName(), 
					me.getRawEventData(), 
					me.getChannel(), 
					me.getSession()
				);
			}
		}
		
		return me;
	}
}