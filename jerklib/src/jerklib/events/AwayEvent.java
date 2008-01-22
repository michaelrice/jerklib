package jerklib.events;

public interface AwayEvent extends IRCEvent {

    public boolean isAway();

    public String getOwnNick();

    public String getAwayMessage();

    public boolean isYou();

    public String getNick(); 


}
