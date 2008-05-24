package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.IRCEvent;

/**
 * Event fired when an Invite message is recieved from server
 * 
 * @author <a href="mailto:rob@mybawx.org">Robert O'Connor</a>
 */
public class InviteEven extends IRCEvent
{
	private final String channelName;

	public InviteEven(String channelName, String rawEventData, Session session)
	{
		super(rawEventData, session, Type.INVITE_EVENT);
		this.channelName = channelName;
	}

	/**
	 * Gets the channel to which we were invited to
	 * 
	 * @return the channel we were invited to.
	 */
	public String getChannelName()
	{
		return channelName;
	}

}
