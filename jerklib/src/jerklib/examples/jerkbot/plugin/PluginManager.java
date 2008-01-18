package jerklib.examples.jerkbot.plugin;

import java.util.ArrayList;

import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;
import jerklib.examples.jerkbot.plugin.types.ChannelPlugin;
import jerklib.examples.jerkbot.plugin.types.PMPlugin;

public class PluginManager {
	private ArrayList<PMPlugin> pmPlugins = new ArrayList<PMPlugin>();
	private ArrayList<ChannelPlugin> chanPlugins = new ArrayList<ChannelPlugin>();

	public PluginManager() {

	}

	public void load(Plugin pl) {
		if (pl instanceof PMPlugin) {
			pmPlugins.add((PMPlugin)pl);
		}
		if (pl instanceof ChannelPlugin) {
			chanPlugins.add((ChannelPlugin)pl);
		}
	}

	public void unload(Plugin pl) {
		if (pl instanceof PMPlugin) {
			pmPlugins.remove((PMPlugin)pl);
		}
		if (pl instanceof ChannelPlugin) {
			chanPlugins.remove((ChannelPlugin)pl);
		}
	}

	public void fireEventRecevied(IRCEvent evt) {
		if (evt.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
			for (ChannelPlugin plugin : chanPlugins) {
				if (plugin.willHandle((ChannelMsgEvent)evt)) {
					plugin.doEvent((ChannelMsgEvent)evt);
					break;
				}
			}
		} else if (evt.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
			for (PMPlugin plugin : pmPlugins) {
				if (plugin.willHandle((PrivateMsgEvent)evt)) {
					plugin.doEvent((PrivateMsgEvent)evt);
					break;
				}
			}
		}
	}
}
