/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.UpdateHostNameIRCEvent

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
 * UpdateHostNameIRCEvent - event made when connected to the server
 * This event contains the real server name. Example. When connection
 * to 'irc.freenode.net' we might actually connect to kornbluf.freenode.net 
 * or sopme other host. This event will have the real hosts name.
 * 
 * @author mohadib
 *
 */
public interface ConnectionCompleteEvent extends IRCEvent
{
  
  public String getActualHostName();

}
