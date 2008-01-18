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


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jerklib.events.ChannelListEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import jerklib.events.TopicEvent;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.impl.ChannelListEventImpl;
import jerklib.events.impl.TopicEventImpl;


/**
 * InternalEventParser is the first place IRCEvents are sent.
 * Here they are parsed and made into more specific events then
 * re-dispachted to any listeners via the ConnectionManager.
 * 
 * This class is mostly convience for users of the lib , however some
 * internal stuff depends on this class as well. Like keeping track
 * of nicks in a channel or knowing when a connection is ready to join channels etc.
 * 
 * @author mohadib
 *
 */
public class InternalEventParser
{

	private ConnectionManager manager;

	private Map<Channel, TopicEvent> topicMap = new HashMap<Channel, TopicEvent>();
//	/private Map<Pair<String, Connection> , WhoisEvent> whoisMap = new HashMap<Pair<String,Connection>, WhoisEvent>();
	private ChannelListEvent tempListEvent;
	
	public InternalEventParser(ConnectionManager manager)
	{
		this.manager = manager;
	}


	/*
	:card.freenode.net 321 r0bby___ Channel :Users  Name
	:card.freenode.net 322 r0bby___ #jerklib 6 :JerkLib - You know you want it
	:card.freenode.net 323 r0bby___ :End of /LIST
	*/
	private void chanList(String data , Connection con)
	{
		if(data.matches("^:\\S+\\s321\\s.+$"))
		{
			tempListEvent = IRCEventFactory.chanList(data, con);
		}
		else if(data.matches("^:\\S+\\s322\\s.*"))
		{
			Pattern p = Pattern.compile("^:\\S+\\s322\\s\\S+\\s(#\\S+)\\s(\\d+)\\s:(.*)$");
			Matcher m = p.matcher(data);
			if(m.matches())
			{
				Channel channel = new ChannelImpl(m.group(1) , con);
				channel.setTopic(m.group(3));
				if(tempListEvent != null)
				{
					ChannelListEventImpl cle = (ChannelListEventImpl)tempListEvent;
					cle.appendToMap(channel, Integer.parseInt(m.group(2)));
					cle.appendToRawEventData(data);
				}
			}
		}
		else
		{
			if(tempListEvent != null)
			{
				ChannelListEventImpl cle = (ChannelListEventImpl)tempListEvent;
				cle.appendToRawEventData(data);
				manager.addToRelayList(cle);
				tempListEvent = null;
			}
		}
	}
	
	
	/**
	 * Takes an IRCEvent and tries to parse it into a more specific event
	 * then redispatch the more specfic event
	 * 
	 * @param event <code>IRCEvent</code> the event to parse
	 * 
	 */
	void parseEvent(IRCEvent event)
	{
		final Connection con = ((InternalSession)event.getSession()).getConnection();
		final String data = event.getRawEventData().trim();
		final String nick = con.getProfile().getActualNick();
		final IRCEvent origEventCopy = event;
		
		/* A Channel Msg
		 * :fuknuit!~admin@212.199.146.104 PRIVMSG #debian :blah blah */
		/* Private message
		 * :mohadib!~mohadib@67.41.102.162 PRIVMSG SwingBot :HY!! */
		if (data.matches("^:.+?\\!.+?\\s+PRIVMSG\\s+\\S+\\s+:.*$"))
		{
			privMsg(data, con, nick);
			return;
		}
		
		//match numerics OMG INTERNETS?!
		else if(data.matches("^:\\S+\\s+\\d{3}.+$"))
		{
			Pattern p = Pattern.compile("^:\\S+\\s+(\\d{3}).+$");
			Matcher m = p.matcher(data);
			if(m.matches())
			{
				switch(Integer.parseInt(m.group(1)))
				{
					case 001: connectionComplete(data, con, event);return;
                    case 321: // beginning of /list
                    case 322: // channel listings use this numeric method is IRCEventFactory.parseChannelList(data,con)
                    case 323: chanList(data, con); return;// end of channel /list
                    case 332: firstPartOfTopic(data, con); return;
					case 333: secondPartOfTopic(data, con); return;
					case 353: namesLine(data, con); return;
					case 366: manager.addToRelayList(IRCEventFactory.nickList(data, con)); return;
					case 372: //motd
					case 375: //motd
					case 376: manager.addToRelayList(IRCEventFactory.motd(data, con));return;
					case 433: nick(data, con, event.getSession());return;
				}
			}
		}
		
	
		
		
		/* PERSON QUIT 
		 * :Xolt!brad@c-67-165-231-230.hsd1.co.comcast.net QUIT :"Deleted" 
		 * :james_so!~me@213-152-46-35.dsl.eclipse.net.uk QUIT :Read error: 60 (Operation timed out) */
		else if (data.matches("^:.+?\\s+QUIT\\s+:.*"))
		{
			QuitEvent qEvent = IRCEventFactory.quit(data,con);
			con.removeNickFromAllChannels(qEvent.getWho());
			event = qEvent;
		}

		/* JOIN COMPLETED  must come before 'SOMEONE JOINS A CHANNEL'
		 *:BILLY42!~BILLY@dhcp64-134-133-42.smh.phx.wayport.net JOIN :#test
		 *:BILLY42!~BILLY@dhcp64-134-133-42.smh.phx.wayport.net JOIN #test
		 *:SwingBot!n=SwingBot@207.114.175.81 JOIN :##swing */
		else if (data.matches("^:\\Q" + nick + "\\E\\!.*?\\s+JOIN\\s+:?#.*$"))
		{
			Pattern p = Pattern.compile("^:\\Q" + nick + "\\E\\!.*?\\s+JOIN\\s+:?(#.*)$");
			Matcher m = p.matcher(data);

			m.matches();

			Channel channel = new ChannelImpl(m.group(1), con);
			
			con.addChannel(channel);
			
			manager.getSessionFor(con).addChannelName(channel.getName());

			event = IRCEventFactory.joinCompleted(data, con, nick, channel);
		}

		
		/* MODE event */
		else if(data.matches("^:.+?\\!\\S+\\s+MODE\\s+.+$"))
		{
			event = IRCEventFactory.modeEvent(data, con);
		}
		
		
		/* SOMEONE JOINS A CHANNEL :Yog!~magnus@hades.27b-6.de JOIN :#perl
		 * :Solaya!~Solaya@cable-134-9.iesy.tv JOIN #bratwurstbude */
		else if (data.matches("^:.+?\\!.+?\\s+JOIN\\s+:?#.*$"))
		{
			JoinEvent jEvent = IRCEventFactory.regularJoin(data, con);

			jEvent.getChannel().addNick(jEvent.getWho());

			event = jEvent;
		}

		/* PART 
		 * :tdegruyl!~tdegruyl@c-24-60-127-140.hsd1.ma.comcast.net PART #perl :
		 * :DrGonzo42069!~raulduke@c-67-171-159-2.hsd1.or.comcast.net PART #debian :"Kopete 0.10 : http://kopete.kde.org"
		 * :mooohadib!~mohadib@63-230-98-87.albq.qwest.net PART #test
		 */
		else if (data.matches("^:.+?\\!.+?\\s+PART\\s+#.*$"))
		{
			PartEvent pEvent = IRCEventFactory.part(data, con);

			pEvent.getChannel().removeNick(pEvent.getWho());

			event = pEvent;
		}
		
		/*Generic  NOTICE from server - not a specific usar ... so it seems*/
		else if (data.matches("^NOTICE.+"))
		{
			event = IRCEventFactory.notice(data, con);
		}
		
		//channel notice - does this actually happen?
		else if (data.matches("^:.+?\\!.+?\\s+NOTICE\\s+#.+?\\s+:.*$"))
		{
			event = IRCEventFactory.notice(data, con);
		}
		
		//user notice
		else if (data.matches("^:.+?\\!.+?\\s+NOTICE\\s+.*?\\s+:.*$"))
		{
			event = IRCEventFactory.notice(data, con);
		}
		
		//topic changed
		//:mohadib!n=fran@unaffiliated/mohadib TOPIC #jerklib :Jerklib 
		else if(data.matches("^:.+?\\!\\S+\\s+TOPIC\\s+#.+?\\s+:.*$"))
		{
			Pattern p = Pattern.compile("^.+?TOPIC\\s+(#.+?)\\s+.*$");
			Matcher m = p.matcher(data);
			m.matches();
			event.getSession().rawSay("TOPIC " + m.group(1) +"\r\n");
			return;
		}
        // :r0bby!n=wakawaka@guifications/user/r0bby INVITE scripy1 :#jerklib2
        else if(data.matches("^:.*?!.*\\sINVITE\\s.*\\s:.*?$")) {
            event = IRCEventFactory.invitedToChan(data,con);
        }

        /* NICK CHANGE :mohadib!~mohadib@65.19.62.93 NICK :slaps */
		else if (data.matches("^:.+?\\!.+?\\s+NICK\\s+:.*"))
		{
			NickChangeEvent nEvent = IRCEventFactory.nickChange(data,con);
			con.nickChanged(nEvent.getOldNick(), nEvent.getNewNick());
			event = nEvent;
			
			if(nEvent.getOldNick().equals(nick))
			{
				event.getSession().updateProfileSuccessfully(true);
			}
		}

		/* KICK :mohadib!~mohadib@67.41.102.162 KICK #test scab :bye! */
		else if (data.matches("^:.+?\\!\\S+\\s+KICK\\s+\\S+\\s+\\S+\\s+:.*$"))
		{
			KickEvent ke = IRCEventFactory.kick(data, con);
			
			if (!ke.getChannel().removeNick(ke.who()))
			{
				System.out.println("COULD NOT REMOVE NICK " + ke.who() + " from channel " + ke.getChannel().getName());
			}
			
			if(ke.who().equals(nick))
			{
				con.removeChannel(ke.getChannel());
				if(manager.getSessionFor(con).isRejoinOnKick())con.join(ke.getChannel().getName());
			}
			event = ke;
		}

		/* PING PONG */
		else if (data.matches("^PING.*"))
		{
			con.pong(event);
			return;
		}
		else if (data.matches(".*PONG.*"))
		{
			con.gotPong();
			return;
		}

		manager.addToRelayList(event == null? origEventCopy:event );
	}

	
	
	
	private void firstPartOfTopic(String data , Connection con)
	{
		//FIRST PART TOF TOPPIC;
		//sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is for testing only.
		if(data.matches(":(.+?)\\s+332\\s+(.+?)\\s+(#.+?)\\s+:(.*)$"))
		{
			TopicEvent tEvent = IRCEventFactory.topic(data, con);
			if(topicMap.containsValue(tEvent.getChannel()))
			{
				((TopicEventImpl)topicMap.get(tEvent.getChannel())).appendToTopic(tEvent.getTopic());
			}
			else
			{
				topicMap.put(tEvent.getChannel(), tEvent);
			}
		}
	}
	
