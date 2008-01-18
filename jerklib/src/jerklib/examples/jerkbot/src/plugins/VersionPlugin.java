package plugins;

import jerkbot.plugin.Plugin;
import jerkbot.plugin.types.ChannelPlugin;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.PrivateMsgEvent;

public class VersionPlugin extends ChannelPlugin {

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
		return "marin";
	}

	@Override
	public String getCommand() {
		return "version";
	}

	@Override
	public String getHelpMsg() {
		return "Prints the help ver";
	}

	@Override
	public String getTitle() {
		return "version";
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
