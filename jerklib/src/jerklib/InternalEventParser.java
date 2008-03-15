/*

 Jason Davis - 2005 | mohadib@openactive.org 

 jerklib.InternalIRCEventParser

 This file is part of JerkLib Java IRC Library.

 JerkLib is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 JerkLib is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GenricPlayer; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 

 */

package jerklib;

import jerklib.ServerInformation.ModeType;
import jerklib.events.*;
import jerklib.events.impl.ModeEventImpl;
import jerklib.events.impl.ServerInformationEventImpl;
import jerklib.events.impl.TopicEventImpl;
import jerklib.events.impl.WhoisEventImpl;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InternalEventParser is the first place IRCEvents are sent. Here they are
 * parsed and made into more specific events then re-dispachted to any listeners
 * via the ConnectionManager. <p/> This class is mostly convience for users of
 * the lib , however some internal stuff depends on this class as well. Like
 * keeping track of nicks in a channel or knowing when a connection is ready to
 * join channels etc. <p/> Thanks to Gracenotes for the help
 * 
 * @author mohadib
 */
public class InternalEventParser
{
	private ConnectionManager manager;
	private Map<Channel, TopicEvent> topicMap = new HashMap<Channel, TopicEvent>();
	private WhoisEventImpl we;
	Logger log = Logger.getLogger(this.getClass().getName());

	public InternalEventParser(ConnectionManager manager)
	{
		this.manager = manager;
	}

	/**
	 * Takes an IRCEvent and tries to parse it into a more specific event then
	 * redispatch the more specfic event.
	 * 
	 * @param event
	 *          <code>IRCEvent</code> the event to parse
	 */
	void parseEvent(IRCEvent event)
	{
		Session session = event.getSession();
		String data = event.getRawEventData();
		String nick = session.getNick();

		EventToken eventToken = new EventToken(data);

		List<Token> tokens = eventToken.getWordTokens();
		if (tokens.isEmpty()) return;

		String command = tokens.get(1).data;

		if (command.matches("^\\d{3}$"))
		{
			numericEvent(eventToken, session, event, Integer.parseInt(command));
		}
		else if (command.equals("PRIVMSG"))
		{
			message(eventToken, session);
		}
		else if (command.equals("QUIT"))
		{
			QuitEvent qEvent = IRCEventFactory.quit(eventToken, session);
			session.removeNickFromAllChannels(qEvent.getWho());
			manager.addToRelayList(qEvent);
		}
		else if (command.equals("JOIN"))
		{
			Pattern p = Pattern.compile("^:\\Q" + nick + "\\E\\!.*?\\s+JOIN\\s+:?(\\S+)$");
			Matcher m = p.matcher(data);
			if (m.matches())
			{
				Channel channel = new Channel(m.group(1).toLowerCase(), session);
				session.addChannel(channel);
				manager.addToRelayList(IRCEventFactory.joinCompleted(data, session, nick, channel));
			}
			else
			{
				JoinEvent jEvent = IRCEventFactory.regularJoin(data, session);
				jEvent.getChannel().addNick(jEvent.getNick());
				manager.addToRelayList(jEvent);
			}
		}
		else if (command.equals("MODE"))
		{
			mode(event);
		}
		else if (command.equals("PART"))
		{
			PartEvent pEvent = IRCEventFactory.part(eventToken, session);
			if (!pEvent.getChannel().removeNick(pEvent.getWho()))
			{
				System.err.println("Could Not remove nick " + pEvent.getWho() + " from " + pEvent.getChannelName());
			}
			if (pEvent.getWho().equalsIgnoreCase(nick))
			{
				session.removeChannel(pEvent.getChannel());
			}
			manager.addToRelayList(pEvent);
		}
		else if (command.equals("NOTICE"))
		{
			manager.addToRelayList(IRCEventFactory.notice(data, session));
		}
		else if (command.equals("TOPIC"))
		{
			Pattern p = Pattern.compile("^.+?TOPIC\\s+(.+?)\\s+.*$");
			Matcher m = p.matcher(data);
			m.matches();
			event.getSession().sayRaw("TOPIC " + m.group(1));
		}
		else if (command.equals("INVITE"))
		{
			manager.addToRelayList(IRCEventFactory.invite(data, session));
		}
		else if (command.equals("NICK"))
		{
			NickChangeEvent nEvent = IRCEventFactory.nickChange(eventToken , session);
			session.nickChanged(nEvent.getOldNick(), nEvent.getNewNick());
			if (nEvent.getOldNick().equals(nick))
			{
				event.getSession().updateProfileSuccessfully(true);
			}
			manager.addToRelayList(nEvent);
		}
		else if (command.equals("KICK"))
		{
			KickEvent ke = IRCEventFactory.kick(eventToken, session);
			if (!ke.getChannel().removeNick(ke.getWho()))
			{
				log.info("COULD NOT REMOVE NICK " + ke.getWho() + " from channel " + ke.getChannel().getName());
			}

			if (ke.getWho().equals(nick))
			{
				session.removeChannel(ke.getChannel());
				if (session.isRejoinOnKick())
				{
					session.join(ke.getChannel().getName());
				}
			}
			manager.addToRelayList(ke);
		}
		else if (data.matches("^PING.*"))
		{
			session.getConnection().pong(event);
			manager.addToRelayList(event);
		}
		else if (data.matches(".*PONG.*"))
		{
			session.getConnection().gotPong();
			manager.addToRelayList(event);
		}
		else if (data.matches("^NOTICE\\s+(.*$)$"))
		{
			manager.addToRelayList(IRCEventFactory.notice(data, session));
		}
		else
		{
			manager.addToRelayList(event);
		}
	}

