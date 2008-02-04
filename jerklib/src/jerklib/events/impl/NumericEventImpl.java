package jerklib.events.impl;

import jerklib.Session;
import jerklib.events.NumericErrorEvent;

public class NumericEventImpl implements NumericErrorEvent
{
	private final String errMsg,rawEventData;
	private final ErrorType errorType;
	private final Type type = Type.ERROR;
	private final Session session;
	private final int numeric;
	
	public NumericEventImpl
	(
			String errMsg, 
			String rawEventData, 
			ErrorType errorType,
			int numeric,
			Session session
	)
	{
		super();
		this.errMsg = errMsg;
		this.rawEventData = rawEventData;
		this.errorType = errorType;
		this.session = session;
		this.numeric = numeric;
	}

	public String getErrorMsg()
	{
		return errMsg;
	}

	public ErrorType getErrorType()
	{
		return errorType;
	}

	public int getNumeric()
	{
		return numeric;
	}

	public String getRawEventData()
	{
		return rawEventData;
	}

	public Session getSession()
	{
		return session;
	}

	public Type getType()
	{
		return type;
	}

}
