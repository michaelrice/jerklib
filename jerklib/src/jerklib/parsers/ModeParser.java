package jerklib.parsers;

import java.util.ArrayList;
import java.util.List;

import jerklib.EventToken;
import jerklib.ServerInformation;
import jerklib.ServerInformation.ModeType;
import jerklib.events.IRCEvent;
import jerklib.events.impl.ModeEventImpl;
import jerklib.events.modes.ModeAdjustment;
import jerklib.events.modes.ModeEvent;
import jerklib.events.modes.ModeAdjustment.Action;

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
		char[] modeTokens = token.getArguments().get(2).toCharArray();
		String[] arguments = new String[0];
		if(token.getArguments().size() > 3)
		{
			arguments = token.getArguments().subList(3, token.getArguments().size()).toArray(new String[0]);
		}
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
			"",
			event.getSession().getChannel(token.getArguments().get(1))
		);
	}

	// :services. MODE mohadib :+e
	private IRCEvent userModeEvent(EventToken token, IRCEvent event)
	{
		List<ModeAdjustment> modeAdjustments = new ArrayList<ModeAdjustment>();
		char action = '+';
		int argumntOffset = 0;
		char[] modeTokens = token.getArguments().get(1).toCharArray();
		String[] arguments = new String[0];
		
		//if size > 2 then mode adjustment has arguments
		if(token.getArguments().size() > 2)
		{
			arguments = token.getArguments().subList(2, token.getArguments().size()).toArray(new String[0]);
		}
		
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
		if (token.getNumeric() == 324) return numericModeEvent(token, event);
		else if (!event.getSession().isChannelToken(token.getArguments().get(0)))
		{
			return userModeEvent(token, event);
		}
		else
		{
			char[] modeTokens = token.getArguments().get(1).toCharArray();
			String[] arguments = new String[0];
			if(token.getArguments().size() > 2)
			{
				arguments = token.getArguments().subList(2, token.getArguments().size()).toArray(new String[0]);
			}
			
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
				token.getNick(),
				event.getSession().getChannel(token.getArguments().get(0))
			);
		}
	}
}
