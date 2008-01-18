package plugins;

import jerkbot.plugin.types.ChannelPlugin;
import jerklib.events.ChannelMsgEvent;

public class PluginInfoPlugin extends ChannelPlugin{

	@Override
	public void doEvent(ChannelMsgEvent event) {
	}

	@Override
	public String[] getArgs(ChannelMsgEvent event) {
		return null;
	}

	@Override
	public boolean willHandle(ChannelMsgEvent event) {
		return false;
	}

	@Override
	public String getAuthor() {
		return null;
	}

	@Override
	public String getCommand() {
		return null;
	}

	@Override
	public String getHelpMsg() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getUsage() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

}
