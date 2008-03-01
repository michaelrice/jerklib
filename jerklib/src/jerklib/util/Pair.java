package jerklib.util;

public class Pair<A,B>
{
	public final A first;
	public final B second;
	
	public Pair(A first , B second)
	{
		this.first = first;
		this.second = second;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Pair && obj.hashCode() == hashCode())
		{
			Pair<A, B> other = (Pair<A, B>)obj;
			return other.first.equals(first) && other.second.equals(second);
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return first.hashCode() ^ 42 ^ second.hashCode();
	}
}
