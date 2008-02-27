package jerklib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//http://www.watersprings.org/pub/id/draft-hardy-irc-isupport-00.txt
public class ServerInformation 
{

	private String caseMapping="",ircd="",serverName="";
	private String[] channelPrefixes,statusPrefixes;
	private int maxChanNameLen,maxModesPerCommand,maxNickLen,maxSilenceListSize,maxTopicLen;
    private int maxKickLength,maxKeyLength,maxUserLength,maxHostLength;
    private boolean supportsCNotice,supportsCPrivMsg,supportsBanExceptions,supportsInviteExceptions;
	private boolean supportsSafeList,supportsStatusNotice,supportsCAPAB,supportsNickPrefixes,supportsSilenceList;
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
			else if(subTokens[0].equals("TOPICLEN")) maxTopicLen = Integer.parseInt(subTokens[1]);
			else if(subTokens[0].equals("CNOTICE")) supportsCNotice = true;
			else if(subTokens[0].equals("CPRIVMSG")) supportsCPrivMsg = true;
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
			else if(subTokens[0].equals("NICKLEN"))
			{
				maxNickLen = Integer.parseInt(subTokens[1]);
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
	
	
	public boolean supportsCAPAB()
	{
		return supportsCAPAB;
	}
	
	public String getServerName()
	{
		return serverName;
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
		return new String[]{};
	}
	
	/* max length of a channel name */
	public int getMaxChannelNameLength()
	{
		return maxChanNameLen;
	}
	
	/* the prefixes a channel name can start with */
	public String[] getChannelPrefixes()
	{
		return channelPrefixes;
	}
	
	public boolean supportsCNotice()
	{
		return supportsCNotice;
	}
	
	public boolean supportsCPrivMsg()
	{
		return supportsCPrivMsg;
	}
	
	
	public boolean supportsBanExceptions()
	{
		return supportsBanExceptions;
	}
	
	public boolean supportsInviteExceptions()
	{
		return supportsInviteExceptions;
	}
	
	
	public int getMaxModesPerCommnad()
	{
		return maxModesPerCommand;
	}
	
	
	public int getMaxNickLength()
	{
		return maxNickLen;
	}
	
	public boolean supportsSafeList()
	{
		return supportsSafeList;
	}
	
	public boolean supportsSilenceList()
	{
		return supportsSilenceList;
	}
	
	public int getMaxSilenceListSize()
	{
		return maxSilenceListSize;
	}
	
	public String getIrcdString()
	{
		return ircd;
	}
	
	public Collection<String> getNickPrefixes()
	{
		return nickPrefixMap.values();
	}
	
	public boolean supportsNickPrefxies()
	{
		return supportsNickPrefixes;
	}
	
	public boolean supportsStatusNotices()
	{
		return supportsStatusNotice;
	}
	
	public String[] getStatusPrefixes()
	{
		return statusPrefixes;
	}
	
	public int getMaxTopicLength()
	{
		return maxTopicLen;
	}

    public int getMaxHostLength() {
        return maxHostLength;
    }

    public int getMaxKeyLength() {
        return maxKeyLength;
    }

    public int getMaxKickLength() {
        return maxKickLength;
    }

    public int getMaxUserLength() {
        return maxUserLength;
    }
}
