/*

Jason Davis - 2005 | mohadib@openactive.org 

jerklib.events.listeners.IRCWriteRequestListener

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

package jerklib.events.listeners;

import jerklib.WriteRequest;


/**
 * IRCWriteRequestListener - Listener to be notified of all writes
 *
 * @author mohadib
 */
public interface WriteRequestListener
{

    /**
     * receiveEvent() - method will be called anytime a write is requestd.
     *
     * @param request <code>IRCWriteRequest</code> the write request
     */
    public void receiveEvent(WriteRequest request);

}