	// :kubrick.freenode.net 324 mohadib__ #test +mnPzlfJ 101 #flood 1,2
	private void channelMode(IRCEvent event)
	{
		Pattern p = Pattern.compile("^\\S+\\s+\\S+\\s+\\S+\\s+(\\S+)\\s+(.+)$");
		Matcher m = p.matcher(event.getRawEventData());
		m.matches();

		event.getSession().getChannel(m.group(1)).setModeString(m.group(2));
		ModeEvent me = new ModeEventImpl(event.getRawEventData(), event.getSession(), null, null, event.getSession().getChannel(m.group(1)));

		// notify with a Mode EVent
		manager.addToRelayList(me);
	}

	// :mohadib_!n=mohadib@unaffiliated/mohadib MODE #jerklib +o scripyasas
	// :services. MODE mohadib :+e
	private void mode(IRCEvent event)
	{
		if (log.isLoggable(Level.FINE))
		{
			log.fine(event.getRawEventData());
		}
		String[] rawTokens = event.getRawEventData().split("\\s+");
		String[] rawModeTokens = rawTokens[3].split("");
		String[] modeTokens = new String[rawModeTokens.length - 1];
		System.arraycopy(rawModeTokens, 1, modeTokens, 0, rawModeTokens.length - 1);
		String[] arguments = new String[rawTokens.length - 4];
		System.arraycopy(rawTokens, 4, arguments, 0, arguments.length);

		Map<String, List<String>> modeMap = new HashMap<String, List<String>>();

		/* see if user mode */
		ServerInformation info = event.getSession().getServerInformation();
		String[] channelPrefixes = info.getChannelPrefixes();
		boolean userMode = true;
		for (String prefix : channelPrefixes)
		{
			if (rawTokens[2].startsWith(prefix))
			{
				userMode = false;
			}
		}

		if (userMode)
		{
			// do something..

			if (log.isLoggable(Level.INFO))
			{
				log.info("MODE  " + Arrays.toString(modeTokens));
			}

			// free mode adds a : for some reason
			if (modeTokens[0].equals(":"))
			{
				String[] cleanTokens = new String[modeTokens.length - 1];
				System.arraycopy(modeTokens, 1, cleanTokens, 0, modeTokens.length - 1);
				modeTokens = cleanTokens;
			}

			char action = '+';
			List<String> targets = new ArrayList<String>();
			targets.add(event.getSession().getNick());
			for (String mode : modeTokens)
			{
				if (mode.equals("+") || mode.equals("-"))
				{
					action = mode.charAt(0);
				}
				else
				{
					modeMap.put(action + mode, targets);
				}
			}

			ModeEvent me = new ModeEventImpl(event.getRawEventData(), event.getSession(), modeMap, rawTokens[0].substring(1).split("\\!")[0], null);

			manager.addToRelayList(me);

			return;
		}

		char action = '+';
		int argumntOffset = 0;

		for (String mode : modeTokens)
		{
			if (mode.equals("+") || mode.equals("-"))
			{
				action = mode.charAt(0);
			}
			else
			{
				ModeType type = info.getTypeForMode(mode);
				// must have an argument on + and -
				if (type == ModeType.GROUP_A || type == ModeType.GROUP_B)
				{
					List<String> modeArgs = modeMap.get(action + mode);
					if (modeArgs == null)
					{
						modeArgs = new ArrayList<String>();
					}
					modeArgs.add(arguments[argumntOffset]);
					// System.err.println("Mode " + action + mode + " " +
					// arguments[argumntOffset] );
					argumntOffset++;
					modeMap.put(action + mode, modeArgs);
				}
				// must have args on + : must not have args on -
				else if (type == ModeType.GROUP_C)
				{
					List<String> modeArgs = modeMap.get(action + mode);
					if (modeArgs == null)
					{
						modeArgs = new ArrayList<String>();
					}
					if (action == '-')
					{
						if (!modeMap.containsKey(action + mode))
						{
							modeMap.put(action + mode, new ArrayList<String>());
							// System.err.println("Mode " + action + mode);
						}
					}
					else
					{
						modeArgs.add(arguments[argumntOffset]);
						// System.err.println("Mode " + action + mode + " " +
						// arguments[argumntOffset] );
						argumntOffset++;
						modeMap.put(action + mode, modeArgs);
					}
				}
				// no args
				else if (type == ModeType.GROUP_D)
				{
					modeMap.put(action + mode, new ArrayList<String>());
					// System.err.println("Mode " + action + mode);
				}
				else
				{
					// System.err.println("unreconzied mode " + mode);
				}
			}
		}
		// update channel object for o and v mode events
		Channel chan = event.getSession().getChannel(rawTokens[2]);

		List<String> voicedNicks = modeMap.get("+v") == null ? new ArrayList<String>() : modeMap.get("+v");
		List<String> opedNicks = modeMap.get("+o") == null ? new ArrayList<String>() : modeMap.get("+o");
		List<String> devoicedNicks = modeMap.get("-v") == null ? new ArrayList<String>() : modeMap.get("-v");
		List<String> deopedNicks = modeMap.get("-o") == null ? new ArrayList<String>() : modeMap.get("-o");

		for (String nick : voicedNicks)
		{
			chan.updateUsersMode(nick, "+v");
		}
		for (String nick : opedNicks)
		{
			chan.updateUsersMode(nick, "+o");
		}
		for (String nick : devoicedNicks)
		{
			chan.updateUsersMode(nick, "-v");
		}
		for (String nick : deopedNicks)
		{
			chan.updateUsersMode(nick, "-o");
		}

		ModeEvent me = new ModeEventImpl(event.getRawEventData(), event.getSession(), modeMap, rawTokens[0].substring(1).split("\\!")[0], chan);

		// notify with a Mode EVent
		manager.addToRelayList(me);
	}

