package jerklib.events;

/**
 * @author Mohadib
 * Event fired for most all numeric error replies
 *
 */
public interface NumericErrorEvent extends IRCEvent
{
	
	/**
	 * @author Mohadib
	 * Enum used with getErrorType
	 *
	 */
	//http://www.irchelp.org/irchelp/rfc/chapter6.html
	public enum ErrorType
	{
		ERR_NOSUCHNICK, //401
		ERR_NOSUCHSERVER , //402,
		ERR_NOSUCHCHANNEL, //403,
		ERR_CANNOTSENDTOCHAN, //404
		ERR_TOOMANYCHANNELS, //405
		ERR_WASNOSUCHNICK, //406
		ERR_TOOMANYTARGETS, //407
		ERR_NOORIGIN, //409
		ERR_NORECIPIENT, //411
		ERR_NOTEXTTOSEND, //412
		ERR_NOTOPLEVEL, //413
		ERR_WILDTOPLEVEL, //414
		ERR_UNKNOWNCOMMAND, //421
		ERR_NOMOTD, //422
		ERR_NOADMININFO, //423
		ERR_FILEERROR, //424
		ERR_NONICKNAMEGIVEN, //431
		ERR_ERRONEUSNICKNAME, //432
		ERR_NICKNAMEINUSE, //433
		ERR_NICKCOLLISION, //436
		ERR_USERNOTINCHANNEL, //441
		ERR_NOTONCHANNEL, //442
		ERR_USERONCHANNEL, //443
		ERR_NOLOGIN, //444
		ERR_SUMMONDISABLED, //445
		ERR_USERSDISABLED, //446
		ERR_NOTREGISTERED, //451
		ERR_NEEDMOREPARAMS, //461
		ERR_ALREADYREGISTRED, //462
		ERR_NOPERMFORHOST, //463
		ERR_PASSWDMISMATCH, //464
		ERR_YOUREBANNEDCREEP, //465
		ERR_KEYSET, //467
		ERR_CHANNELISFULL, //471
		ERR_UNKNOWNMODE, //472
		ERR_INVITEONLYCHAN, //473
		ERR_BANNEDFROMCHAN, //474
		ERR_BADCHANNELKEY, //475
		ERR_NOPRIVILEGES, //481
		ERR_CHANOPRIVSNEEDED, //482
		ERR_CANTKILLSERVER, //483
		ERR_NOOPERHOST, //491
		ERR_UMODEUNKNOWNFLAG, //501
		ERR_USERSDONTMATCH //502
	}
	
	/**
	 * gets error type
	 * @return error type
	 */
	public ErrorType getErrorType();
	
	/**
	 * gets error message
	 * @return error message
	 */
	public String getErrorMsg();
	
	/**
	 * Gets numeric error code
	 * @return numeric
	 */
	public int getNumeric();
	
}
