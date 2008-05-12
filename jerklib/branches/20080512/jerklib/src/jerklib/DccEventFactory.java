package jerklib;

import java.net.InetAddress;
import java.util.List;

import jerklib.events.MessageEvent;
import jerklib.events.dcc.DccEvent;
import jerklib.events.impl.dcc.DccAcceptEventImpl;
import jerklib.events.impl.dcc.DccChatEventImpl;
import jerklib.events.impl.dcc.DccResumeEventImpl;
import jerklib.events.impl.dcc.DccSendEventImpl;
import jerklib.events.impl.dcc.DccUnknownEventImpl;
import jerklib.util.InetAddressUtils;

/**
 * Factory methods for DCC Events.
 * 
 * @author Andres N. Kievsky
 */

class DccEventFactory
{

	static DccEvent dcc(MessageEvent event, String ctcpString)
	{
		
		EventToken dccTokens = new EventToken(ctcpString);
		List<Token> dccTokenList = dccTokens.getWordTokens();

		// TODO ANK: Reject invalid ports, invalid filenames, IPs like 0.0.0.0.

		if(dccTokenList.size() >= 2)
		{
			String dccType = dccTokenList.get(1).data;
			
			// DCC SEND filename ip port <fsize>
			if("SEND".equals(dccType)
					&& (dccTokenList.size() == 5 || dccTokenList.size() == 6)
					&& dccTokenList.get(3).isNumeric()
					&& dccTokenList.get(4).isInteger())
			{
				String filename = dccTokenList.get(2).data;
				InetAddress ip = InetAddressUtils.parseNumericIp(dccTokenList.get(3).getAsLong());
				int port = dccTokenList.get(4).getAsInteger();

				// File Size is optative.
				long fileSize = -1;
				if (dccTokenList.size() == 6 && dccTokenList.get(5).isLong())
				{
					fileSize = dccTokenList.get(5).getAsLong();
				}

				if (ip != null) {
					return new DccSendEventImpl(filename, ip, port, fileSize, ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
				}
			}
			
			// DCC RESUME filename port position
			else if("RESUME".equals(dccType)
					&& dccTokenList.size() == 5
					&& dccTokenList.get(3).isInteger()
					&& dccTokenList.get(4).isLong())
			{
				String filename = dccTokenList.get(2).data;
				int port = dccTokenList.get(3).getAsInteger();
				long position = dccTokenList.get(4).getAsLong();
				return new DccResumeEventImpl(filename, port, position, ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
			}
			
			// DCC ACCEPT filename port position
			else if("ACCEPT".equals(dccType)
					&& dccTokenList.size() == 5		
					&& dccTokenList.get(3).isInteger()
					&& dccTokenList.get(4).isLong())
			{
				String filename = dccTokenList.get(2).data;
				int port = dccTokenList.get(3).getAsInteger();
				long position = dccTokenList.get(4).getAsLong();
				return new DccAcceptEventImpl(filename, port, position, ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
			}
			
			// DCC CHAT protocol ip port
			else if("CHAT".equals(dccType)
					&& dccTokenList.size() == 5
					&& dccTokenList.get(3).isNumeric()
					&& dccTokenList.get(4).isInteger())
			{
				String protocol = dccTokenList.get(2).data;
				InetAddress ip = InetAddressUtils.parseNumericIp(dccTokenList.get(3).getAsLong());
				int port = dccTokenList.get(4).getAsInteger();
				
				if (ip != null)
				{
					return new DccChatEventImpl(protocol, ip, port, ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
				}
			}
			
		}
		
	// Default case: unknown DCC type.
		return new DccUnknownEventImpl(ctcpString, event.getHostName(), event.getMessage(), event.getNick(), event.getUserName(), event.getRawEventData(), event.getChannel(), event.getSession());
	}

}
