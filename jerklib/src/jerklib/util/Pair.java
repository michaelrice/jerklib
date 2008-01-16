package jerklib.util;

public class Pair<T,E> 
{
	public final T first;
	public final E second;
	
	public Pair(T first , E second)
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
			Pair<T, E> other = (Pair<T, E>)obj;
			return other.first.equals(first) &&
				other.second.equals(second);
		}
		return false;
	}
	
	@Override
	public int hashCode() 
	{
		return first.hashCode() ^ 42 ^ second.hashCode();
	}
}
