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
		//remove :
		data = data.substring(1);
		//move offset up one for char we removed
		offset++;
		
		//set prefix
		prefix = data.substring(0 , data.indexOf(" "));
		
		//increment offset
		offset += prefix.length();
	}
	
	public String getHostName()
	{
		int index = prefix.indexOf('@');
		if(index != -1 && index + 1 < prefix.length())
		{
			return prefix.substring(index + 1);
		}
		return "";
	}

	public String getUserName()
	{
		int sindex = prefix.indexOf('!');
		int eindex = prefix.indexOf("@");
		if(eindex == -1)eindex = prefix.length() - 1;
		if(sindex != -1 && sindex + 1 < prefix.length())
		{
			return prefix.substring(sindex , eindex);
		}
		return "";
	}
	
	public String getNick()
	{
		if(prefix.indexOf("!") != -1)
		{
			return prefix.substring(0,prefix.indexOf('!'));
		}
		return "";
	}

	public String getPrefix()
	{
		return prefix;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public List<String>getArguments()
	{
		return arguments;
	}
	
	public String getData()
	{
		return data;
	}
	
	public int getNumeric()
	{
		int i = -1;
		try
		{
			i = Integer.parseInt(command);
		}
		catch (NumberFormatException e) 
		{
			e.printStackTrace();
		}
		return i;
	}
	
	public String toString()
	{
		return data;
	}
}
