package jerklib.examples.jerkbot.plugin.types;

import jerklib.events.ChannelMsgEvent;
import jerklib.examples.jerkbot.plugin.Plugin;

public abstract class ChannelPlugin extends Plugin {
	public abstract boolean willHandle(ChannelMsgEvent event);
	public abstract void doEvent(ChannelMsgEvent event);
	public abstract String[] getArgs(ChannelMsgEvent event);
}
