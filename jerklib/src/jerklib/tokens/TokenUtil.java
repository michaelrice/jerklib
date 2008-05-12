package jerklib.tokens;


public class TokenUtil
{
	
	public static String getHostName(Token t)
	{
		return t.data.substring(t.data.indexOf('@') + 1);
	}

	public static String getUserName(Token t)
	{
		return t.data.substring(t.data.indexOf('!') + 1 , t.data.indexOf('@'));
	}
	
	public static String getNick(Token t)
	{
		if(t.data.indexOf("!") != -1)
		{
			return t.data.substring(1).substring(0,t.data.indexOf('!') - 1);
		}
		return t.data.substring(1);
	}
}
