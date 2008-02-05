package jerklib;

import jerklib.Session.State;


class ConnectionState
{
	private long lastResponse = System.currentTimeMillis();
	private State conState = State.CONNECTING;
	private PingState pingState = PingState.NO_ACTION_NEEDED;
	
	enum PingState
	{
		NEED_TO_PING,
		PING_SENT,
		NEED_TO_RECONNECT,
		NO_ACTION_NEEDED
	}
	
	void gotResponse()
	{
		lastResponse = System.currentTimeMillis();
		pingState = PingState.NO_ACTION_NEEDED;
	}
	
	void pingSent()
	{
		pingState = PingState.PING_SENT;
	}
	
	
	void setConState(State state)
	{
		conState = state;
	}
	
	State getConState()
	{
		return conState;
	}
	
	
	PingState getPingState()
	{
		long current = System.currentTimeMillis();
		
		if(current - lastResponse > 300000)
		{
			conState = State.DISCONNECTED;
			return PingState.NEED_TO_RECONNECT;
		}
		else if(pingState == PingState.PING_SENT)
		{
			return PingState.PING_SENT;
		}
		else if(current - lastResponse > 200000 && pingState != PingState.PING_SENT)
		{
			return PingState.NEED_TO_PING;
		}
		
		return PingState.NO_ACTION_NEEDED;
	}
	
}
