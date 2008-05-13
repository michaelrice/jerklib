package jerklib.parsers;

import java.util.ArrayList;
import java.util.List;

import jerklib.ServerInformation;
import jerklib.ServerInformation.ModeType;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ModeEventImpl;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;
import jerklib.events.modes.ModeAdjustment.Action;
import jerklib.tokens.EventToken;
import jerklib.tokens.Token;
import jerklib.tokens.TokenUtil;

/**
 * @author mohadib
 * 
 * Class currently handles Server level modes for use of the lib and channel
 * level modes for any user in a given channel.
 * 
 * developers see:
 * https://sourceforge.net/tracker/index.php?func=detail&aid=1962621&group_id=214803&atid=1031130
 * 
 */
public class ModeParser implements CommandParser
{
	// :kubrick.freenode.net 324 mohadib__ #test +mnPzlfJ 101 #flood 1,2
	private IRCEvent numericModeEvent(EventToken token, IRCEvent event)
	{
		List<Token> wordTokens = token.getWordTokens();
		char[] modeTokens = wordTokens.get(4).data.replaceFirst(":", "").toCharArray();
		String[] arguments = token.concatTokens(10).split("\\s+");
		ServerInformation info = event.getSession().getServerInformation();
		List<ModeAdjustment> modeAdjustments = new ArrayList<ModeAdjustment>();
		char action = '+';
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
					modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, arguments[argumntOffset]));
					argumntOffset++;
				}
				// must have args on + , must not have args on -
				else if (type == ModeType.GROUP_C)
				{
					if (action == '-')
					{
						modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, ""));
					}
					else
					{
						modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, arguments[argumntOffset]));
						argumntOffset++;
					}
				}
				// no args
				else if (type == ModeType.GROUP_D)
				{
					modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, ""));
				}
				else
				{
					System.err.println("unreconzied mode " + mode);
				}
			}
		}
		
		return new ModeEventImpl
		(
			ModeEvent.ModeType.CHANNEL, 
			event.getRawEventData(), 
			event.getSession(), 
			modeAdjustments, 
			"server",
			event.getSession().getChannel(wordTokens.get(3).data)
		);
	}

	// :services. MODE mohadib :+e
	private IRCEvent userModeEvent(EventToken token, IRCEvent event)
	{
		List<ModeAdjustment> modeAdjustments = new ArrayList<ModeAdjustment>();
		char action = '+';
		int argumntOffset = 0;
		List<Token> wordTokens = token.getWordTokens();
		char[] modeTokens = wordTokens.get(3).data.replaceFirst(":", "").toCharArray();
		String[] arguments = token.concatTokens(8).split("\\s+");
		for (char mode : modeTokens)
		{
			if (mode == '+' || mode == '-') action = mode;
			else
			{
				String argument = argumntOffset >= arguments.length? "":arguments[argumntOffset];
				modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, argument));
				argumntOffset++;
			}
		}

		return new ModeEventImpl(ModeEvent.ModeType.USER, token.getData(), event.getSession(), modeAdjustments , event.getSession().getConnectedHostName(), null);
	}

	
	// :mohadib_!n=mohadib@unaffiliated/mohadib MODE #jerklib +o scripyasas
	public IRCEvent createEvent(EventToken token, IRCEvent event)
	{
		List<Token> wordTokens = token.getWordTokens();
		if (token.getCommand().equals("324")) return numericModeEvent(token, event);
		else if (!event.getSession().isChannelToken(wordTokens.get(2)))
		{
			return userModeEvent(token, event);
		}
		else
		{
			char[] modeTokens = wordTokens.get(3).data.replaceFirst(":", "").toCharArray();
			String[] arguments = token.concatTokens(8).split("\\s+");
			ServerInformation info = event.getSession().getServerInformation();
			List<ModeAdjustment> modeAdjustments = new ArrayList<ModeAdjustment>();
			char action = '+';
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
						modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, arguments[argumntOffset]));
						argumntOffset++;
					}
					// must have args on + , must not have args on -
					else if (type == ModeType.GROUP_C)
					{
						if (action == '-')
						{
							modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, ""));
						}
						else
						{
							modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, arguments[argumntOffset]));
							argumntOffset++;
						}
					}
					// no args
					else if (type == ModeType.GROUP_D)
					{
						modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, ""));
					}
					else
					{
						System.err.println("unreconzied mode " + mode);
					}
				}
			}

			return new ModeEventImpl
			(
				ModeEvent.ModeType.CHANNEL, 
				event.getRawEventData(), 
				event.getSession(), 
				modeAdjustments, 
				TokenUtil.getNick(wordTokens.get(0)),
				event.getSession().getChannel(wordTokens.get(2).data)
			);
		}
	}
}
