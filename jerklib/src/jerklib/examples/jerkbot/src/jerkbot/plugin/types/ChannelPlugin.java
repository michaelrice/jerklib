package jerkbot.plugin.types;

import jerkbot.plugin.Plugin;
import jerklib.events.ChannelMsgEvent;

public abstract class ChannelPlugin extends Plugin {
	public abstract boolean willHandle(ChannelMsgEvent event);
	public abstract void doEvent(ChannelMsgEvent event);
	public abstract String[] getArgs(ChannelMsgEvent event);
}
