package plugins;

import jerklib.events.ChannelMsgEvent;
import marinbot.plugin.Plugin;
import marinbot.plugin.types.ChannelMsgPlugin;

public class PartPlugin extends Plugin implements ChannelMsgPlugin {
	@Override
	public String getAuthor() {
		return "staykov";
	}

	@Override
	public String getHelpMsg() {
		return "Allows the bot to part channels";
	}

	@Override
	public String getTitle() {
		return "part";
	}

	@Override
	public String getUsage() {
		return "part <channel> <msg> | channel is optional. if not specified, it will part the current channel";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public void doEvent(ChannelMsgEvent event) {
		if (getArgs(event)[1].trim().length() == 0) {
			event.getSession().partChannel(event.getChannel(),
					getArgs(event)[1]);
		} else
			event.getSession()
					.partChannel(getArgs(event)[0], getArgs(event)[1]);
	}

	@Override
	public boolean willHandle(ChannelMsgEvent event) {
		if (event.getMessage().startsWith(getPrefix() + getCommand())) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommand() {
		return "part";
	}

	@Override
	public String[] getArgs(ChannelMsgEvent event) {
		String[] args = event.getMessage().split(" ");
		return new String[] { args[1], args[2] };
	}

}
