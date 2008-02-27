/*

 Jason Davis - 2005 | mohadib@openactive.org 

 jerklib.events.IRCEvent

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

import jerklib.Session;


/**
 * The base interface for all JerkLib events.
 * 
 * This Interface provieds the bare essintails for an IRCEvent. The raw event
 * data, host , port , and IRCConnection object.
 * 
 * @author mohadib
 * 
 */

public interface IRCEvent 
{

  /**
   * Type enum is used to determine type. It is returned from getType()
   */
  public enum Type 
  {
    /**
     *Topic event - channel topic event
     */
    TOPIC,
    
    /**
     * Private message event - msg not sent to channel 
     */
    PRIVATE_MESSAGE ,
    
    /**
     *Channel message event - msg sent to a channel 
     */
    CHANNEL_MESSAGE,
    
    /**
     *Server notice event 
     */
    NOTICE,
    
    /**
     * Message of the day event
     */
    MOTD, 
    
    /**
     * Default event - unreconized or ignored by JerkLib 
     */
    DEFAULT, 
    
    /**
     *Quit Event - when someone quits from a server 
     */
    QUIT, 
    
    /**
     *Part event - someone parts a channel 
     */
    PART, 
    
    /**
     *Join event - someone joins a channel 
     */
    JOIN, 
    
    /**
     * Nick change event - someones nick changed
     */
    NICK_CHANGE,
    
    /**
     * Nick in use event - The nick JerkLib is tring to use is in use
     */
    NICK_IN_USE,
    
    /**
     *unused 
     */
    EXCEPTION,
    
    /**
     * Event spawned from irc numeric 005
     */
    SERVER_INFORMATION,
    CONNECT_COMPLETE, 
    UPDATE_HOST_NAME, 
    READY_TO_JOIN, 
    JOIN_COMPLETE,
    MODE_EVENT,
    KICK_EVENT,
    NICK_LIST_EVENT,
    WHO_EVENT,  
    WHOIS_EVENT,
    WHOWAS_EVENT,
    CHANNEL_LIST_EVENT,
    INVITE_EVENT,
    SERVER_VERSION_EVENT,
    AWAY_EVENT,
    ERROR
  }

  /**
   * Used to find out the exact type of event the IRCEvent object
   * is. The IRCEvent object can be cast into a more specific event object to
   * get access to convience methods for the specific event types.
   * 
   * @return Type of event
   */
  public Type getType();

  
  /**
   * Returns the raw IRC data that makes up this event
   * 
   * @return Raw IRC event text.
   */
  public String getRawEventData();

  
  /**
   * Gets session for connection
   * @return Session 
   */
  public Session getSession();


}