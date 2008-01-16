package marinbot.plugin.types;

import jerklib.events.PrivateMsgEvent;

public interface PrivateMsgPlugin {
	void eventReceived(PrivateMsgEvent evt);
}