	private void secondPartOfTopic(String data , Connection con)
	{
		//2nd part of topic
		//:zelazny.freenode.net 333 scrip #test LuX 1159267246
		if(data.matches(":(.+?)\\s+333\\s+(.+?)\\s+(#.+?)\\s+(\\S+)\\s+(\\S+)$"))
		{
			Pattern p = Pattern.compile(":(.+?)\\s+333\\s+(.+?)\\s+(#.+?)\\s+(\\S+)\\s+(\\S+)$");
			Matcher m = p.matcher(data);
			m.matches();
			ChannelImpl chan = (ChannelImpl)con.getChannel(m.group(3));
			if(topicMap.containsKey(chan))
			{
				TopicEventImpl tEvent = (TopicEventImpl)topicMap.get(chan);
				topicMap.remove(chan);
				tEvent.setSetBy(m.group(4));
				tEvent.setSetWhen(m.group(5));
				chan.setTopicEvent(tEvent);
				manager.addToRelayList(tEvent);
			}
		}
	}
	
	private void nick(String data , Connection con , Session session)
	{
		/* NICK IN USE */
		 if (data.matches(".+?\\s433\\s.+?\\s.+?\\s:?Nickname is already in use.*$")) 
		 {
			 if(session.isConnected() && session.isProfileUpdating())
			 {
				 session.updateProfileSuccessfully(false);
			 }
			 else
			 {
				 Profile p = session.getRequestedConnection().getProfile();
				 String aNick = p.getActualNick();
				 String newNick = p.getFirstNick() + (Math.random() * 100);
				 if(aNick.equals(p.getFirstNick())){ newNick = p.getSecondNick(); }
				 else if(aNick.equals(p.getSecondNick())){ newNick = p.getThirdNick(); }
				
				 p.setActualNick(newNick);
				 
				 session.changeProfile(p);
				 
				 manager.addToRelayList(IRCEventFactory.nickInUse(data, con));
			 } 
		 }
		
	}
	
