package jerklib;

import java.util.List;
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
import jerklib.events.PartEvent;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.PrivateMsgEvent;
import jerklib.events.QuitEvent;
import jerklib.events.TopicEvent;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.impl.JoinCompleteEventImpl;
import jerklib.events.impl.JoinEventImpl;
import jerklib.events.impl.KickEventImpl;
import jerklib.events.impl.MotdEventImpl;
import jerklib.events.impl.ModeEventImpl;
import jerklib.events.impl.NickChangeEventImpl;
import jerklib.events.impl.NickInUseEventImpl;
import jerklib.events.impl.NickListEventImpl;
import jerklib.events.impl.NoticeEventImpl;
import jerklib.events.impl.PartEventImpl;
import jerklib.events.impl.ChannelMsgEventImpl;
import jerklib.events.impl.PrivateMessageEventImpl;
import jerklib.events.impl.QuitEventImpl;
import jerklib.events.impl.ReadyToJoinEventImpl;
import jerklib.events.impl.TopicEventImpl;
import jerklib.events.impl.ConnectionCompleteEventImpl;



class IRCEventFactory
{
  
	static private ConnectionManager myManager;
	
	static void setManager(ConnectionManager manager)
	{
		myManager = manager;
	}
	
	static ConnectionCompleteEvent connectionComplete(String data , Connection con)
	{
	  Pattern p = Pattern.compile(":(.*?)\\s+001\\s+\\Q" + con.getProfile().getActualNick() + "\\E\\s+:.*$");
	  Matcher m = p.matcher(data);
	  if (m.matches()) 
	  {
	    /* send host name changed event so users of lib can update *records* */
	    ConnectionCompleteEvent e = new ConnectionCompleteEventImpl
	      ( 
	          data, 
	          m.group(1).toLowerCase().trim(), // new hostname
	          myManager.getSessionFor(con),
	          con.getHostName() // old hostname
	    );

	    return e;
	  }
	  return null;
	}
	
	/* end of names :irc.newcommunity.tummy.com 366 SwingBot #test :End of NAMES list */
	static NickListEvent nickList(String data , Connection con)
	{
		Pattern p = Pattern.compile("^:(?:.*?)\\s+366\\s+\\S+\\s+(.*?)\\s+.*$");
		Matcher m = p.matcher(data);
		
		if(m.matches())
		{
			NickListEvent nle = new NickListEventImpl
			(
				data,
				myManager.getSessionFor(con),
				con.getChannel(m.group(1)),
				con.getChannel(m.group(1)).getNicks()
			);
			return nle;
		}
		
		return null;
	}
	
