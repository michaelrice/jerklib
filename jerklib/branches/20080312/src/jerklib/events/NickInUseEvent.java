/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.NickInUseIRCEvent

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
 * NickInUseIRCEvent is thrown when jerklib is trying to use a nick
 * that is in use on a given server.
 * 
 * @author mohadib
 *
 */
public interface NickInUseEvent extends IRCEvent
{
	
  /**
   * returns nick that was in use
   * 
   * @return nick that was in use.
   */
  public String getInUseNick();
}
