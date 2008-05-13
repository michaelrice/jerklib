package jerklib.events.modes;

import jerklib.Channel;
import jerklib.events.IRCEvent;

import java.util.List;

/**
 * @author Mohadib
 *         Event fired when mode changes for us(UserMode) or Channel(ChannelMode)
 *         
 * @see Channel
 */
public interface ModeEvent extends IRCEvent
{
	
		enum ModeType
		{
			USER,
			CHANNEL
		}
	
		
		
		public ModeType getModeType();

    public List<ModeAdjustment> getModeAdjustments();

    /**
     * Gets who set the mode
     *
     * @return who set the mode
     */
    public String setBy();


    /**
     * If mode event adjusted a Channel mode
     * then the Channel effected will be returned
     *
     * @return Channel
     * @see Channel
     */
    public Channel getChannel();
}
