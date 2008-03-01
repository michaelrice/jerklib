package jerklib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//http://www.watersprings.org/pub/id/draft-hardy-irc-isupport-00.txt
public class ServerInformation 
{

	private String caseMapping="",ircd="",serverName="";
	private String[] channelPrefixes,statusPrefixes,channelModes;
	private int maxChanNameLen,maxModesPerCommand,maxNickLen,maxSilenceListSize,maxTopicLen,maxAwayLen,maxKickLen,maxKeyLen,maxHostLen;
	private boolean supportsCNotice,supportsCPrivMsg,supportsBanExceptions,supportsInviteExceptions;
	private boolean supportsSafeList,supportsStatusNotice,supportsCAPAB,supportsNickPrefixes,supportsSilenceList;
	private boolean supportsKnock,supportsWhox,supportsWallchops,supportsWallVoices,supportsUserIP,supportsEtrace;
	private Map<String, Integer> joinLimits = new HashMap<String, Integer>();
	private Map<String , String> nickPrefixMap = new HashMap<String, String>();
	private Map<String , ModeType> modeMap = new HashMap<String, ModeType>();
	
	
	
		/*

	o  Type A: Modes that add or remove an address to or from a list.
      These modes MUST always have a parameter when sent from the server
      to a client.  A client MAY issue the mode without an argument to
      obtain the current contents of the list.

   o  Type B: Modes that change a setting on a channel.  These modes
      MUST always have a parameter.

   o  Type C: Modes that change a setting on a channel.  These modes
      MUST have a parameter when being set, and MUST NOT have a
      parameter when being unset.

   o  Type D: Modes that change a setting on a channel.  These modes
      MUST NOT have a parameter.

	 */
	
	public static enum ModeType
	{
		GROUP_A,
		GROUP_B,
		GROUP_C,
		GROUP_D,
		CUSTOM,
		ALL
	}
	
	/*
	 * :swiftco.wa.us.dal.net 005 r0bby_ NETWORK=DALnet SAFELIST MAXBANS=200 MAXCHANNELS=20 CHANNELLEN=32 KICKLEN=307 NICKLEN=30 TOPICLEN=307 MODES=6 CHANTYPES=# CHANLIMIT=#:20 PREFIX=(ov)@+ STATUSMSG=@+ :are available on this server
	 * :swiftco.wa.us.dal.net 005 r0bby_ CASEMAPPING=ascii WATCH=128 SILENCE=10 ELIST=cmntu EXCEPTS INVEX CHANMODES=beI,k,jl,cimMnOprRst MAXLIST=b:200,e:100,I:100 TARGMAX=DCCALLOW:,JOIN:,KICK:4,KILL:20,NOTICE:20,PART:,PRIVMSG:20,WHOIS:,WHOWAS: :are available on this server
	 * :Vancouver.BC.CA.Undernet.org 005 r0bby___ MAXNICKLEN=15 TOPICLEN=160 AWAYLEN=160 KICKLEN=160 CHANNELLEN=200 MAXCHANNELLEN=200 CHANTYPES=#& PREFIX=(ov)@+ STATUSMSG=@+ CHANMODES=b,k,l,imnpstrDd CASEMAPPING=rfc1459 NETWORK=UnderNet :are supported by this server
	 * :Vancouver.BC.CA.Undernet.org 005 r0bby___ WHOX WALLCHOPS WALLVOICES USERIP CPRIVMSG CNOTICE SILENCE=15 MODES=6 MAXCHANNELS=10 MAXBANS=45 NICKLEN=12 :are supported by this server
	 * :kubrick.freenode.net 005 BigDaddy IRCD=dancer CAPAB CHANTYPES=# EXCEPTS INVEX CHANMODES=bdeIq,k,lfJD,cgijLmnPQrRstz CHANLIMIT=#:20 PREFIX=(ov)@+ MAXLIST=bdeI:50 MODES=4 STATUSMSG=@ KNOCK NICKLEN=16 :are supported by this server
	 * :kubrick.freenode.net 005 BigDaddy SAFELIST CASEMAPPING=ascii CHANNELLEN=30 TOPICLEN=450 KICKLEN=450 KEYLEN=23 USERLEN=10 HOSTLEN=63 SILENCE=50 :are supported by this server
	 */
	
