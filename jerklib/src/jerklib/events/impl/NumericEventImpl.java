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

	@Override
	public String getErrorMsg()
	{
		return errMsg;
	}

	@Override
	public ErrorType getErrorType()
	{
		return errorType;
	}

	@Override
	public int getNumeric()
	{
		return numeric;
	}

	@Override
	public String getRawEventData()
	{
		return rawEventData;
	}

	@Override
	public Session getSession()
	{
		return session;
	}

	@Override
	public Type getType()
	{
		return type;
	}

}
