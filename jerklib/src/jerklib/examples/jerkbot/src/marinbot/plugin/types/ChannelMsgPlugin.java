package marinbot.plugin.types;

import jerklib.events.ChannelMsgEvent;

public interface ChannelMsgPlugin {
	public boolean willHandle(ChannelMsgEvent event);
	public void doEvent(ChannelMsgEvent event);
	public String[] getArgs(ChannelMsgEvent event);
}
