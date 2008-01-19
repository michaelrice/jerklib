package jerklib.examples.jerkbot;

import java.io.File;
import java.util.ArrayList;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.listeners.IRCEventListener;
import jerklib.examples.jerkbot.plugin.Plugin;
import jerklib.examples.jerkbot.plugin.PluginManager;

public class Jerkbot implements IRCEventListener {
	ConnectionManager mngr;
	PluginManager plugins = new PluginManager();

	public static void main(String args[]) {
		new Jerkbot();
	}

	@SuppressWarnings("unchecked")
	public Jerkbot() {
		mngr = new ConnectionManager(new ProfileImpl("Jerk Bot", "jerkbot",
				"jerkb0t", "jerkb1t"));
		Session session = mngr.requestConnection("irc.freenode.net");
		session.addIRCEventListener(this);

		File libsFolder = new File("src/plugins");
		ArrayList<Class> pluginClases = new ArrayList<Class>();
		for (File file : libsFolder.listFiles()) {
			try {
				pluginClases.add(Class.forName("plugins." + file.getName().substring(0, file.getName().indexOf(".java"))));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (Class plugin : pluginClases) {
			try {
				plugins.load((Plugin)plugin.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void recieveEvent(IRCEvent e) {
		if (e.getType() == IRCEvent.Type.DEFAULT) {
			System.out.println(e.getRawEventData());
		}

		if (e.getType() == IRCEvent.Type.READY_TO_JOIN) {
			e.getSession().joinChannel("#jerklib");
		}

		if (e.getType() == IRCEvent.Type.CHANNEL_MESSAGE) {
			plugins.fireEventRecevied(e);
		}
	}
}
