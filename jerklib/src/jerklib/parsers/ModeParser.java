package jerklib.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jerklib.ServerInformation;
import jerklib.ServerInformation.ModeType;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ModeEventImpl;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

/**
 * @author mohadib
 *
 *Class currently handles Server level modes for use of the lib
 *and channel level modes for any user in a given channel.
 *
 */
public class ModeParser implements CommandParser
{
	
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> wordTokens = token.getWordTokens(); 
		char[] modeTokens = wordTokens.get(3).data.replaceFirst(":" , "").toCharArray();
		String[] arguments = token.concatTokens(8).split("\\s+");

		Map<String, List<String>> modeMap = new HashMap<String, List<String>>();
		ServerInformation info = event.getSession().getServerInformation();
		
		/* see if user mode */
		boolean channelMode = event.getSession().isChannelToken(wordTokens.get(2));

		char action = '+';
		
		/* this is a server wide mode adjustment for the user of the lib */
		if (!channelMode)
		{
			List<String> targets = new ArrayList<String>();
			targets.add(event.getSession().getNick());
			for (char mode : modeTokens)
			{
				if (mode == '+' || mode == '-')
				{
					action = mode;
				}
				else
				{
					modeMap.put(action + "" +  mode, targets);
				}
			}

			//remove : and . -> :services.
			String who = wordTokens.get(0).data.replaceFirst(":" , "");
			if(who.endsWith("."))who = who.substring(0 , who.length() - 1);
			
			return new ModeEventImpl
			(
				token.getData(), 
				event.getSession(), 
				modeMap, 
				who,
				null
			);

		}

		/* this is channel level mode adjustment for a user */
		/* Actual adjustments of a channel's modes are not handled here ... for now
		 */
		int argumntOffset = 0;
		
		for (char mode : modeTokens)
		{
			if (mode == '+' || mode == '-')
			{
				action = mode;
			}	
			else
			{
				ModeType type = info.getTypeForMode(String.valueOf(mode));
				// must have an argument on + and -
				if (type == ModeType.GROUP_A || type == ModeType.GROUP_B)
				{
					List<String> modeArgs = modeMap.get(action +""+ mode);
					if (modeArgs == null)modeArgs = new ArrayList<String>();
					modeArgs.add(arguments[argumntOffset]);
					argumntOffset++;
					modeMap.put(action + "" + mode, modeArgs);
				}
				// must have args on + , must not have args on -
				else if (type == ModeType.GROUP_C)
				{
					List<String> modeArgs = modeMap.get(action +""+ mode);
					if (modeArgs == null)modeArgs = new ArrayList<String>();
					if (action == '-')
					{
						if (!modeMap.containsKey(action +""+ mode))
						{
							modeMap.put(action + "" + mode, new ArrayList<String>());
						}
					}
					else
					{
						modeArgs.add(arguments[argumntOffset]);
						argumntOffset++;
						modeMap.put(action +""+ mode, modeArgs);
					}
				}
				// no args
				else if (type == ModeType.GROUP_D)
				{
					modeMap.put(action +""+ mode, new ArrayList<String>());
				}
				else
				{
					System.err.println("unreconzied mode " + mode);
				}
			}
		}
		
		
		return new ModeEventImpl
		(
			event.getRawEventData(), 
			event.getSession(), 
			modeMap, 
			TokenUtil.getNick(wordTokens.get(0)), 
			event.getSession().getChannel(wordTokens.get(2).data)
		);

	}
}
