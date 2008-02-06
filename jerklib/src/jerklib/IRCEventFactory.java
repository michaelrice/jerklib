package jerklib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.MotdEvent;
import jerklib.events.ModeEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.NickInUseEvent;
import jerklib.events.NickListEvent;
import jerklib.events.NoticeEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.PartEvent;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.PrivateMsgEvent;
import jerklib.events.QuitEvent;
import jerklib.events.ServerVersionEvent;
import jerklib.events.TopicEvent;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.ChannelListEvent;
import jerklib.events.InviteEvent;
import jerklib.events.WhoisEvent;
import jerklib.events.WhowasEvent;
import jerklib.events.AwayEvent;
import jerklib.events.WhoEvent;
import jerklib.events.NumericErrorEvent.ErrorType;
import jerklib.events.impl.JoinCompleteEventImpl;
import jerklib.events.impl.JoinEventImpl;
import jerklib.events.impl.KickEventImpl;
import jerklib.events.impl.MotdEventImpl;
import jerklib.events.impl.ModeEventImpl;
import jerklib.events.impl.NickChangeEventImpl;
import jerklib.events.impl.NickInUseEventImpl;
import jerklib.events.impl.NickListEventImpl;
import jerklib.events.impl.NoticeEventImpl;
import jerklib.events.impl.NumericEventImpl;
import jerklib.events.impl.PartEventImpl;
import jerklib.events.impl.ChannelMsgEventImpl;
import jerklib.events.impl.PrivateMessageEventImpl;
import jerklib.events.impl.QuitEventImpl;
import jerklib.events.impl.ServerVersionEventImpl;
import jerklib.events.impl.TopicEventImpl;
import jerklib.events.impl.ConnectionCompleteEventImpl;
import jerklib.events.impl.ChannelListEventImpl;
import jerklib.events.impl.InviteEventImpl;
import jerklib.events.impl.WhoisEventImpl;
import jerklib.events.impl.WhowasEventImpl;
import jerklib.events.impl.AwayEventImpl;
import jerklib.events.impl.WhoEventImpl;

class IRCEventFactory
{
	private static ConnectionManager myManager;
	private static Map<Integer, ErrorType> numericErrorMap;	

	static void setManager(ConnectionManager manager)
	{
		myManager = manager;
		initNumericErrorMap();
	}

