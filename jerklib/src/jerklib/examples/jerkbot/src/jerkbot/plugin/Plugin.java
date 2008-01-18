package jerkbot.plugin;


public abstract class Plugin {
	private static String prefix = new String("~");

	/* name of the plugin. used when loading and unloading by name */
	public abstract String getTitle();

	/* help msg for the specified plugin */
	public abstract String getHelpMsg();

	/* returns te name of the of author of this plugin */
	public abstract String getAuthor();

	/* returns the version of the plugin */
	public abstract String getVersion();

	/* usage information */
	public abstract String getUsage();

	/* command (if any) that triggers this plugin */
	public abstract String getCommand();

	/**
	 * This is the global prefix that all plugins will respond to.
	 * TODO Make this a String[] so there can be more than one prefixes
	 * @return prefix
	 */
	public static String getPrefix() {
		return prefix;
	}

	/**
	 * Change the prefix 
	 * @param prefix new prefix
	 */
	public static void setPrefix(String prefix) {
		Plugin.prefix = prefix;
	}
}
