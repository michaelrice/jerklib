package jerkbot.plugin;

import java.util.ArrayList;

import jerkbot.plugin.types.ChannelMsgPlugin;
import jerkbot.plugin.types.PrivateMsgPlugin;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.PrivateMsgEvent;


public class PluginManager {
	private ArrayList<Plugin> pmPlugins = new ArrayList<Plugin>();
	private ArrayList<Plugin> chanPlugins = new ArrayList<Plugin>();

	public PluginManager() {

	}

	public void load(Plugin pl) {
		if (pl instanceof PrivateMsgPlugin) {
			pmPlugins.add(pl);
		}
		if (pl instanceof ChannelMsgPlugin) {
			chanPlugins.add(pl);
		}
	}

	public void unload(Plugin pl) {
		if (pl instanceof PrivateMsgPlugin) {
			pmPlugins.remove(pl);
		}
		if (pl instanceof ChannelMsgPlugin) {
			chanPlugins.remove(pl);
		}
	}

	public void fireEventRecevied(IRCEvent evt) {
		if (evt.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
			for (Plugin plugin : chanPlugins) {
				if (plugin.willHandle((ChannelMsgEvent)evt)) {
					plugin.doEvent((ChannelMsgEvent)evt);
					break;
				}
			}
		} else if (evt.getType() == IRCEvent.Type.PRIVATE_MESSAGE) {
			for (Plugin plugin : pmPlugins) {
				if (plugin.willHandle(evt)) {
					plugin.doEvent(evt);
					break;
				}
			}
		}
	}
}
