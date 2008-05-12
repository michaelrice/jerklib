package jerklib.tasks;

import jerklib.listeners.IRCEventListener;

public interface Task extends IRCEventListener
{
    public String getName();

    public void cancel();

    public boolean isCanceled();
}
