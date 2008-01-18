package random;

import java.util.List;
import java.util.Random;

import jerklib.events.IRCEvent;

class Games {
	public void bomb(IRCEvent e) {
		List<String> nicks = e.getSession().getChannel("#jerklib")
				.getNicks();
		for (String nick : nicks) {
			if (!nick.equals("marinbot"))
				e.getSession().kick(nick, "BOOOM!",
						e.getSession().getChannel("#jerklib"));
		}
	}

	public void rr(IRCEvent e) {
		List<String> nicks = e.getSession().getChannel("#jerklib")
				.getNicks();

		Random r = new Random();
		int shot = r.nextInt(nicks.size());
		for (int i = 0; i < nicks.size(); i++) {
			if (i == shot)
				e.getSession().kick(nicks.get(r.nextInt(nicks.size())),
						"BANG", e.getSession().getChannel("#jerklib"));
			else
				e.getSession().channelSay("#jerklib",
						nicks.get(i) + ": CLICK!");
		}
	}
}
