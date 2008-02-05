package jerklib.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jerklib.events.listeners.IRCEventListener;

public abstract class Task implements IRCEventListener
{
	private final List<TaskCompletionListener> listeners = new ArrayList<TaskCompletionListener>();
	private boolean canceled;
	private String name;
	
	private Task(){}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void cancel()
	{
		canceled = true;
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
	
	public void addTaskListener(TaskCompletionListener listener)
	{
		listeners.add(listener);
	}
	
	public boolean removeTaskListener(TaskCompletionListener listener)
	{
		return listeners.remove(listener);
	}
	
	public List<TaskCompletionListener> getTaskListeners()
	{
		return Collections.unmodifiableList(listeners);
	}
	
	public void taskComplete(Object result)
	{
		for(TaskCompletionListener listener : listeners)
		{
			listener.taskComplete(result);
		}
	}
}
