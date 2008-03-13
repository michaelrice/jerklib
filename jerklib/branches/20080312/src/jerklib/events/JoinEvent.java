/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.JoinIRCEvent

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
 * JoinIRCEvent is the event that will be dispatched when someone joins a channel
 * 
 * @author mohadib
 *
 */
public interface JoinEvent extends IRCEvent {

    /**
     * returns the nick of who joined the channel
     *
     * @return Nick of who joined channel
     */
	public String getNick();

    
    /**
     * return the username in the user's hostmask
     * @return username of the user
     */
    public String getUserName(); 

    /**
     * returns the hostname of the person who joined the channel
     * @return hostname of the person who joined
     */
    public String getHostName();
	
  /**
   * returns the name of the channel joined to cause this event
   * 
   * @return Name of channel
   */
  public String getChannelName();
	
  /**
   * returns the Channel object joined
   * 
   * @return The Channel object
   * @see Channel
   */
  public Channel getChannel();

}