	private void connectionComplete(String data, Connection con , IRCEvent event)
	{

		/* CONNECTION COMPLETE 
		 * irc,freenode.net might actually be niveen.freenode.net
		 * :irc.nmglug.org 001 namnar :Welcome to the nmglug.org Internet Relay Chat Network namnar */
		if (data.matches(":.+?\\s+001\\s+.+?\\s+:.*$"))
		{
			manager.addToRelayList(event);
			manager.addToRelayList(IRCEventFactory.readyToJoin(data ,con));
			ConnectionCompleteEvent ccEvent = IRCEventFactory.connectionComplete(data, con);
			con.setHostName(ccEvent.getActualHostName());
			manager.addToRelayList(ccEvent);
		}
	}
	
	private void privMsg(String data , Connection con , String nick)
	{
		if(data.matches("^.+?PRIVMSG\\s+#.+$"))
		{
			manager.addToRelayList(IRCEventFactory.channelMsg(data, con));
		}
		else
		{
			manager.addToRelayList(IRCEventFactory.privateMsg(data, con, nick));
		}
	}
	
	private void namesLine(String data, Connection con)
	{
		Pattern p = Pattern.compile("^:(?:.+?)\\s+353\\s+\\S+\\s+(?:=|@)\\s+(#+\\S+)\\s:(.+)$");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			Channel chan = con.getChannel(m.group(1).toLowerCase().trim());
			String[] names = m.group(2).trim().split("\\s+");

            for(String name : names)
			{
				// remove @ and + from front for operators ?
				if (name != null && name.length() > 0)
				{
					chan.addNick(name.toLowerCase().replace("+", "").replace("@", "").trim());
				}
			}
		}
	}

}