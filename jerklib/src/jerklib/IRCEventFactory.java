package jerklib;

import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.events.impl.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IRCEventFactory
{
	private static ConnectionManager myManager;

	static void setManager(ConnectionManager manager)
	{
		myManager = manager;
	}

	// :kubrick.freenode.net 351 scripy hyperion-1.0.2b(382). kubrick.freenode.net :iM dncrTS/v4
	// "<version>.<debuglevel> <server> :<comments>"
	static ServerVersionEvent serverVersion(EventToken token, Session session)
	{
		List<Token> tokens = token.getWordTokens();
			return new ServerVersionEventImpl
			(
				token.concatTokens(10).substring(1), 
				tokens.get(5).data,
				tokens.get(4).data, 
				"", 
				token.getData(), 
				session
			);
	}

	// :irc.nmglug.org 001 namnar :Welcome to the nmglug.org
	static ConnectionCompleteEvent connectionComplete(EventToken token, Session session)
	{
			List<Token> tokens = token.getWordTokens();
			/* send host name changed event so users of lib can update *records* */
			return new ConnectionCompleteEventImpl
			(
					token.getData(), 
					tokens.get(0).data.substring(1).toLowerCase(), // new
					session, 
					session.getConnection().getHostName() // old hostname
			);
	}

	// :simmons.freenode.net 352 r0bby_ * n=wakawaka guifications/user/r0bby
	// irc.freenode.net r0bby H :0 Robert O'Connor
	static WhoEvent who(EventToken token, Session session)
	{
		String data = token.getData();
		Pattern p = Pattern.compile("^:.+?\\s+352\\s+.+?\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?):(\\d+)\\s+(.+)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			
			boolean away = m.group(6).charAt(0) == 'G';
			return new WhoEventImpl(m.group(1), // channel
					Integer.parseInt(m.group(7)), // hop count
					m.group(3), // hostname
					away, // status indicator
					m.group(5), // nick
					data, // raw event data
					m.group(8), // real name
					m.group(4), // server name
					session, // session
					m.group(2) // username
			);
		}
		debug("WHO", data);
		return null;
	}

	// :kubrick.freenode.net 314 scripy1 ty n=ty 71.237.206.180 * :ty
	// "<nick> <user> <host> * :<real name>"
	static WhowasEvent whowas(EventToken token, Session session)
	{
		List<Token> tokens = token.getWordTokens();
			return new WhowasEventImpl
			(
					tokens.get(5).data, 
					tokens.get(4).data, 
					tokens.get(3).data, 
					tokens.get(tokens.size() -1).data.substring(1), 
					token.getData(), 
					session
			); 
	}

	static NumericErrorEvent numericError(EventToken token, Session session, int numeric)
	{
		return new NumericEventImpl
		(
				token.concatTokens(7), 
				token.getData(), 
				numeric, 
				session
		); 
	}

	static WhoisEvent whois(EventToken token, Session session)
	{
		// "<nick> <user> <host> * :<real name>"
			List<Token> tokens = token.getWordTokens();
			return new WhoisEventImpl
			(	
					tokens.get(4).data, 
					token.getData().substring(token.getData().lastIndexOf(":") + 1), 
					tokens.get(5).data, 
					tokens.get(6).data, 
					token.getData(), 
					session
			); 
	}

	/*
	 * end of names :irc.newcommunity.tummy.com 366 SwingBot #test :End of NAMES
	 * list
	 */
	static NickListEvent nickList(EventToken token, Session session)
	{
			List<Token>tokens = token.getWordTokens();
			return new NickListEventImpl
			(
					token.getData(), 
					session, 
					session.getChannel(tokens.get(3).data.toLowerCase()), 
					session.getChannel(tokens.get(3).data.toLowerCase()).getNicks()
			); 
	}

	/* :mohadib!~mohadib@67.41.102.162 KICK #test scab :bye! */
	static KickEvent kick(EventToken token, Session session)
	{
		List<Token>tokens = token.getWordTokens();
			Channel channel = session.getChannel(tokens.get(2).data.toLowerCase());
			return new KickEventImpl
			(
					token.getData(), 
					session, 
					getNick(tokens.get(0)), // byWho
					getUserName(tokens.get(0)), // username
					getHostName(tokens.get(0)), // host name
					tokens.get(3).data, // victim
					token.concatTokens(8).substring(1), // message
					channel
			);
	}

	// sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is
	// for testing only.
	static TopicEvent topic(EventToken token, Session session)
	{
			List<Token> tokens = token.getWordTokens();
			return new TopicEventImpl
			(
					token.getData(), 
					session, 
					session.getChannel(tokens.get(3).data),
					token.concatTokens(8).substring(1)
			);
	}

	/*
	 * A Channel Msg :fuknuit!~admin@212.199.146.104 PRIVMSG #debian :blah blah
	 * Private message :mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!!
	 */
	static MessageEvent message(EventToken token , Session session)
	{
		List<Token>tokens = token.getWordTokens();
		Type type = session.isChannelToken(tokens.get(2))?Type.CHANNEL_MESSAGE:Type.PRIVATE_MESSAGE;
		Channel chan = type == Type.CHANNEL_MESSAGE? session.getChannel(tokens.get(2).data):null;
		return new MessageEventImpl
		(
			chan,
			getHostName(tokens.get(0)), 
			token.concatTokens(6).substring(1), 
			getNick(tokens.get(0)),
			token.getData(), 
			session, 
			type, 
			getUserName(tokens.get(0))
		);
	}
	
	static String getHostName(Token t)
	{
		return t.data.substring(t.data.indexOf('@') + 1);
	}

	static String getUserName(Token t)
	{
		return t.data.substring(t.data.indexOf('!') + 1 , t.data.indexOf('@'));
	}
	
	static String getNick(Token t)
	{
		return t.data.substring(1).substring(0,t.data.indexOf('!') - 1);
	}


	static CtcpEvent ctcp(MessageEvent event, String ctcpString)
	{
		return new CtcpEventImpl(ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
	}

	// the_horrible is the nick that is in use
	// fran is the current nickname
	/* :simmons.freenode.net 433 fran the_horrible :Nickname is already in use. */

	// * is used for current nick when this event happens on connect
	/* :simmons.freenode.net 433 * fran :Nickname is already in use. */

	static NickInUseEvent nickInUse(EventToken token, Session session)
	{
			List<Token> tokens = token.getWordTokens();
			String nick = tokens.get(2).data.equals("*")?tokens.get(3).data:tokens.get(2).data;
			return new NickInUseEventImpl
			(
					nick,
					token.getData(), 
					session
			); 
	}

	// :leguin.freenode.net 306 r0bby_ :You have been marked as being away
	// :leguin.freenode.net 305 r0bby_ :You are no longer marked as being away
	static AwayEvent away(String data, Session session, int numeric)
	{
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s+(\\S+)\\s:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			switch (numeric)
			{
			case 305:
				return new AwayEventImpl(session, AwayEvent.EventType.RETURNED_FROM_AWAY, false, true, myManager.getDefaultProfile().getActualNick(), data);
			case 306:
				return new AwayEventImpl(session, AwayEvent.EventType.WENT_AWAY, true, true, myManager.getDefaultProfile().getActualNick(), data);
			}
		}
		// :card.freenode.net 301 r0bby_ r0bby :foo
		p = Pattern.compile("^:\\S+\\s+\\d{3}\\s+\\S+\\s+(\\S+)\\s+:(.*)$");
		m = p.matcher(data);
		m.matches();
		return new AwayEventImpl(m.group(2), AwayEvent.EventType.USER_IS_AWAY, true, false, m.group(1), data, session);
	}

	// :anthony.freenode.net 375 mohadib_ :- anthony.freenode.net Message of the Day -
	// :anthony.freenode.net 372 mohadib_ :- Welcome to anthony.freenode.net in Irvine, CA, USA! Thanks to
	// :anthony.freenode.net 376 mohadib_ :End of /MOTD command.
	static MotdEvent motd(EventToken token , Session session)
	{
		List<Token>tokens = token.getWordTokens();
		return new MotdEventImpl
		(
			token.getData(), 
			session, 
			token.concatTokens(6).substring(1), 
			tokens.get(0).data.substring(1)
		);
	}

	static NoticeEvent notice(EventToken token, Session session)
	{
		
		List<Token> tokens = token.getWordTokens();
		
		// generic notice NOTICE AUTH :*** No identd (auth) response
		if(tokens.get(0).data.equals("NOTICE"))
		{
			return new NoticeEventImpl
			(
				token.getData(), 
				session, 
				"generic", 
				token.substring(token.getData().indexOf(":") + 1), 
				session.getNick(), 
				session.getConnectedHostName(),
				null
			);
		}
		
		if(tokens.get(1).data.equals("NOTICE"))
		{
			//from server to user
			//:anthony.freenode.net NOTICE mohadib_ :NickServ set your hostname to foo
			if(tokens.get(0).data.indexOf("@") == -1)
			{
				return new NoticeEventImpl
				(
					token.getData(), 
					session, 
					"server", 
					token.substring(token.getData().indexOf(":" , 1) + 1), 
					session.getNick(), 
					session.getConnectedHostName(), 
					null
				);
			}
			
			//from user to channel
			// channel notice :DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #jerklib :test
			if(session.isChannelToken(tokens.get(2)))
			{
				Channel channel = session.getChannel(tokens.get(2).data);
				return new NoticeEventImpl
				(
						token.getData(),
						session,
						"channel",
						token.getData().substring(token.getData().indexOf(":", 1) + 1),
						channel.getName(),
						getNick(tokens.get(0)),
						channel
				);
			}
			else
			{
				//:NickServ!NickServ@services. NOTICE mohadib_ :This nickname is owned by someone else
				return new NoticeEventImpl
				(
						token.getData(),
						session,
						"user",
						token.getData().substring(token.getData().indexOf(":", 1) + 1),
						session.getNick(),
						getNick(tokens.get(0)),
						null
				);
			}
		}
		
		return null;
	}

	// TODO
	// :anthony.freenode.net 391 scripy anthony.freenode.net :Monday February 4
	// 2008 -- 18:02:31 -08:00
	static void ServerTimeEvent()
	{

	}

	/*
	 * :r0bby__!n=wakawaka@cpe-24-164-167-171.hvc.res.rr.com QUIT :Client Quit
	 * PERSON QUIT :Xolt!brad@c-67-165-231-230.hsd1.co.comcast.net QUIT :"Deleted"
	 * :james_so!~me@213-152-46-35.dsl.eclipse.net.uk QUIT :Read error: 60
	 * (Operation timed out)
	 */
	static QuitEvent quit(EventToken token, Session session)
	{
		List<Token>tokens = token.getWordTokens();
		String nick = getNick(tokens.get(0));
		List<Channel> chanList = session.removeNickFromAllChannels(nick);
		return new QuitEventImpl
		(
			token.getData(), 
			session, 
			nick, // who
			getUserName(tokens.get(0)), // username
			getHostName(tokens.get(0)), // hostName
			token.concatTokens(4).substring(1), // message
			chanList
		);
	}

	// :r0bby!n=wakawaka@guifications/user/r0bby JOIN :#jerklib
	// :mohadib_!~mohadib@68.35.11.181 JOIN &test
	static JoinEvent regularJoin(String data, Session session)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+JOIN\\s+:?(\\S+)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			try
			{
				return new JoinEventImpl(data, session, m.group(1), // nick
						m.group(2), // user name
						m.group(3).toLowerCase(), // host
						session.getChannel(m.group(4).toLowerCase()).getName(), // channel
																																		// name
						session.getChannel(m.group(4).toLowerCase()) // channel
				);
			}
			catch (Exception e)
			{
				System.err.println(data);
				for (Channel chan : session.getChannels())
				{
					System.err.println(chan.getName());
				}
				e.printStackTrace();
			}
		}
		debug("JOIN_EVENT", data);
		return null;
	}

	/*
	 * :anthony.freenode.net 322 mohadib_ #jerklib 5 :JerkLib IRC Library -
	 * https://sourceforge.net/projects/jerklib :irc.nixgeeks.com 321 mohadib
	 * Channel :Users Name
	 */
	static ChannelListEvent chanList(EventToken token, Session session)
	{
		String data = token.getData();
		if (log.isLoggable(Level.FINE))
		{
			log.fine(data);
		}
		Pattern p = Pattern.compile("^:\\S+\\s322\\s\\S+\\s(\\S+)\\s(\\d+)\\s:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new ChannelListEventImpl(data, m.group(1), m.group(3), Integer.parseInt(m.group(2)), session); }
		debug("CHAN_LIST", data);
		return null;
	}

	static JoinCompleteEvent joinCompleted(String data, Session session, String nick, Channel channel)
	{
		return new JoinCompleteEventImpl(data, session, channel);
	}

	// :fran!~fran@outsiderz-88006847.hsd1.nm.comcast.net PART #jerklib
	// :r0bby!n=wakawaka@guifications/user/r0bby PART #jerklib :"FOO"
	// :mohadib_!~mohadib@68.35.11.181 PART :&test
	static PartEvent part(EventToken token, Session session)
	{
			List<Token>tokens = token.getWordTokens();
			String partMsg = tokens.size() >= 3?token.concatTokens(6):"";
			if(tokens.get(2).data.startsWith(":"))tokens.get(2).data = tokens.get(2).data.substring(1);
			return new PartEventImpl
			(
					token.getData(), 
					session,
					getNick(tokens.get(0)), // who
					getUserName(tokens.get(0)), // username
					getHostName(tokens.get(0)), // host name
					session.getChannel(tokens.get(2).data.toLowerCase()).getName(), // channel name
					session.getChannel(tokens.get(2).data.toLowerCase()), 
					partMsg 
			);
	}

	// :raving!n=raving@74.195.43.119 NICK :Sir_Fawnpug
	static NickChangeEvent nickChange(EventToken token, Session session)
	{
			List<Token> tokens = token.getWordTokens();
			return new NickChangeEventImpl
			(
					token.getData(), 
					session, 
					getNick(tokens.get(0)), // old
					tokens.get(2).data.substring(1), // new nick
				getHostName(tokens.get(0)), // hostname
				getUserName(tokens.get(0)) // username
		); 
	}

	// :r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2
	static InviteEvent invite(String data, Session session)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+INVITE.+?:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new InviteEventImpl(m.group(4).toLowerCase(), m.group(1), m.group(2), m.group(3), data, session); }
		debug("INVITE", data);
		return null;
	}

	private static void debug(String method, String data)
	{
		if (!ConnectionManager.debug) { return; }
		log.info("Returning null from " + method + " in IRCEventFactory. Offending data:");
		log.info(data);
	}

	static Logger log = Logger.getLogger(IRCEventFactory.class.getName());
}
