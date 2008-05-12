package jerklib.events;

import jerklib.ServerInformation;

public interface ServerInformationEvent extends IRCEvent
{
    ServerInformation getServerInformation();
}
