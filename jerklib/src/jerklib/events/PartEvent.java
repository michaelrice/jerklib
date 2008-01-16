/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.PartIRCEvent

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
 * PartIRCEvent() is made when someone parts a channel 
 * 
 * @author mohadib
 *
 */
public interface PartEvent extends IRCEvent {
  
  
	/**
   * getWho() returns the nick of who parted
   * 
	 * @return <code>String</code> nick of parted
	 */
	public String getWho();
  
  
	/**
   * getChannelName() returns the name of the channel parted
   * 
	 * @return <code>String</code> name of channel parted
	 */
	public String getChannelName();
	
  
  /**
   * getChannel() returns IRCChannel object for channel parted
   * @return <code>IRCChannel</code> IRCChannel object parted
   */
  public Channel getChannel();
	
  
  /**
   * getPartMessage() returns part message if there is one
   * 
   * @return <code>String</code> part message
   */
  public String getPartMessage();
}
