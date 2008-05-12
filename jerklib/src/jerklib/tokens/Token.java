package jerklib.tokens;

public class Token
{

	public String data = "";
	public int offset = 0;
	public int len = 0;
	public Type type;

	enum Type
	{
		WHITESPACE, NON_WHITESPACE
	}

	/**
	 * Does this token look like a number?
	 * in this instance decimal points will
	 * never appear so \\d+ works fine
	 * 
	 * @return true/false.
	 */
	public boolean isNumeric()
	{
		return data.matches("^\\d+$");
	}

	/**
	 * Check if the number is a long
	 * 
	 * @return true/false
	 */
	public boolean isLong()
	{
		if (!isNumeric()) { return false; }
		try
		{
			Long.parseLong(data);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}

	}

	/**
	 * Is this token a valid integer?
	 * 
	 * @return true/false.
	 */
	public boolean isInteger()
	{
		if (!isNumeric()) { return false; }
		try
		{
			Integer.parseInt(data);
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Convert to integer, if possible.
	 * 
	 * @return the int value;
	 */
	public int asInteger()
	{
		try
		{
			return Integer.parseInt(data);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	/**
	 * Get the Token as a long.
	 * 
	 * @return the long value.
	 */
	public long asLong()
	{
		try
		{
			return Long.parseLong(data);
		}
		catch (NumberFormatException e)
		{
			return 0L;
		}
	}
	
	@Override
	public String toString()
	{
		return data;
	}

}
