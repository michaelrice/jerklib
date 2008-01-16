/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.QuitIRCEvent

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

import java.util.List;

import jerklib.Channel;

/**
 * QuitIRCEvent - this is the event made when someone quits
 * 
 * @author mohadib
 */
public interface QuitEvent extends IRCEvent{

  /**
   * getWho() returns the nick of who quit
   * 
   * @return <code>String</code> the nick who quit
   */
  public String getWho();
  
  
	/**
   * getQuitMessage() get the quit message
   * 
	 * @return <code>String</code> the quit message
	 */
	public String getQuitMessage();
  
	/**
   * getChannelList() returns a list of IRCChannel object
   * the nick who quit was in
   * 
	 * @return <code>List<IRCChannel><code> List of channels nick was in
	 */
	public List<Channel> getChannelList();
}