	/* KICK :mohadib!~mohadib@67.41.102.162 KICK #test scab :bye! */
	static KickEvent kick(String data , Connection con)
	{
		Pattern p = Pattern.compile("^:(.*?)\\!\\S+\\s+KICK\\s+(\\S+)\\s+(\\S+)\\s+:?(.*)");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			KickEvent ke = new KickEventImpl
			(
				data,
				myManager.getSessionFor(con),
				m.group(1),
				m.group(3),
				m.group(4),
				con.getChannel(m.group(2))
			);
			return ke;
		}
		return null;
	}
	
	//sterling.freenode.net 332 scrip #test :Welcome to #test - This channel is for testing only.
	static TopicEvent topic(String data , Connection con)
	{
		Pattern p = Pattern.compile(":(.*?)\\s+332\\s+(.*?)\\s+(#.*?)\\s+:(.*)$");
		Matcher m = p.matcher(data);
		if(m.matches())
		{
			TopicEvent topicEvent = new TopicEventImpl
			(
					data,
					myManager.getSessionFor(con),
					con.getChannel(m.group(3)),
					m.group(4)
			);
			return topicEvent;
		}
		
		return null;
	}
	
	//:mohadib!n=fran@unaffiliated/mohadib MODE #jerklib +b scripy!*@* 
	//:mohadib!n=fran@unaffiliated/mohadib MODE #jerklib -m
	static ModeEvent modeEvent(String data , Connection con)
	{
		Pattern p = Pattern.compile("^:(.*?)\\!\\S+\\s+MODE\\s+(\\S+)\\s+(\\S+)\\s*(\\S*)");
		Matcher m = p.matcher(data);
		if(m.matches())
		{
			ModeEvent me = new ModeEventImpl
			(
				data,
				myManager.getSessionFor(con),
				m.group(3),
				m.group(4),
				m.group(1),
				con.getChannel(m.group(2).toLowerCase().trim())
			);
			return me;
		}
		return null;
	}

	
  static ChannelMsgEvent channelMsg(String data , Connection con)
  {
    Pattern p = Pattern.compile("^:(.*?)\\!(.*?)@(.*?)\\s+PRIVMSG\\s+(\\S+)\\s+:(.*)$");
    Matcher m = p.matcher(data);
    if (m.matches()) 
    {
      ChannelMsgEvent channelMsgEvent = new ChannelMsgEventImpl
        ( 
          data, 
          myManager.getSessionFor(con),
          con.getChannel(m.group(4).trim().toLowerCase()), // channel
          m.group(1).trim(), // nick
          m.group(2).trim(), // login
          m.group(5), // message
          m.group(3) // nicks host
        );
      
      return channelMsgEvent;
    }
    
    return null;
  }
  
  // the_horrible is the nick that is in use
  //fran is the current nickname
  /* :simmons.freenode.net 433 fran the_horrible :Nickname is already in use. */
  
  // * is used for current nick when this event happens on connect
  /* :simmons.freenode.net 433 * fran :Nickname is already in use. */
  static NickInUseEvent nickInUse(String data , Connection con)
  {
	Pattern p = Pattern.compile(".*?\\s433\\s.*?\\s(.*?)\\s:?Nickname is already in use.*$");
	Matcher m = p.matcher(data);
	if(m.matches())
	{
		NickInUseEvent event = new NickInUseEventImpl
		(
				m.group(1),
				data,
				myManager.getSessionFor(con)
		);

		return event;
	}
	return null;
  }
  
  static MotdEvent motd(String data ,Connection con)
  {
    Pattern p = Pattern.compile(":(.*?)\\s+(\\d+)\\s+(\\Q" + con.getProfile().getActualNick() + "\\E)\\s+:(.*)$");
    Matcher m = p.matcher(data);

    
    	if(!m.matches()) return null;
    	
      return new MotdEventImpl
      (
          data, 
          myManager.getSessionFor(con), 
          m.group(4)
      );
  }
  
  
  static NoticeEvent notice(String data , Connection con)
  {
	  
	  
	//generic notice
  	Pattern p = Pattern.compile("NOTICE\\s+(.*$)");
  	Matcher m = p.matcher(data);
  	if (m.matches()) 
  	{
  		NoticeEvent noticeEvent = new NoticeEventImpl
        (
            data, 
            myManager.getSessionFor(con), 
            "generic",
            m.group(1),
            "",
            "",
            null
        );
  			
  		return noticeEvent;
  	}
  	
  	//user notice
  	p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(.*?)\\s+:(.*)$");
  	m = p.matcher(data);
  	if(m.matches())
  	{
  		NoticeEvent ne = new NoticeEventImpl
  		(
  			data,
  			myManager.getSessionFor(con),
  			"user",
  			m.group(3),
  			m.group(2),
  			m.group(1),
  			null
  		);
  		
  		return ne;
  	}
  	
  	//channel notice
  	p = Pattern.compile("^:(.*?)\\!.*?\\s+NOTICE\\s+(#.*?)\\s+:(.*)$");
  	m = p.matcher(data);
  	if(m.matches())
  	{
  		NoticeEvent ne = new NoticeEventImpl
  		(
  			data,
  			myManager.getSessionFor(con),
  			"channel",
  			m.group(3),
  			"",
  			m.group(1),
  			con.getChannel(m.group(2))
  		);
  		
  		return ne;
  	}
  	
  	
  	return null;
  }
  
  
  static ReadyToJoinEventImpl readyToJoin(String data , Connection con)
  {
    return  new ReadyToJoinEventImpl
    ( 
    	data,
      myManager.getSessionFor(con)
    );
  }
  
  
  static QuitEvent quit(String data , Connection con)
  {
    Pattern pattern = Pattern.compile("^:(.*?)\\!.*?\\s+QUIT\\s+:(.*)$");
    Matcher matcher = pattern.matcher(data);
    if (matcher.matches()) {
      List<Channel> chanList = con.removeNickFromAllChannels(matcher.group(1));
      QuitEvent quitEvent = new QuitEventImpl
        (
            data, 
            myManager.getSessionFor(con),
            matcher.group(1).trim(), // who
            matcher.group(2), // msg
            chanList
        );
      return quitEvent;
    }
    return null;
  }
  
  
  static JoinEvent regularJoin(String data , Connection con){
    Pattern p = Pattern.compile("^:(.*?)\\!.*?\\s+JOIN\\s+:?(#.*)$");
    Matcher m = p.matcher(data);
    if (m.matches()) 
    {
      JoinEvent joinEvent = new JoinEventImpl
        (
            data, 
            myManager.getSessionFor(con),
            m.group(1).trim(), // nick
            m.group(2).toLowerCase().trim(), // channel name
            con.getChannel(m.group(2).toLowerCase().trim()) // channel
        );
      return joinEvent;
    }
    return null;
  }
  
  
  static JoinCompleteEvent joinCompleted(String data , Connection con, String nick , Channel channel)
  {
  	return new JoinCompleteEventImpl(data ,myManager.getSessionFor(con), channel);
  }
  
  
  static PartEvent part(String data  , Connection con)
  {
    Pattern p = Pattern.compile("^:(.+?)\\!.*?\\s+PART\\s+(\\S+)\\s*:?(.*)$");
    Matcher m = p.matcher(data);
    if (m.matches()) {
      PartEvent partEvent = new PartEventImpl
        (
          data, 
          myManager.getSessionFor(con),
          m.group(1).trim(), // who
          m.group(2).toLowerCase().trim(), // channel name
          con.getChannel(m.group(2).toLowerCase().trim()),// channel
          m.group(3) // part message
      );
     
      return partEvent;
    }
    return null;
  }
  
  
  static PrivateMsgEvent privateMsg(String data , Connection con , String nick ){
    Pattern p = Pattern.compile("^:(.+?)\\!(.+?)@(.+?)\\sprivmsg\\s\\Q" + nick.toLowerCase() + "\\E\\s+:(.*)$", Pattern.CASE_INSENSITIVE);
    Matcher m = p.matcher(data);

    if (m.matches()) {
      PrivateMsgEvent privateMsg = new PrivateMessageEventImpl
        (
            data, 
            myManager.getSessionFor(con),
            m.group(1).trim(), // nick
            m.group(2).trim(), // login    
            m.group(4), // message
            m.group(3) // nicks host
      );
      
      return privateMsg;
    }
    return null;
  }
  
  
  static NickChangeEvent nickChange(String data , Connection con){
    Pattern p = Pattern.compile("^:(.*?)\\!.*?\\s+NICK\\s+:(.*)$");
    Matcher m = p.matcher(data);
    if (m.matches()) {
      NickChangeEvent nickChangeEvent = new NickChangeEventImpl
        (
            data, 
            myManager.getSessionFor(con),
            m.group(1).trim(), // old nick
            m.group(2).trim() // new nick
      );
      return nickChangeEvent;
    }
    return null;
  }
  

  static ConnectionCompleteEvent updateHostName(String data , Connection con , String oldHostName)
  {
    Pattern p = Pattern.compile(":(.*?)\\s+001\\s+\\Q" + con.getProfile().getActualNick() + "\\E\\s+:.*$");
    Matcher m = p.matcher(data);
    if (m.matches()) {
      ConnectionCompleteEvent e = new ConnectionCompleteEventImpl
        ( 
            data, 
            m.group(1).toLowerCase().trim(), // new hostname
            myManager.getSessionFor(con),
            oldHostName// old hostname
      );
      return e;
    }
    return null;
  }

	
}
