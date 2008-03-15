package jerklib;

public class Token
{
	
	public String data = "";
	public int offset = 0;
	public int len = 0;
	public Type type;
	
	enum Type
	{
		WHITESPACE,
		NON_WHITESPACE
	}
}
