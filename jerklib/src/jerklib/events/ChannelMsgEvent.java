/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.PrivMsgIRCEvent

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

package jerklib.events;

import jerklib.Channel;


/**
 * PrivMsgIRCEvent - this is the event made when someone speaks in a channel
 * 
 * @author mohadib
 *
 */
public interface ChannelMsgEvent extends IRCEvent {
  
  
	/**
   * getChannel() returns IRCChannel object the PrivMsg occured in 
   * 
	 * @return <code>IRCChannel</code> the IRCChannel object
	 */
	public Channel getChannel();
	
  
  /**
   * getNick() returns the nick of the person who created the PrivMsgIRCEvent
   * 
   * @return <code>String</code> the nick
   */
  public String getNick();

    /**
     * This will return the username field of the user's hostmask
     * nick!username@host
     * @return <code>String</code> the login field
     */
 public String getUserName();
	
  
  /**
   * getHostName() returns a string that represents the host
   * of the creator of this event
   * 
   * @return <code>String</code> the host string
   */
  public String getHostName();
  
  
  /**
   * getMessage() returns the message part of the event
   * 
   * @return <code>String</code> the message
   */
  public String getMessage();
  
}