	/*
	 * :kubrick.freenode.net 311 scripy mohadib n=fran unaffiliated/mohadib *
	 * :fran :kubrick.freenode.net 319 scripy mohadib :#jerklib
	 * :kubrick.freenode.net 312 scripy mohadib irc.freenode.net
	 * :http://freenode.net/ :kubrick.freenode.net 320 scripy mohadib :is
	 * identified to services :kubrick.freenode.net 318 scripy mohadib :End of
	 * /WHOIS list.
	 */
	private void whois(EventToken token, Session session, int numeric)
	{
		switch (numeric)
		{
		case 311:
		{
			// "<nick> <user> <host> * :<real name>"
			we = (WhoisEventImpl) IRCEventFactory.whois(token, session);
			break;
		}
		case 319:
		{
			// "<nick> :{[@|+]<channel><space>}"
			// :kubrick.freenode.net 319 scripy mohadib :@#jerklib
			// kubrick.freenode.net 319 scripy mohadib :@#jerklib ##swing
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				List<String> chanNames = Arrays.asList(m.group(1).split("\\s+"));
				we.setChannelNamesList(chanNames);
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// "<nick> <server> :<server info>"
			// :kubrick.freenode.net 312 scripy mohadib irc.freenode.net
			// :http://freenode.net/
		case 312:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s(\\S+)\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				we.setWhoisServer(m.group(1));
				we.setWhoisServerInfo(m.group(2));
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// not in RFC1459
			// :kubrick.freenode.net 320 scripy mohadib :is identified to services
		case 320:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(\\S+)\\s:(.*)$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				// System.out.println("nick idented: " + m.group(1) + " " + m.group(2));
				we.appendRawEventData(token.getData());
			}
			break;
		}
			// :anthony.freenode.net 317 scripy scripy 2 1202063240 :seconds idle,
			// signon time
			// from rfc "<nick> <integer> :seconds idle"
		case 317:
		{
			Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s\\S+\\s+(\\d+)\\s+(\\d+)\\s+:.*$");
			Matcher m = p.matcher(token.getData());
			if (we != null && m.matches())
			{
				we.setSignOnTime(Integer.parseInt(m.group(2)));
				we.setSecondsIdle(Integer.parseInt(m.group(1)));
			}
		}
		case 318:
		{
			// end of whois - fireevent
			if (we != null)
			{
				we.appendRawEventData(token.getData());
				manager.addToRelayList(we);
				we = null;
			}
			break;
		}
		default:
			log.info(token.getData());
			break;
		}
	}

	private void serverInfo(EventToken token, IRCEvent event)
	{
		Session session = (Session) event.getSession();
		session.getServerInformation().parseServerInfo(token.getData());
		ServerInformationEventImpl se = new ServerInformationEventImpl(session, token.getData(), session.getServerInformation());
		manager.addToRelayList(se);
	}

	private void numericEvent(EventToken token , Session session, IRCEvent event, int numeric)
	{
		switch (numeric)
		{
		case 001:
			connectionComplete(token, session, event);
			break;
		case 005:
			serverInfo(token, event);
			break;
		case 301:
			manager.addToRelayList(IRCEventFactory.away(token.getData(), session, numeric));
			break;
		case 305:
			manager.addToRelayList(IRCEventFactory.away(token.getData() , session, numeric));
			break;
		case 306:
			manager.addToRelayList(IRCEventFactory.away(token.getData(), session, numeric));
			break;
		case 314:
			manager.addToRelayList(IRCEventFactory.whowas(token, session));
			break;
		case 311:// whois
		case 312:// whois
		case 317:// whois
		case 318:// whois
		case 319:// whois
		case 320:
			whois(token, event.getSession(), numeric);
			break;
		case 321:
			break;// chanlist
		case 322:
			manager.addToRelayList(IRCEventFactory.chanList(token, session));
			break;
		case 323:
			break; // end chan ist
		case 324:
			channelMode(event);
			break;
		case 332:
			firstPartOfTopic(token, session);
			break;
		case 333:
			secondPartOfTopic(token, session);
			break;
		case 351:
			manager.addToRelayList(IRCEventFactory.serverVersion(token, session));
			break;
		case 352:
			manager.addToRelayList(IRCEventFactory.who(token, session));
			break;
		case 353:
			namesLine(token, session);
			manager.addToRelayList(event);
			break;
		case 366:
			manager.addToRelayList(IRCEventFactory.nickList(token, session));
			break;
		case 372:// motd
		case 375:// motd
		case 376:
			manager.addToRelayList(IRCEventFactory.motd(token, session));
			break;
		case 433:
			nick(token, session);
			break;
		case 401:
		case 402:
		case 403:
		case 404:
		case 405:
		case 406:
		case 407:
		case 409:
		case 411:
		case 412:
		case 413:
		case 414:
		case 421:
		case 422:
		case 423:
		case 424:
		case 431:
		case 432:
		case 436:
		case 441:
		case 442:
		case 443:
		case 444:
		case 445:
		case 446:
		case 451:
		case 461:
		case 462:
		case 463:
		case 464:
		case 465:
		case 467:
		case 471:
		case 472:
		case 473:
		case 474:
		case 475:
		case 481:
		case 482:
		case 483:
		case 491:
		case 501:
		case 502:
			manager.addToRelayList(IRCEventFactory.numericError(token, session, numeric));
			break;
		default:
			manager.addToRelayList(event);
		}
	}

	private void firstPartOfTopic(EventToken token, Session session)
	{
		// FIRST PART TOF TOPPIC;
		// sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is
		// for testing only.
		// if (data.matches(":(.+?)\\s+332\\s+(.+?)\\s+(" + channelPrefixRegex
		// +".+?)\\s+:(.*)$"))
		// {
		TopicEvent tEvent = IRCEventFactory.topic(token, session);
		if (topicMap.containsValue(tEvent.getChannel()))
		{
			((TopicEventImpl) topicMap.get(tEvent.getChannel())).appendToTopic(tEvent.getTopic());
		}
		else
		{
			topicMap.put(tEvent.getChannel(), tEvent);
		}
		// }
	}

	private void secondPartOfTopic(EventToken token, Session session)
	{
		// 2nd part of topic
		// :zelazny.freenode.net 333 scrip #test LuX 1159267246
		// if (data.matches(":(.+?)\\s+333\\s+(.+?)\\s+("+
		// channelPrefixRegex+".+?)\\s+(\\S+)\\s+(\\S+)$"))
		// {
		Pattern p = Pattern.compile(":(\\S+)\\s+333\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)$");
		Matcher m = p.matcher(token.getData());
		m.matches();
		Channel chan = (Channel) session.getChannel(m.group(3).toLowerCase());
		if (topicMap.containsKey(chan))
		{
			TopicEventImpl tEvent = (TopicEventImpl) topicMap.get(chan);
			topicMap.remove(chan);
			tEvent.setSetBy(m.group(4));
			tEvent.setSetWhen(m.group(5));
			chan.setTopicEvent(tEvent);
			manager.addToRelayList(tEvent);
		}
		// }
	}

	Random rand = new Random();

	private void nick(EventToken token, Session session)
	{
		/* NICK IN USE */
		// :simmons.freenode.net 433 * fran :Nickname is already in use.
		// if (data.matches(":\\S+\\s433\\s.+?\\s\\S+\\s:?.*$"))
		// {
		if (session.isConnected() && session.isProfileUpdating())
		{
			session.updateProfileSuccessfully(false);
		}

		if (!session.getConnection().loggedInSuccessfully())
		{
			Profile p = session.getRequestedConnection().getProfile();
			String aNick = p.getActualNick();
			String newNick = p.getFirstNick() + rand.nextInt(100);
			if (aNick.equals(p.getFirstNick()))
			{
				newNick = p.getSecondNick();
			}
			else if (aNick.equals(p.getSecondNick()))
			{
				newNick = p.getThirdNick();
			}
			((ProfileImpl) p).setActualNick(newNick);
			session.changeProfile(p);
		}

		manager.addToRelayList(IRCEventFactory.nickInUse(token, session));
		// }

	}

	private void connectionComplete(EventToken token, Session session, IRCEvent event)
	{
		/*
		 * CONNECTION COMPLETE irc,freenode.net might actually be
		 * niveen.freenode.net :irc.nmglug.org 001 namnar :Welcome to the nmglug.org
		 * Internet Relay Chat Network namnar
		 */
		ConnectionCompleteEvent ccEvent = IRCEventFactory.connectionComplete(token, session);
		session.getConnection().loginSuccess();
		session.getConnection().setHostName(ccEvent.getActualHostName());
		manager.addToRelayList(ccEvent);
	}

	private void message(EventToken token, Session session)
	{
		
		MessageEvent me = IRCEventFactory.message(token , session);

		String msg = me.getMessage();

		if (msg.startsWith("\u0001"))
		{
			String ctcpString = msg.substring(0, msg.length() - 1).substring(1);
			me = IRCEventFactory.ctcp(me, ctcpString);
		}
		manager.addToRelayList(me);
	}

	private void namesLine(EventToken token, Session session)
	{
		Pattern p = Pattern.compile("^:(?:.+?)\\s+353\\s+\\S+\\s+(?:=|@)\\s+(\\S+)\\s:(.+)$");
		Matcher m = p.matcher(token.getData());
		if (m.matches())
		{
			Channel chan = session.getChannel(m.group(1).toLowerCase());
			String[] names = m.group(2).split("\\s+");

			for (String name : names)
			{
				// remove @ and + from front for voice and ops ?
				if (name != null && name.length() > 0)
				{
					chan.addNick(name);
				}
			}
		}
	}

}
