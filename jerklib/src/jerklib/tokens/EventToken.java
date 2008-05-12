package jerklib.tokens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jerklib.tokens.Token.Type;

public class EventToken implements Iterable<Token>
{
	private String data;
	private List<Token>tokens = new ArrayList<Token>();
	
	public EventToken(String data)
	{
		this.data = data;
		tokenize();
	}
	
	public String getCommand()
	{
		return getWordTokens().get(1).data;
	}
	
	
	public String getData()
	{
		return data;
	}
	
	public List<Token> getTokens()
	{
		return new ArrayList<Token>(tokens);
	}
	
	public List<Token> getWordTokens()
	{
		List<Token> returnList = new ArrayList<Token>();
		for(Token t : tokens)
		{
			if(t.type == Type.NON_WHITESPACE)
			{
				returnList.add(t);
			}
		}
		return returnList;
	}
	
	public String substring(int offset , int length)
	{
		return data.substring(offset , offset + length);
	}
	
	public String substring(int offset)
	{
		return data.substring(offset);
	}
	
	public String concatTokens(int tokenOffset)
	{
		String data = "";
		for(int i = tokenOffset; i < tokens.size(); i++)
		{
			data += tokens.get(i).data;
		}
		return data;
	}
	
	private void tokenize()
	{
		char[] chars = data.toCharArray();
		
		Token token = new Token();
		
		for(int i = 0; i < chars.length ; i++)
		{
			
			char c = chars[i];
			if(Character.isWhitespace(c))
			{
				if(token.type == null)token.type = Type.WHITESPACE;
				if(token.type != Type.WHITESPACE)
				{
					tokens.add(token);
					token = new Token();
					token.type = Type.WHITESPACE;
					token.offset = i;
				}
			}
			else
			{
				if(token.type == null)token.type = Type.NON_WHITESPACE;
				if(token.type != Type.NON_WHITESPACE)
				{
					tokens.add(token);
					token = new Token();
					token.type = Type.NON_WHITESPACE;
					token.offset = i;
				}
			}
			
			token.len++;
			token.data += c;
			
			if(i + 1 == chars.length) tokens.add(token);
		}
	}
	
	public String toString()
	{
		return data;
	}

	public Iterator<Token> iterator()
	{
		return tokens.iterator();
	}
}
