package jerklib.tasks;

public interface TaskCompletionListener<T>
{
	public void taskComplete(T result);
}