	// :kubrick.freenode.net 351 scripy hyperion-1.0.2b(382). kubrick.freenode.net
	// :iM dncrTS/v4
	// "<version>.<debuglevel> <server> :<comments>"
	static ServerVersionEvent serverVersion(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:\\S+\\s351\\s\\S+\\s(\\S+)\\s(\\S+)\\s:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new ServerVersionEventImpl(m.group(3), m.group(2), m.group(1), "", data, myManager.getSessionFor(con)); }
		return null;
	}

	static ConnectionCompleteEvent connectionComplete(String data, Connection con)
	{
		System.out.println("DATA:" + data + " " + con.getProfile().getActualNick());
		Pattern p = Pattern.compile(":(\\S+)\\s+001\\s+\\Q" + con.getProfile().getActualNick() + "\\E\\s+:.*$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			/* send host name changed event so users of lib can update *records* */
			ConnectionCompleteEvent e = new ConnectionCompleteEventImpl
			(
					data, 
					m.group(1).toLowerCase(), // new
					myManager.getSessionFor(con), 
					con.getHostName() // old hostname
			);

			return e;
		}
		return null;
	}

	// :simmons.freenode.net 352 r0bby_ * n=wakawaka guifications/user/r0bby
	// irc.freenode.net r0bby H :0 Robert O'Connor
	static WhoEvent who(String data, Connection con)
	{
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
					myManager.getSessionFor(con), // session
					m.group(2)); // username

		}
		return null;

	}

	// :kubrick.freenode.net 314 scripy1 ty n=ty 71.237.206.180 * :ty
	// "<nick> <user> <host> * :<real name>"
	static WhowasEvent whowas(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:\\S+\\s314\\s\\S+\\s(\\S+)\\s(\\S+)\\s(\\S+).+?:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new WhowasEventImpl(m.group(3), m.group(2), m.group(1), m.group(4), data, myManager.getSessionFor(con)); }
		return null;
	}

	static NumericErrorEvent numericError(String data, Connection con, int numeric)
	{
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new NumericEventImpl(m.group(1), data, numericErrorMap.get(numeric), numeric, myManager.getSessionFor(con)); }
		return null;
	}

	static WhoisEvent whois(String data, Session session)
	{
		// "<nick> <user> <host> * :<real name>"
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(\\S+)\\s(\\S+)\\s(\\S+).*?:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new WhoisEventImpl(m.group(1), m.group(4), m.group(2), m.group(3), data, session); }
		return null;
	}

	/*
	 * end of names :irc.newcommunity.tummy.com 366 SwingBot #test :End of NAMES
	 * list
	 */
	static NickListEvent nickList(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(?:.*?)\\s+366\\s+\\S+\\s+(.*?)\\s+.*$");
		Matcher m = p.matcher(data);

		if (m.matches())
		{
			NickListEvent nle = new NickListEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				con.getChannel(m.group(1).toLowerCase()), 
				con.getChannel(m.group(1).toLowerCase()).getNicks()
			);
			return nle;
		}

		return null;
	}

	/* :mohadib!~mohadib@67.41.102.162 KICK #test scab :bye! */
	static KickEvent kick(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+KICK\\s+(\\S+)\\s+(\\S+)\\s+:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			KickEvent ke = new KickEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(1), // byWho
				m.group(2), // username
				m.group(3), // host name
				m.group(5),// victim
				m.group(6), // message
				con.getChannel(m.group(4).toLowerCase())
			);
			return ke;
		}
		return null;
	}

	// sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is
	// for testing only.
	static TopicEvent topic(String data, Connection con)
	{
		Pattern p = Pattern.compile(":(\\S+)\\s+332\\s+(\\S+)\\s+(#\\S+)\\s+:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			TopicEvent topicEvent = new TopicEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				con.getChannel(m.group(3).toLowerCase()), 
				m.group(4)
			);
			
			return topicEvent;
		}

		return null;
	}

	// :mohadib!n=fran@unaffiliated/mohadib MODE #jerklib +b scripy!*@*
	// :mohadib!n=fran@unaffiliated/mohadib MODE #jerklib -m
	// :services. MODE scripy :+e
	static ModeEvent modeEvent(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+)\\s+MODE\\s+(\\S+)\\s+(\\S+)\\s*$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			ModeEvent me = new ModeEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(3), 
				m.group(2), 
				m.group(1),
				null
			);
			return me;
		}
		
		p = Pattern.compile("^:(\\S+)\\s+MODE\\s+(\\S+)\\s+(\\S+)(?:\\s+(\\S+)\\s*)?");
		m = p.matcher(data);
		if (m.matches())
		{
			System.out.println("MATCHEDDDDDDDDDd");
			String user = m.group(1);
			if(user.indexOf("!") != -1)
			{
				user = user.split("!")[0];
			}
			ModeEvent me = new ModeEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(3), 
				m.group(4), 
				user, 
				con.getChannel(m.group(2).toLowerCase())
			);
			return me;
		}

		return null;
	}

	/*
	 * A Channel Msg :fuknuit!~admin@212.199.146.104 PRIVMSG #debian :blah blah
	 * Private message :mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!!
	 */
	static ChannelMsgEvent channelMsg(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+?)\\!(\\S+?)@(\\S+)\\s+PRIVMSG\\s+(\\S+)\\s+:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			ChannelMsgEvent channelMsgEvent = new ChannelMsgEventImpl
			(
					data, 
					myManager.getSessionFor(con), 
					con.getChannel(m.group(4).toLowerCase()), // channel
					m.group(1), // nick
					m.group(2), // user name
					m.group(5), // message
					m.group(3) // nicks host
			);

			return channelMsgEvent;
		}

		return null;
	}

	// the_horrible is the nick that is in use
	// fran is the current nickname
	/* :simmons.freenode.net 433 fran the_horrible :Nickname is already in use. */

	// * is used for current nick when this event happens on connect
	/* :simmons.freenode.net 433 * fran :Nickname is already in use. */
	static NickInUseEvent nickInUse(String data, Connection con)
	{
		Pattern p = Pattern.compile("\\S+\\s433\\s.*?\\s(\\S+)\\s:?.*$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			NickInUseEvent event = new NickInUseEventImpl(m.group(1), data, myManager.getSessionFor(con));

			return event;
		}
		return null;
	}

	
    // :leguin.freenode.net 306 r0bby_ :You have been marked as being away
    // :leguin.freenode.net 305 r0bby_ :You are no longer marked as being away
  static AwayEvent away(String data, Connection con, int numeric)
	{
		Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s+(\\S+)\\s:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			switch(numeric) 
			{
				case 305:return new AwayEventImpl(myManager.getSessionFor(con), AwayEvent.EventType.RETURNED_FROM_AWAY, false, true, myManager.getDefaultProfile().getActualNick(), data);	
				case 306:return new AwayEventImpl(myManager.getSessionFor(con), AwayEvent.EventType.WENT_AWAY, true, true, myManager.getDefaultProfile().getActualNick(), data);
      }
		}
	// :card.freenode.net 301 r0bby_ r0bby :foo
		p = Pattern.compile("^:\\S+\\s+\\d{3}\\s+\\S+\\s+(\\S+)\\s+:(.*)$");
		m = p.matcher(data);
		m.matches();
		return new AwayEventImpl(m.group(2), AwayEvent.EventType.USER_IS_AWAY, true, false, m.group(1), data, myManager.getSessionFor(con));
   }

	//:anthony.freenode.net 375 mohadib_ :- anthony.freenode.net Message of the Day -
	//:anthony.freenode.net 372 mohadib_ :- Welcome to anthony.freenode.net in Irvine, CA, USA!  Thanks to
	//:anthony.freenode.net 376 mohadib_ :End of /MOTD command.
	static MotdEvent motd(String data, Connection con)
	{
		Pattern p = Pattern.compile(":(\\S+)\\s+\\d+\\s+(\\Q" + con.getProfile().getActualNick() + "\\E)\\s+:(.*)$");
		Matcher m = p.matcher(data);

		if (!m.matches()) return null;

		return new MotdEventImpl(data, myManager.getSessionFor(con), m.group(3) , m.group(1));
	}

	static NoticeEvent notice(String data, Connection con)
	{
		
		
		// generic notice NOTICE AUTH :*** No identd (auth) response
		Pattern p = Pattern.compile("^NOTICE\\s+(.*$)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			NoticeEvent noticeEvent = new NoticeEventImpl(data, myManager.getSessionFor(con), "generic", m.group(1), "", "", null);

			return noticeEvent;
		}
		
		// channel notice :DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE #jerklib :test
		p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(#.*?)\\s+:(.*)$");
		m = p.matcher(data);
		if (m.matches())
		{
			NoticeEvent ne = new NoticeEventImpl(data, myManager.getSessionFor(con), "channel", m.group(3), "", m.group(1), con.getChannel(m.group(2).toLowerCase()));

			return ne;
		}

		// user notice :NickServ!NickServ@services. NOTICE mohadib_ :This nickname is owned by someone else
		p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(.*?)\\s+:(.*)$");
		m = p.matcher(data);
		if (m.matches())
		{
			NoticeEvent ne = new NoticeEventImpl(data, myManager.getSessionFor(con), "user", m.group(3), m.group(2), m.group(1), null);

			return ne;
		}
		
		
		//user notice but from the server - user notice means to a user as opposed a channel
		//:anthony.freenode.net NOTICE mohadib_ :NickServ set your hostname to "unaffiliated/mohadib"
		p = Pattern.compile("^:(\\S+)\\s+NOTICE\\s+(\\S+)\\s+:(.*)$");
		m = p.matcher(data);
		if (m.matches())
		{
			NoticeEvent ne = new NoticeEventImpl(data, myManager.getSessionFor(con), "user", m.group(3), m.group(2), m.group(1), null);

			return ne;
		}

		return null;
	}

	//TODO
	//:anthony.freenode.net 391 scripy anthony.freenode.net :Monday February 4 2008 -- 18:02:31 -08:00
	static void ServerTimeEvent()
	{
		
	}
	
	/*
	 * :r0bby__!n=wakawaka@cpe-24-164-167-171.hvc.res.rr.com QUIT :Client Quit
	 * PERSON QUIT :Xolt!brad@c-67-165-231-230.hsd1.co.comcast.net QUIT :"Deleted"
	 * :james_so!~me@213-152-46-35.dsl.eclipse.net.uk QUIT :Read error: 60 (Operation timed out)
	 */
	static QuitEvent quit(String data, Connection con)
	{
		Pattern pattern = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+QUIT\\s+:(.*)$");
		Matcher matcher = pattern.matcher(data);
		if (matcher.matches())
		{
			List<Channel> chanList = con.removeNickFromAllChannels(matcher.group(1));
			QuitEvent quitEvent = new QuitEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				matcher.group(1), // who
				matcher.group(2), // username
				matcher.group(3), // hostName
				matcher.group(4), // message
				chanList
			);
			return quitEvent;
		}
		return null;
	}

	// :r0bby!n=wakawaka@guifications/user/r0bby JOIN :#jerklib
	static JoinEvent regularJoin(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+JOIN\\s+:(\\S+)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			try
			{
			JoinEvent joinEvent = new JoinEventImpl
			(
				data, 
				myManager.getSessionFor(con),
				m.group(1), // nick
				m.group(2), // user name
				m.group(3).toLowerCase(), // host
				con.getChannel(m.group(4).toLowerCase()).getName(), // channel name
				con.getChannel(m.group(4).toLowerCase()) // channel
			);
			return joinEvent;
			}
			catch (Exception e) 
			{
				System.err.println(data);
				for (Channel chan : con.getChannels())
				{
					System.err.println(chan.getName());
				}
				e.printStackTrace();
			}
		}
		return null;
	}

	//TODO change this to use channel names , not objects
	/*
	 * :anthony.freenode.net 322 mohadib_ #jerklib 5 :JerkLib IRC Library - https://sourceforge.net/projects/jerklib
	 */
	static ChannelListEvent chanList(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:\\S+\\s322\\s\\S+\\s(#\\S+)\\s(\\d+)\\s:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			Channel channel = new Channel(m.group(1), con);
			channel.setTopic(m.group(3));
			return new ChannelListEventImpl(data, m.group(1), m.group(3), Integer.parseInt(m.group(2)), myManager.getSessionFor(con));
		}
		return null;
	}

	static JoinCompleteEvent joinCompleted(String data, Connection con, String nick, Channel channel)
	{
		return new JoinCompleteEventImpl(data, myManager.getSessionFor(con), channel);
	}
	// :fran!~fran@outsiderz-88006847.hsd1.nm.comcast.net PART #jerklib
	//          :r0bby!n=wakawaka@guifications/user/r0bby PART #jerklib :"FOO"
	static PartEvent part(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+PART\\s+(\\S+?)(?:\\s+:(.*))?$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			PartEvent partEvent = new PartEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(1), // who
				m.group(2), // username
				m.group(3), // host name
				con.getChannel(m.group(4).toLowerCase()).getName(), // channel name
				con.getChannel(m.group(4).toLowerCase()), 
				m.group(5) // part message
			);

			return partEvent;
		}
		else
		{
			System.err.println("NO MATCH");
		}
		return null;
	}

	/*
	 * A Channel Msg :fuknuit!~admin@212.199.146.104 PRIVMSG #debian :blah blah
	 * Private message :mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!!
	 */
	static PrivateMsgEvent privateMsg(String data, Connection con, String nick)
	{
		Pattern p = Pattern.compile("^:(\\S+?)\\!(\\S+?)@(\\S+?)\\sprivmsg\\s\\Q" + nick + "\\E\\s+:(.*)$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(data);

		if (m.matches())
		{
			PrivateMsgEvent privateMsg = new PrivateMessageEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(1), // nick
				m.group(2), // user name
				m.group(4), // message
				m.group(3) // nicks host
			);

			return privateMsg;
		}
		return null;
	}

	//:raving!n=raving@74.195.43.119 NICK :Sir_Fawnpug
	static NickChangeEvent nickChange(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+)!(\\S+)@(\\S+)\\s+NICK\\s+:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			NickChangeEvent nickChangeEvent = new NickChangeEventImpl
			(
				data, 
				myManager.getSessionFor(con), 
				m.group(1), // old
				m.group(4), // new nick
				m.group(3), // hostname
				m.group(2) // username    
			);
			return nickChangeEvent;
		}
		return null;
	}

	//:irc.nmglug.org 001 namnar :Welcome to the nmglug.org
	static ConnectionCompleteEvent updateHostName(String data, Connection con, String oldHostName)
	{
		Pattern p = Pattern.compile(":(\\S+)\\s+001\\s+\\Q" + con.getProfile().getActualNick() + "\\E\\s+:.*$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			ConnectionCompleteEvent e = new ConnectionCompleteEventImpl
			(
				data, 
				m.group(1).toLowerCase(), // new hostname
				myManager.getSessionFor(con), 
				oldHostName// old hostname
			);
			return e;
		}
		return null;
	}

	// :r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2
	static InviteEvent invite(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+INVITE.+?:(.*)$");
		Matcher m = p.matcher(data);
		if (m.matches()) { return new InviteEventImpl(m.group(4).toLowerCase(), m.group(1), m.group(2), m.group(3), data, myManager.getSessionFor(con)); }
		return null;
	}

	static void initNumericErrorMap()
	{
		numericErrorMap = new HashMap<Integer, ErrorType>();
		numericErrorMap.put(401, ErrorType.ERR_NOSUCHNICK);
		numericErrorMap.put(402, ErrorType.ERR_NOSUCHSERVER);
		numericErrorMap.put(403, ErrorType.ERR_NOSUCHCHANNEL);
		numericErrorMap.put(404, ErrorType.ERR_CANNOTSENDTOCHAN);
		numericErrorMap.put(405, ErrorType.ERR_TOOMANYCHANNELS);
		numericErrorMap.put(406, ErrorType.ERR_WASNOSUCHNICK);
		numericErrorMap.put(407, ErrorType.ERR_TOOMANYTARGETS);
		numericErrorMap.put(409, ErrorType.ERR_NOORIGIN);
		numericErrorMap.put(411, ErrorType.ERR_NORECIPIENT);
		numericErrorMap.put(412, ErrorType.ERR_NOTEXTTOSEND);
		numericErrorMap.put(413, ErrorType.ERR_NOTOPLEVEL);
		numericErrorMap.put(414, ErrorType.ERR_WILDTOPLEVEL);
		numericErrorMap.put(421, ErrorType.ERR_UNKNOWNCOMMAND);
		numericErrorMap.put(422, ErrorType.ERR_NOMOTD);
		numericErrorMap.put(423, ErrorType.ERR_NOADMININFO);
		numericErrorMap.put(424, ErrorType.ERR_FILEERROR);
		numericErrorMap.put(431, ErrorType.ERR_NONICKNAMEGIVEN);
		numericErrorMap.put(432, ErrorType.ERR_ERRONEUSNICKNAME);
		numericErrorMap.put(433, ErrorType.ERR_NICKNAMEINUSE);
		numericErrorMap.put(436, ErrorType.ERR_NICKCOLLISION);
		numericErrorMap.put(441, ErrorType.ERR_USERNOTINCHANNEL);
		numericErrorMap.put(442, ErrorType.ERR_NOTONCHANNEL);
		numericErrorMap.put(443, ErrorType.ERR_USERONCHANNEL);
		numericErrorMap.put(444, ErrorType.ERR_NOLOGIN);
		numericErrorMap.put(445, ErrorType.ERR_SUMMONDISABLED);
		numericErrorMap.put(446, ErrorType.ERR_USERSDISABLED);
		numericErrorMap.put(451, ErrorType.ERR_NOTREGISTERED);
		numericErrorMap.put(461, ErrorType.ERR_NEEDMOREPARAMS);
		numericErrorMap.put(462, ErrorType.ERR_ALREADYREGISTRED);
		numericErrorMap.put(463, ErrorType.ERR_NOPERMFORHOST);
		numericErrorMap.put(464, ErrorType.ERR_PASSWDMISMATCH);
		numericErrorMap.put(465, ErrorType.ERR_YOUREBANNEDCREEP);
		numericErrorMap.put(467, ErrorType.ERR_KEYSET);
		numericErrorMap.put(471, ErrorType.ERR_CHANNELISFULL);
		numericErrorMap.put(472, ErrorType.ERR_UNKNOWNMODE);
		numericErrorMap.put(473, ErrorType.ERR_INVITEONLYCHAN);
		numericErrorMap.put(474, ErrorType.ERR_BANNEDFROMCHAN);
		numericErrorMap.put(475, ErrorType.ERR_BADCHANNELKEY);
		numericErrorMap.put(481, ErrorType.ERR_NOPRIVILEGES);
		numericErrorMap.put(482, ErrorType.ERR_CHANOPRIVSNEEDED);
		numericErrorMap.put(483, ErrorType.ERR_CANTKILLSERVER);
		numericErrorMap.put(491, ErrorType.ERR_NOOPERHOST);
		numericErrorMap.put(501, ErrorType.ERR_UMODEUNKNOWNFLAG);
		numericErrorMap.put(502, ErrorType.ERR_USERSDONTMATCH);
	}

}
