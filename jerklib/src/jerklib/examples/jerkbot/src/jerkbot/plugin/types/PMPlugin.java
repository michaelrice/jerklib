package jerkbot.plugin.types;

import jerkbot.plugin.Plugin;
import jerklib.events.PrivateMsgEvent;

public abstract class PMPlugin extends Plugin {
	public abstract boolean willHandle(PrivateMsgEvent event);
	public abstract void doEvent(PrivateMsgEvent event);
	public abstract String[] getArgs(PrivateMsgEvent event);
}
