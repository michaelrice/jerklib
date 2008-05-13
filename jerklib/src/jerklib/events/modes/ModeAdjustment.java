package jerklib.events.modes;


public class ModeAdjustment
{
	private final Action action;
	private final char mode;
	private final String argument;
	
	public static enum Action
	{
		PLUS,
		MINUS
	}
	
	public ModeAdjustment(Action action , char mode , String argument)
	{
		this.action = action;
		this.mode = mode;
		this.argument = argument;
	}
	
	public Action getAction()
	{
		return action; 
	}
	
	public char getMode()
	{
		return mode;
	}
	
	/**
	 * This will return the argument for 
	 * this mode if any.
	 * 
	 * @return
	 */
	public String getArgument()
	{
		return argument;
	}
	
	public String toString()
	{
		return (action == Action.PLUS?"+":"-") + mode + " " + argument;
	}
}
