/*

 Jason Davis - 2005 | mohadib@openactive.org 

 jerklib.IRCWriteRequest

 This file is part of JerkLib Java IRC Library.

 JerkLib is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 JerkLib is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GenricPlayer; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA 

 */

package jerklib;



/**
 * IRCWriteRequest - this is sent to an IRCConnection whenever a 'write' needs
 * to happen. There are 3 types of IRCWriteRequests. PRIV_MSG , DIRECT_MSG ,
 * RAW_MSG (from the Type enum). RAW_MSG is used when you need direct access to
 * the IRC stream , else PRIV_MSG or DIRECT_MSG should be used.
 * 
 * @author mohadib
 * 
 */
public interface WriteRequest
{

	/**
	 * Type enum is used to determine type. It is returned from getType() PRIV_MSG
	 * is a standard msg to an IRC channel. DIRECT_MSG is msg sent directly to
	 * another user (not in a channel). RAW_MSG when direct access to the IRC
	 * stream is needed.
	 */
	public enum Type
	{
		CHANNEL_MSG, PRIVATE_MSG, RAW_MSG
	};

	/**
	 * getType() - returns Type of write request this is.
	 * 
	 * @return <code>Type</code> type of write request.
	 */
	public Type getType();

	/**
	 * getMessage() - returns message to write
	 * 
	 * @return <code>String</code> message to write
	 */
	public String getMessage();

	/**
	 * getChannel() - if Type is PRIV_MSG this method will return the IRCChannel
	 * object to send the message to.
	 * 
	 * @return <code>IRCChannel<code> channel to send message to
	 */
	public Channel getChannel();

	/**
	 * getConnection() - returns IRCConnection object to use for write.
	 * 
	 * @return <code>IRCConnection</code> RCConnection object to use for write
	 */
	public Connection getConnection();

	/**
	 * getNick() - if Type is DIRECT_MSG , this method will return the nick to
	 * send message to.
	 * 
	 * @return <code>String</code> nick to send message to
	 */
	public String getNick();
	
	public String getConnectionName();
	
	public String getChannelName();
	
}
