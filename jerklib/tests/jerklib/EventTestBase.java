package jerklib;

import java.util.HashMap;
import java.util.Map;

public class EventTestBase
{
	protected MockConnectionManager conMan;
	protected Session session;
	private String dataFile,outputFile,nick;
	private Map<ServerInfo, String[]>serverInfoMap = new HashMap<ServerInfo, String[]>();
	
	public static enum ServerInfo
	{
		BAHAMUT,
		UNREAL,
		HYPERION,
		SNIRCD,
		ULTIMATE,
		ALL
	}
	
	
	public EventTestBase(String dataFile , String outputFile)
	{
		this(dataFile, outputFile, "scripy");
	}
	
	public EventTestBase(String dataFile , String outputFile , String nick)
	{
		this.dataFile = dataFile;
		this.outputFile = outputFile;
		this.nick = nick;
		initServerInfoMap();
	}
	

	public void addChannel(String name)
	{
		session.addChannel(new Channel(name , session));
	}
	
	public void addServerInfo(ServerInfo type)
	{
		if(type == ServerInfo.ALL)
		{
			for(String[] data : serverInfoMap.values())
			{
				for(String line : data)
				{
					addServerInfo(line);
				}
			}
		}
		else
		{
			for(String line : serverInfoMap.get(type))
			{
				addServerInfo(line);
			}
		}
	}
	
	public void addServerInfo(String data)
	{
		session.getServerInformation().parseServerInfo(data);
	}
	
	public void createSession()
	{
		conMan = new MockConnectionManager();
		session = conMan.requestConnection
		(
			"anthony.freenode.net", 
			6667, 
			new Profile(nick , nick , nick + "_" , nick + "__"), 
			dataFile, 
			outputFile
		);
	}
	
	private void initServerInfoMap()
	{
		serverInfoMap.put
		(
			ServerInfo.BAHAMUT,
			new String[]
			{
				":swiftco.wa.us.dal.net 005 mohadib__ NETWORK=DALnet SAFELIST MAXBANS=200 MAXCHANNELS=20 CHANNELLEN=32 KICKLEN=307 NICKLEN=30 TOPICLEN=307 MODES=6 CHANTYPES=# CHANLIMIT=#:20 PREFIX=(ov)@+ STATUSMSG=@+ :are available on this server",
				":swiftco.wa.us.dal.net 005 mohadib__ CASEMAPPING=ascii WATCH=128 SILENCE=10 ELIST=cmntu EXCEPTS INVEX CHANMODES=beI,k,jl,cimMnOprRst MAXLIST=b:200,e:100,I:100 TARGMAX=DCCALLOW:,JOIN:,KICK:4,KILL:20,NOTICE:20,PART:,PRIVMSG:20,WHOIS:,WHOWAS: :are available on this server"
			}
		);
		
		serverInfoMap.put
		(
			ServerInfo.UNREAL,
			new String[]
			{
				":irc.nixgeeks.com 005 mohadib__ SAFELIST HCN MAXCHANNELS=20 CHANLIMIT=#:20 MAXLIST=b:60,e:60,I:60 NICKLEN=30 CHANNELLEN=32 TOPICLEN=307 KICKLEN=307 AWAYLEN=307 MAXTARGETS=20 WALLCHOPS WATCH=128 :are supported by this server",
				":irc.nixgeeks.com 005 mohadib__ SILENCE=15 MODES=12 CHANTYPES=# PREFIX=(qaohv)~&@%+ CHANMODES=beI,kfL,lj,psmntirRcOAQKVGCuzNSMTG NETWORK=NixGeeks CASEMAPPING=ascii EXTBAN=~,cqnr ELIST=MNUCT STATUSMSG=~&@%+ EXCEPTS INVEX CMDS=KNOCK,MAP,DCCALLOW,USERIP :are supported by this server"
			}
		);
		
		serverInfoMap.put
		(
			ServerInfo.HYPERION,
			new String[]
			{
				":kubrick.freenode.net 005 mohadib IRCD=dancer CAPAB CHANTYPES=# EXCEPTS INVEX CHANMODES=bdeIq,k,lfJD,cgijLmnPQrRstz CHANLIMIT=#:20 PREFIX=(ov)@+ MAXLIST=bdeI:50 MODES=4 STATUSMSG=@ KNOCK NICKLEN=16 :are supported by this server",
				":kubrick.freenode.net 005 mohadib SAFELIST CASEMAPPING=ascii CHANNELLEN=30 TOPICLEN=450 KICKLEN=450 KEYLEN=23 USERLEN=10 HOSTLEN=63 SILENCE=50 :are supported by this server"
      }
		);
		
		serverInfoMap.put
		(
			ServerInfo.SNIRCD,
			new String[]
			{
				":underworld2.no.quakenet.org 005 mohadib__ WHOX WALLCHOPS WALLVOICES USERIP CPRIVMSG CNOTICE SILENCE=15 MODES=6 MAXCHANNELS=20 MAXBANS=45 NICKLEN=15 :are supported by this server",
				":underworld2.no.quakenet.org 005 mohadib__ MAXNICKLEN=15 TOPICLEN=250 AWAYLEN=160 KICKLEN=250 CHANNELLEN=200 MAXCHANNELLEN=200 CHANTYPES=#& PREFIX=(ov)@+ STATUSMSG=@+ CHANMODES=b,k,l,imnpstrDducCNMT CASEMAPPING=rfc1459 NETWORK=QuakeNet :are supported by this server"
			}
		);
	}
	
}
