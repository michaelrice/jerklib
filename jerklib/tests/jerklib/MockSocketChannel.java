package jerklib;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class MockSocketChannel extends SocketChannel
{

	
	protected MockSocketChannel(SelectorProvider provider)
	{
		super(provider);
	}

	@Override
	public boolean connect(SocketAddress remote) throws IOException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finishConnect() throws IOException
	{
		return true;
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	@Override
	public boolean isConnectionPending()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Socket socket()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer src) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length) throws IOException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void implCloseSelectableChannel() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void implConfigureBlocking(boolean block) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

}
