package jerklib.examples.jerkbot.plugin.types;

import jerklib.events.PrivateMsgEvent;
import jerklib.examples.jerkbot.plugin.Plugin;

public abstract class PMPlugin extends Plugin {
	public abstract boolean willHandle(PrivateMsgEvent event);
	public abstract void doEvent(PrivateMsgEvent event);
	public abstract String[] getArgs(PrivateMsgEvent event);
}