	void parseServerInfo(String rawData)
	{
		String[] tokens = rawData.split("\\s+");
		serverName = tokens[0].substring(1);
		
		for(int i = 3 ; i < tokens.length; i++)
		{
			String[] subTokens = tokens[i].split("=");
			if(subTokens[0].equals("IRCD")) ircd = subTokens[1];
			else if(subTokens[0].equals("CAPAB")) supportsCAPAB = true;
			else if(subTokens[0].equals("CHANTYPES") && subTokens.length == 2)
			{
				String[] data = subTokens[1].split("");
				channelPrefixes = new String[data.length - 1];
				System.arraycopy(data, 1, channelPrefixes, 0, data.length - 1);
			}
			else if(subTokens[0].equals("EXCEPTS"))supportsBanExceptions = true;
			else if(subTokens[0].equals("INVEX")) supportsInviteExceptions = true;
			else if(subTokens[0].equals("SAFELIST")) supportsSafeList = true;
			else if(subTokens[0].equals("CASEMAPPING")) caseMapping = subTokens[1];
			else if(subTokens[0].equals("CHANNELLEN")) maxChanNameLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("MAXCHANNELLEN")) maxChanNameLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("TOPICLEN")) maxTopicLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("AWAYLEN")) maxAwayLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("NICKLEN"))maxNickLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("MAXNICKLEN"))maxNickLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("KICKLEN"))maxKickLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("KEYLEN"))maxKeyLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("HOSTLEN"))maxHostLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("CNOTICE")) supportsCNotice = true;
			else if(subTokens[0].equals("CPRIVMSG")) supportsCPrivMsg = true;
			else if(subTokens[0].equals("KNOCK")) supportsKnock = true;
			else if(subTokens[0].equals("WHOX")) supportsWhox = true;
			else if(subTokens[0].equals("WALLCHOPS")) supportsWallchops = true;
			else if(subTokens[0].equals("WALLVOICES")) supportsWallVoices = true;
			else if(subTokens[0].equals("USERIP")) supportsUserIP = true;
			else if(subTokens[0].equals("ETRACE")) supportsEtrace = true;
			else if(subTokens[0].equals("SILENCE"))
			{
				if(subTokens.length == 2)
				{
					supportsSilenceList = true;
					maxSilenceListSize = Integer.parseInt(subTokens[1]);
				}
				else
				{
					supportsSilenceList = false;
				}
			}
			else if(subTokens[0].equals("CHANLIMIT"))
			{
				String[] keyVals = subTokens[1].split(",");
				for(String keyVal : keyVals)
				{
					String[] limits = keyVal.split(":");
					String[] chanPrefixes = limits[0].split("");
					int limit = -1;
					if(limits.length == 2) limit = Integer.parseInt(limits[1]);
					for(String chanPrefix : chanPrefixes)
					{
						if(chanPrefix.matches(""))continue;
						joinLimits.put(chanPrefix, limit);
					}
				}
			}
			else if(subTokens[0].equals("PREFIX"))
			{
				if(subTokens.length == 2)
				{
					supportsNickPrefixes = true;
					String[] modesAndPrefixes = subTokens[1].split("\\)");
					modesAndPrefixes[0] = modesAndPrefixes[0].substring(1);
					String[] modes = modesAndPrefixes[0].split("");
					String[] prefixes = modesAndPrefixes[1].split("");
					for(int x = 0 ; x < prefixes.length ; x++)
					{
						if(prefixes[x].matches(""))continue;
						nickPrefixMap.put(prefixes[x], modes[x]);
						modeMap.put(modes[x], ModeType.GROUP_B);
					}
				}
				else
				{
					supportsNickPrefixes = false;
				}
			}
			else if(subTokens[0].equals("MODES"))
			{
				if(subTokens.length == 2)
				{
					maxModesPerCommand = Integer.parseInt(subTokens[1]);
				}
			}
			else if(subTokens[0].equals("STATUSMSG"))
			{
				supportsStatusNotice = true;
				String[] tmp = subTokens[1].split("");
				statusPrefixes = new String[tmp.length -1];
				System.arraycopy(tmp, 1, statusPrefixes, 0, tmp.length -1);
			}
		
			else if(subTokens[0].equals("CHANMODES"))
			{
				String[] modeGroups = subTokens[1].split(",");
				for(int x=0 ; x<modeGroups.length ; x++)
				{
					ModeType mt = ModeType.CUSTOM;
					switch(x)
					{
						case 0: mt = ModeType.GROUP_A;break;
						case 1: mt = ModeType.GROUP_B;break;
						case 2: mt = ModeType.GROUP_C;break;
						case 3: mt = ModeType.GROUP_D;break;
					}
					
					String[] modes = modeGroups[x].split("");
					for(String mode : modes)
					{
						if(mode.equals(""))continue;
						modeMap.put(mode, mt);
					}
				}
				channelModes = modeMap.keySet().toArray(new String[modeMap.size()]);
			}
			else
			{
				System.err.println("Unreconized Server Info Token:" + subTokens[0]);
			}
		}
	}

	
	public String[] getModes(ModeType type)
	{
		List<String> modesList = new ArrayList<String>();
		for(String key : modeMap.keySet())
		{
			if(modeMap.get(key) == type || type == ModeType.ALL)
			{
				modesList.add(key);
			}
		}
		return modesList.toArray(new String[modesList.size()]);
	}
	
	
	public ModeType getTypeForMode(String mode)
	{
		return modeMap.get(mode);
	}

	
	public String getServerName()
	{
		return serverName;
	}
	
	public String getIrcdString()
	{
		return ircd;
	}
	
	/* would generally return ascii, rfc1459, or strict-rfc1459 */
	public String getCaseMapping()
	{
		return caseMapping;
	}
	
	/* used to indicate the maximum amount of channels that a client may join
	 * of a given prefix. A commen example prefix would be #
	 */
	public int getChannelJoinLimitForPrefix(String prefix)
	{
		return joinLimits.get(prefix);
	}
	
	public String[] getSupportedChannelModes()
	{
		return channelModes;
	}
	
	
	public boolean supportsCAPAB()
	{
		return supportsCAPAB;
	}
	
	public boolean supportsCNotice()
	{
		return supportsCNotice;
	}
	
	public boolean supportsCPrivMsg()
	{
		return supportsCPrivMsg;
	}
	
	public boolean supportsWhox()
	{
		return supportsWhox;
	}
	
	public boolean supportsWallChops()
	{
		return supportsWallchops;
	}
	
	public boolean supportsWallVoices()
	{
		return supportsWallVoices;
	}
	
	public boolean supportsBanExceptions()
	{
		return supportsBanExceptions;
	}
	
	public boolean supportsInviteExceptions()
	{
		return supportsInviteExceptions;
	}
	
	public boolean supportsKnock()
	{
		return supportsKnock;
	}
	
	public boolean supportsUserIp()
	{
		return supportsUserIP;
	}
	
	public boolean supportsEtrace()
	{
		return supportsEtrace;
	}
	
	public boolean supportsSafeList()
	{
		return supportsSafeList;
	}
	
	public boolean supportsSilenceList()
	{
		return supportsSilenceList;
	}
	
	public boolean supportsNickPrefxies()
	{
		return supportsNickPrefixes;
	}
	
	public boolean supportsStatusNotices()
	{
		return supportsStatusNotice;
	}
	
	
	public int getMaxModesPerCommnad()
	{
		return maxModesPerCommand;
	}
	
	public int getMaxAwayLength()
	{
		return maxAwayLen;
	}
	
	public int getMaxKickLength()
	{
		return maxKickLen;
	}
	
	public int getMaxNickLength()
	{
		return maxNickLen;
	}
	
	public int getMaxSilenceListSize()
	{
		return maxSilenceListSize;
	}
	
	public int getMaxTopicLength()
	{
		return maxTopicLen;
	}
	
	public int getMaxChannelNameLength()
	{
		return maxChanNameLen;
	}
	
	public int getMaxKeyLength()
	{
		return maxKeyLen;
	}
	
	public int getMaxHostLength()
	{
		return maxHostLen;
	}
	
	public List<String> getNickPrefixes()
	{
		return new ArrayList<String>(nickPrefixMap.values());
	}
	
	public String[] getStatusPrefixes()
	{
		return statusPrefixes;
	}
	
	public String[] getChannelPrefixes()
	{
		return channelPrefixes;
	}

	
}
