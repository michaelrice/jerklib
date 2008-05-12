package jerklib;

import java.util.HashMap;
import java.util.Map;

import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;
import jerklib.parsers.AwayParser;
import jerklib.parsers.ChanListParser;
import jerklib.parsers.CommandParser;
import jerklib.parsers.ConnectionCompleteParser;
import jerklib.parsers.DefaultNumericErrorParser;
import jerklib.parsers.InviteParser;
import jerklib.parsers.JoinParser;
import jerklib.parsers.MotdParser;
import jerklib.parsers.NamesParser;
import jerklib.parsers.NickInUseParser;
import jerklib.parsers.NickParser;
import jerklib.parsers.NoticeParser;
import jerklib.parsers.PartParser;
import jerklib.parsers.PrivMsgParser;
import jerklib.parsers.QuitParser;
import jerklib.parsers.ServerInformationParser;
import jerklib.parsers.ServerVersionParser;
import jerklib.parsers.TopicParser;
import jerklib.parsers.TopicUpdatedParser;
import jerklib.parsers.WhoParser;
import jerklib.parsers.WhoWasParser;
import jerklib.parsers.WhoisParser;

public class DefaultInternalEventParser implements InternalEventParser
{
	private final Map<String , CommandParser> parsers = new HashMap<String, CommandParser>();
	private CommandParser defaultParser;
	private IRCEventListener internalEventHandler;
	
	public DefaultInternalEventParser()
	{
		initDefaultParsers();
	}
	
	public void receiveEvent(IRCEvent e)
	{
		EventToken eventToken = new EventToken(e.getRawEventData());
		CommandParser parser = parsers.get(eventToken.getCommand());
		parser = parser == null? defaultParser : parser;
		IRCEvent event = parser == null?e:parser.createEvent(eventToken, e);
		internalEventHandler.receiveEvent(event);
	}
	
	public void setInternalEventHandler(IRCEventListener listener)
	{
		internalEventHandler = listener;
	}
	
	public IRCEventListener getInternalEventHandler()
	{
		return internalEventHandler;
	}
	
	
	public void addParser(String command , CommandParser parser)
	{
		parsers.put(command, parser);
	}
	
	public boolean removeParser(String command)
	{
		return parsers.remove(command) != null;
	}
	
	public void setDefaultParser(CommandParser parser)
	{
		defaultParser = parser;
	}
	
	private void initDefaultParsers()
	{
		parsers.put("001" , new ConnectionCompleteParser());
		parsers.put("002" , new ServerVersionParser());
		parsers.put("005" , new ServerInformationParser());
		
		CommandParser awayParser = new AwayParser();
		parsers.put("301" , awayParser);
		parsers.put("305" , awayParser);
		parsers.put("306" , awayParser);
		
		parsers.put("314", new WhoWasParser());
		
		WhoisParser whoisParser = new WhoisParser();
		parsers.put("311", whoisParser);
		parsers.put("312", whoisParser);
		parsers.put("317", whoisParser);
		parsers.put("318", whoisParser);
		parsers.put("319", whoisParser);
		parsers.put("320", whoisParser);
		
		ChanListParser chanListParser = new ChanListParser();
		parsers.put("321" , chanListParser);
		parsers.put("322" , chanListParser);
		
		//TODO channel mode parser
		//parsers.put("324" , parser);
		
		TopicParser topicParser = new TopicParser();
		parsers.put("332", topicParser);
		parsers.put("333", topicParser);
		
		parsers.put("351" , new ServerVersionParser());
		parsers.put("352", new WhoParser());
		
		NamesParser namesParser = new NamesParser();
		parsers.put("353" , namesParser);
		parsers.put("366" , namesParser);
		
		MotdParser motdParser = new MotdParser();
		parsers.put("372", motdParser);
		parsers.put("375", motdParser);
		parsers.put("376", motdParser);
		
		parsers.put("433", new NickInUseParser());
		
		parsers.put("PRIVMSG", new PrivMsgParser());
		parsers.put("QUIT" , new QuitParser());
		parsers.put("JOIN" , new JoinParser());
		parsers.put("PART", new PartParser());
		parsers.put("NOTICE", new NoticeParser());
		parsers.put("TOPIC", new TopicUpdatedParser());
		parsers.put("INVITE", new InviteParser());
		parsers.put("NICK", new NickParser());
		
		//TODO mode
		
		//numeric errors
		CommandParser errorParser = new DefaultNumericErrorParser();
		for(int i = 401 ; i < 503 ; i++)
		{
			parsers.put(String.valueOf(i), errorParser);
		}
	}
}
