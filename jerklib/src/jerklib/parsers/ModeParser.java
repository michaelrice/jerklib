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
 */
public class ModeParser implements CommandParser
{

	private IRCEvent numericModeEvent(EventToken token, IRCEvent event)
	{
		return event;
	}

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
				modeAdjustments.add(new ModeAdjustment(action == '+' ? Action.PLUS : Action.MINUS, mode, arguments[argumntOffset]));
				argumntOffset++;
			}
		}

		// remove : and . -> :services.
		String who = wordTokens.get(0).data.replaceFirst(":", "");
		if (who.endsWith(".")) who = who.substring(0, who.length() - 1);

		return new ModeEventImpl(ModeEvent.ModeType.USER, token.getData(), event.getSession(), modeAdjustments, who, null);
	}

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
