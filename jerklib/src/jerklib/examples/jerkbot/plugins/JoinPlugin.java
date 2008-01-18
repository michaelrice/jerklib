package jerklib.examples.jerkbot.plugins;

import jerklib.events.ChannelMsgEvent;
import jerklib.examples.jerkbot.plugin.types.ChannelPlugin;

public class JoinPlugin extends ChannelPlugin{
	@Override
	public void doEvent(ChannelMsgEvent event) {
		event.getSession().joinChannel(getArgs(event)[0]);
	}

	@Override
	public boolean willHandle(ChannelMsgEvent event) {
		if (event.getMessage().startsWith(getPrefix() + "join ")) {
			return true;
		}
		return false;
	}

	@Override
	public String getTitle() {
		return "join";
	}

	@Override
	public String getHelpMsg() {
		return "This plugin lets the bot join other channels";
	}

	@Override
	public String getAuthor() {
		return "staykov";
	}

	@Override
	public String getUsage() {
		return "join <channel>";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public String getCommand() {
		return "join";
	}

	@Override
	public String[] getArgs(ChannelMsgEvent event) {
		String msg = event.getMessage();
		String channel = msg.substring(getPrefix().length()
				+ getCommand().length() + 1);
		return new String[] { channel };
	}
}
