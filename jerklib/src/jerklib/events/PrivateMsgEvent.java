package jerklib.events;



/**
 * @author mohadib
 * Event sent for private messages
 *
 */
public interface PrivateMsgEvent extends IRCEvent
{

	
	
  /**
   * returns the nick of the person who created the PrivMsgIRCEvent
   * 
   * @return the nick
   */
  public String getNick();

    /**
     * Returns the username field of the user's hostmask.
     * nick!username@host
     * @return the username of the person.
     */
    public String getUserName();
	
  
  /**
   * returns a string that represents the host
   * of the creator of this event
   * 
   * @return the host name
   */
  public String getHostName();
  
  
  /**
   * returns the message part of the event
   * 
   * @return the message
   */
  public String getMessage();
  
	
	
	
	
	
	
	
	
}
