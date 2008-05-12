package jerklib.tasks;

import jerklib.events.listeners.IRCEventListener;

public interface Task extends IRCEventListener
{
    public String getName();

    public void cancel();

    public boolean isCanceled();
}
