package jerklib.events;



public interface PrivateMsgEvent extends IRCEvent
{

	
	
  /**
   * getNick() returns the nick of the person who created the PrivMsgIRCEvent
   * 
   * @return <code>String</code> the nick
   */
  public String getNick();

    /**
     * getLogin() returns the login of the person who created the PrivMsgIRCEvent
     * :nick!login@host PRIVMSG :msg 
     * @return <code>String</code> the login of the person.
     */
    public String getLogin(); 
	
  
  /**
   * getNicksHost() returns a string that represents the host 
   * of the creator of this event
   * 
   * @return <code>String</code> the host string
   */
  public String getNicksHost();
  
  
  /**
   * getMessage() returns the message part of the event
   * 
   * @return <code>String</code> the message
   */
  public String getMessage();
  
	
	
	
	
	
	
	
	
}
