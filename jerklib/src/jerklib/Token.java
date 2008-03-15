package jerklib;

public class Token
{
	
	String data = "";
	int offset = 0;
	int len = 0;
	Type type;
	
	enum Type
	{
		WHITESPACE,
		NON_WHITESPACE
	}
}
