package jerklib;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A Class to parse a line of IRC text
 * 
 * <pre> 
 * <message>  ::= [':' <prefix> <SPACE> ] <command> <params> <crlf>
 * <prefix>   ::= <servername> | <nick> [ '!' <user> ] [ '@' <host> ]
 * <command>  ::= <letter> { <letter> } | <number> <number> <number>
 * <SPACE>    ::= ' ' { ' ' }
 * <params>   ::= <SPACE> [ ':' <trailing> | <middle> <params> ]
 *
 * <middle>   ::= <Any *non-empty* sequence of octets not including SPACE
 *              or NUL or CR or LF, the first of which may not be ':'>
 * <trailing> ::= <Any, possibly *empty*, sequence of octets not including
 *                NUL or CR or LF>
 * </pre>
 * 
 * 
 * @author mohadib
 *
 */
public class EventToken
{
	private final String data; 
	private String prefix = "", command = "";
	private List<String>arguments = new ArrayList<String>();
	private int offset = 0;
	
	public EventToken(String data)
	{
		this.data = data;
		parse();
	}
	
	private void parse()
	{
		//see if message has prefix
		if(data.startsWith(":"))
		{
			extractPrefix(data);
			incTillChar();
		}
		
		//get command
		command = data.substring(offset , data.indexOf(" ", offset));
		offset += command.length();
		incTillChar();
		
		extractArguments();
	}
	
	private void extractArguments()
	{
		String argument = "";
		for(int i = offset; i < data.length() ; i++)
		{
			if(!Character.isWhitespace(data.charAt(i)))
			{
				argument += data.charAt(i);
				
				//if argument.equals(":") then arg is everything till EOL
				if(argument.length() == 1 && argument.equals(":"))
				{
					argument = data.substring(i + 1);
					arguments.add(argument);
					return;
				}
				offset++;
			}
			else
			{
				if(argument.length() > 0)
				{
					arguments.add(argument);
					argument = "";
				}
				offset++;
			}
		}
		
		if(argument.length() != 0)
		{
			arguments.add(argument);
		}
	}
	
	// increment offset till next non whitespace char
	private void incTillChar()
	{
		for(int i = offset ; i < data.length(); i++)
		{
			if(!Character.isWhitespace(data.charAt(i)))
			{
				return;
			}
			offset++;
		}
	}
	
	private void extractPrefix(String data)
	{
		//set prefix - : is at 0
		prefix = data.substring(1 , data.indexOf(" "));
		
		//increment offset , +1 is for : removed
		offset += prefix.length() + 1;
	}
	
	public String hostName()
	{
		int index = prefix.indexOf('@');
		if(index != -1 && index + 1 < prefix.length())
		{
			return prefix.substring(index + 1);
		}
		return "";
	}

	public String userName()
	{
		int sindex = prefix.indexOf('!');
		int eindex = prefix.indexOf("@");
		if(eindex == -1)eindex = prefix.length() - 1;
		if(sindex != -1 && sindex + 1 < prefix.length())
		{
			return prefix.substring(sindex + 1 , eindex);
		}
		return "";
	}
	
	public String nick()
	{
		if(prefix.indexOf("!") != -1)
		{
			return prefix.substring(0,prefix.indexOf('!'));
		}
		return "";
	}

	public String prefix()
	{
		return prefix;
	}
	
	public String command()
	{
		return command;
	}
	
	public List<String>args()
	{
		return arguments;
	}
	
	public String arg(int index)
	{
		if(index < arguments.size())
		{
			return arguments.get(index);
		}
		return null;
	}
	
	public String data()
	{
		return data;
	}
	
	public int numeric()
	{
		int i = -1;
		try
		{
			i = Integer.parseInt(command);
		}
		catch (NumberFormatException e){}
		return i;
	}
	
	public String toString()
	{
		return data;
	}
}
