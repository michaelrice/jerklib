/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.NickChangeIRCEvent

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

/**
 * NickChangeIRCEvent is created when someone in a channel changes their nick
 * 
 * @author mohadib
 *
 */
public interface NickChangeEvent extends IRCEvent {
	
  /**
   * getOldNick() returns the previous nick of the user before the change
   * 
   * @return <code>String</code> Old nick for user.
   */
  public String getOldNick();
  
  /**
   * getNewNick() returns the new nick of the user
   * 
   * @return <code>String</code> New nick for user
   */
  public String getNewNick();
}
