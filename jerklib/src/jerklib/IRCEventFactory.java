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

    // :kubrick.freenode.net 351 scripy hyperion-1.0.2b(382). kubrick.freenode.net
    // :iM dncrTS/v4
    // "<version>.<debuglevel> <server> :<comments>"
    static ServerVersionEvent serverVersion(String data, Session session)
    {
        Pattern p = Pattern.compile("^:\\S+\\s351\\s\\S+\\s(\\S+)\\s(\\S+)\\s:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new ServerVersionEventImpl
                    (
                            m.group(3),
                            m.group(2),
                            m.group(1),
                            "",
                            data,
                            session
                    );
        }
        debug("SERVER_VERSION", data);
        return null;
    }

    // :irc.nmglug.org 001 namnar :Welcome to the nmglug.org
    static ConnectionCompleteEvent connectionComplete(String data, Session session)
    {

        Pattern p = Pattern.compile(":(\\S+)\\s+001\\s+\\S+\\s+:.*$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            /* send host name changed event so users of lib can update *records* */
            return new ConnectionCompleteEventImpl
                    (
                            data,
                            m.group(1).toLowerCase(), // new
                            session,
                            session.getConnection().getHostName() // old hostname
                    );
        }
        debug("CONN_COMPLETE", data);
        return null;
    }

    // :simmons.freenode.net 352 r0bby_ * n=wakawaka guifications/user/r0bby
    // irc.freenode.net r0bby H :0 Robert O'Connor
    static WhoEvent who(String data, Session session)
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
                                    session, // session
                                    m.group(2) // username
            );
        }
        debug("WHO", data);
        return null;
    }

    // :kubrick.freenode.net 314 scripy1 ty n=ty 71.237.206.180 * :ty
    // "<nick> <user> <host> * :<real name>"
    static WhowasEvent whowas(String data, Session session)
    {
        Pattern p = Pattern.compile("^:\\S+\\s314\\s\\S+\\s(\\S+)\\s(\\S+)\\s(\\S+).+?:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new WhowasEventImpl
                    (
                            m.group(3),
                            m.group(2),
                            m.group(1),
                            m.group(4),
                            data,
                            session
                    );
        }
        debug("WHOWAS", data);
        return null;
    }

    static NumericErrorEvent numericError(String data, Session session, int numeric)
    {
        Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new NumericEventImpl
                    (
                            m.group(1),
                            data,
                            numeric,
                            session
                    );
        }
        debug("NUMERIC ERROR", data);
        return null;
    }

    static WhoisEvent whois(String data, Session session)
    {
        // "<nick> <user> <host> * :<real name>"
        Pattern p = Pattern.compile("^:\\S+\\s\\d{3}\\s\\S+\\s(\\S+)\\s(\\S+)\\s(\\S+).*?:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new WhoisEventImpl
                    (
                            m.group(1),
                            m.group(4),
                            m.group(2),
                            m.group(3),
                            data,
                            session
                    );
        }
        debug("WHOIS", data);
        return null;
    }

    /*
      * end of names :irc.newcommunity.tummy.com 366 SwingBot #test :End of NAMES
      * list
      */
    static NickListEvent nickList(String data, Session session)
    {
        Pattern p = Pattern.compile("^:(?:.*?)\\s+366\\s+\\S+\\s+(.*?)\\s+.*$");
        Matcher m = p.matcher(data);

        if (m.matches())
        {
            return new NickListEventImpl
                    (
                            data,
                            session,
                            session.getChannel(m.group(1).toLowerCase()),
                            session.getChannel(m.group(1).toLowerCase()).getNicks());
        }
        debug("NICK_LIST", data);
        return null;
    }

    /* :mohadib!~mohadib@67.41.102.162 KICK #test scab :bye! */
    static KickEvent kick(String data, Session session)
    {
        Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+KICK\\s+(\\S+)\\s+(\\S+)\\s+:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            Channel channel = session.getChannel(m.group(4).toLowerCase());
            return new KickEventImpl
                    (
                            data,
                            session,
                            m.group(1), // byWho
                            m.group(2), // username
                            m.group(3), // host name
                            m.group(5), // victim
                            m.group(6), // message
                            channel
                    );
        }
        debug("KICK", data);
        return null;
    }

    // sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is
    // for testing only.
    static TopicEvent topic(String data, Session session)
    {
        Pattern p = Pattern.compile(":(\\S+)\\s+332\\s+(\\S+)\\s+(\\S+)\\s+:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new TopicEventImpl
                    (
                            data,
                            session,
                            session.getChannel(m.group(3).toLowerCase()),
                            m.group(4));

        }

        debug("TOPIC", data);
        return null;
    }

    /*
      * A Channel Msg :fuknuit!~admin@212.199.146.104 PRIVMSG #debian :blah blah
      * Private message :mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!!
      */
    static MessageEvent privateMsg(String data, Session session, String channelPrefixRegex)
    {
        if (data.matches("^:\\S+\\s+PRIVMSG\\s+\\S+\\s+:.*$"))
        {
            Pattern p = Pattern.compile("^:(\\S+?)\\!(\\S+?)@(\\S+)\\s+PRIVMSG\\s+(\\S+)\\s+:(.*)$");
            Matcher m = p.matcher(data);
            m.matches();
            String target = m.group(4);
            return new MessageEventImpl
                    (
                            target.matches("^" + channelPrefixRegex + "{1}.+") ? session.getChannel(target.toLowerCase()) : null,
                            m.group(3),
                            m.group(5),
                            m.group(1),
                            data,
                            session,
                            target.matches("^" + channelPrefixRegex + "{1}.+") ? Type.CHANNEL_MESSAGE : Type.PRIVATE_MESSAGE, m.group(2)
                    );
        }

        debug("MESSAGE", data);
        return null;
    }

    static CtcpEvent ctcp(MessageEvent event, String ctcpString)
    {
        return new CtcpEventImpl
                (
                        ctcpString,
                        event.getHostName(),
                        event.getMessage(),
                        event.getNick(),
                        event.getUserName(),
                        event.getRawEventData(),
                        event.getChannel(),
                        event.getSession());
    }

    // the_horrible is the nick that is in use
    // fran is the current nickname
    /* :simmons.freenode.net 433 fran the_horrible :Nickname is already in use. */

    // * is used for current nick when this event happens on connect
    /* :simmons.freenode.net 433 * fran :Nickname is already in use. */

    static NickInUseEvent nickInUse(String data, Session session)
    {
        Pattern p = Pattern.compile("\\S+\\s433\\s.*?\\s(\\S+)\\s:?.*$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new NickInUseEventImpl(m.group(1), data, session);
        }
        debug("NICK_IN_USE", data);
        return null;
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

    // :anthony.freenode.net 375 mohadib_ :- anthony.freenode.net Message of the
    // Day -
    // :anthony.freenode.net 372 mohadib_ :- Welcome to anthony.freenode.net in
    // Irvine, CA, USA! Thanks to
    // :anthony.freenode.net 376 mohadib_ :End of /MOTD command.
    static MotdEvent motd(String data, Session session)
    {
        Pattern p = Pattern.compile(":(\\S+)\\s+\\d+\\s+(\\S+)\\s+:(.*)$");
        Matcher m = p.matcher(data);

        if (!m.matches())
        {
            debug("MOTD", data);
            return null;
        }

        return new MotdEventImpl(data, session, m.group(3), m.group(1));
    }

    static NoticeEvent notice(String data, Session session)
    {

        // generic notice NOTICE AUTH :*** No identd (auth) response
        Pattern p = Pattern.compile("^NOTICE\\s+(.*$)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            NoticeEvent noticeEvent = new NoticeEventImpl(data, session, "generic", m.group(1), "", "", null);

            return noticeEvent;
        }

        // channel notice :DIBLET!n=fran@c-68-35-11-181.hsd1.nm.comcast.net NOTICE
        // #jerklib :test
        p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(.*?)\\s+:(.*)$");
        m = p.matcher(data);
        if (m.matches())
        {
            return new NoticeEventImpl
                    (
                            data,
                            session,
                            "channel",
                            m.group(3),
                            "",
                            m.group(1),
                            session.getChannel(m.group(2).toLowerCase())
                    );

        }

        // user notice :NickServ!NickServ@services. NOTICE mohadib_ :This nickname
        // is owned by someone else
        p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(.*?)\\s+:(.*)$");
        m = p.matcher(data);
        if (m.matches())
        {
            return new NoticeEventImpl
                    (
                            data,
                            session,
                            "user",
                            m.group(3),
                            m.group(2),
                            m.group(1),
                            null
                    );
        }

        // user notice but from the server - user notice means to a user as opposed
        // a channel
        // :anthony.freenode.net NOTICE mohadib_ :NickServ set your hostname to
        // "unaffiliated/mohadib"
        p = Pattern.compile("^:(\\S+)\\s+NOTICE\\s+(\\S+)\\s+:(.*)$");
        m = p.matcher(data);
        if (m.matches())
        {
            return new NoticeEventImpl
                    (
                            data,
                            session,
                            "user",
                            m.group(3),
                            m.group(2),
                            m.group(1),
                            null
                    );
        }
        debug("NOTICE", data);
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
    static QuitEvent quit(String data, Session session)
    {
        Pattern pattern = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+QUIT\\s+:(.*)$");
        Matcher matcher = pattern.matcher(data);
        if (matcher.matches())
        {
            List<Channel> chanList = session.removeNickFromAllChannels(matcher.group(1));
            return new QuitEventImpl
                    (
                            data,
                            session,
                            matcher.group(1), // who
                            matcher.group(2), // username
                            matcher.group(3), // hostName
                            matcher.group(4), // message
                            chanList
                    );
        }
        debug("QUIT", data);
        return null;
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
                return new JoinEventImpl
                        (
                                data,
                                session,
                                m.group(1), // nick
                                m.group(2), // user name
                                m.group(3).toLowerCase(), // host
                                session.getChannel(m.group(4).toLowerCase()).getName(), // channel name
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
    static ChannelListEvent chanList(String data, Session session)
    {
        if (log.isLoggable(Level.FINE))
        {
            log.fine(data);
        }
        Pattern p = Pattern.compile("^:\\S+\\s322\\s\\S+\\s(\\S+)\\s(\\d+)\\s:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new ChannelListEventImpl
                    (
                            data,
                            m.group(1),
                            m.group(3),
                            Integer.parseInt(m.group(2)),
                            session
                    );
        }
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
    static PartEvent part(String data, Session session)
    {
        Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+PART\\s+:?(\\S+?)(?:\\s+:(.*))?$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            if (log.isLoggable(Level.FINE))
            {
                log.fine("HERE? " + m.group(4));
                log.fine(data);
            }
            return new PartEventImpl
                    (
                            data,
                            session,
                            m.group(1), // who
                            m.group(2), // username
                            m.group(3), // host name
                            session.getChannel(m.group(4).toLowerCase()).getName(), // channel name
                            session.getChannel(m.group(4).toLowerCase()), m.group(5) // part message
                    );
        }
        else
        {
            log.severe("NO MATCH");
        }
        debug("PART", data);
        return null;
    }

    // :raving!n=raving@74.195.43.119 NICK :Sir_Fawnpug
    static NickChangeEvent nickChange(String data, Session session)
    {
        Pattern p = Pattern.compile("^:(\\S+)!(\\S+)@(\\S+)\\s+NICK\\s+:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new NickChangeEventImpl
                    (
                            data,
                            session,
                            m.group(1), // old
                            m.group(4), // new nick
                            m.group(3), // hostname
                            m.group(2) // username
                    );
        }
        debug("NICK_CHANGE", data);
        return null;
    }

    // :r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2
    static InviteEvent invite(String data, Session session)
    {
        Pattern p = Pattern.compile("^:(\\S+?)!(\\S+?)@(\\S+)\\s+INVITE.+?:(.*)$");
        Matcher m = p.matcher(data);
        if (m.matches())
        {
            return new InviteEventImpl
                    (
                            m.group(4).toLowerCase(),
                            m.group(1),
                            m.group(2),
                            m.group(3),
                            data,
                            session
                    );
        }
        debug("INVITE", data);
        return null;
    }

    private static void debug(String method, String data)
    {
        if (!ConnectionManager.debug)
        {
            return;
        }
        log.info("Returning null from " + method + " in IRCEventFactory. Offending data:");
        log.info(data);
    }

    static Logger log = Logger.getLogger(IRCEventFactory.class.getName());
}
