package jerkbot;

public abstract class Logger {
	public abstract void log(int lvl, String msg);
	
	public enum Level {
		NONE,
		INFO,
		DEBUG
	}
}