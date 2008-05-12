import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.IRCEvent;
import jerklib.events.ServerVersionEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;
import jerklib.tasks.Task;
import jerklib.tasks.TaskImpl;


/* Bot to connect to major IRCD types to log raw data
 * to write tests with. 
 * 
 * irc.dalnet.com - bahamut
 * 	#perkosa
 * 
 * irc.nixgeeks.com - Unreal
 *  #tvtorrents
 *  
 * irc.freenode.net - hyperion
 * 	#ubuntu
 * 
 * irc.quakenet.org - snircd
 * 	#cod4.wars
 *
 * //TODO
 * irc.shadow-realm.org - ultimate
 *
 * //TODO
 * us.undernet.org - Undernet (ircu)
 * #ubuntu
 * 
 */
public class DataBot implements IRCEventListener
{
	private FileOutputStream fos;
	private Map<String, String>ircdMap = new HashMap<String, String>();
	private ConnectionManager conMan;
	private Profile profile = new ProfileImpl("scripy" , "scripy" , "scripy1" , "scripy2");
	int i = 0;
	
	public DataBot(File logFile)
	{
		try
		{
			fos = new FileOutputStream(logFile , false);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				try
				{
					fos.flush();
					fos.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		ConnectionManager.dontParse = true;
		conMan = new ConnectionManager(profile);
		makeConnections();
	}
	
	public void receiveEvent(IRCEvent e)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("# Host:" + e.getSession().getRequestedConnection().getHostName() + "\n");
		builder.append("# Version:" + ircdMap.get(e.getSession().getRequestedConnection().getHostName()) + "\n");
		builder.append(e.getRawEventData() + "\r\n");
		builder.append("\n");
        try
		{
			fos.write(builder.toString().getBytes());
			fos.flush();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			System.exit(1);
		}
	}
	
	
	private void makeConnections()
	{
		Task t = new TaskImpl("version")
		{
			public void receiveEvent(IRCEvent e)
			{
				ServerVersionEvent se = (ServerVersionEvent)e;
				ircdMap.put(e.getSession().getRequestedConnection().getHostName() , se.getVersion());
			}
		};


        String[] hosts = { "rumble.dal.net" , "irc.nixgeeks.com" , "irc.freenode.net" , "irc.quakenet.org","Vancouver.BC.CA.Undernet.org","irc.shadow-realm.org"};
		for(String host : hosts)
		{
			final Session session = conMan.requestConnection(host);
			session.onEvent(t , Type.SERVER_VERSION_EVENT);            
            session.addIRCEventListener(this);
            
            session.onEvent(new TaskImpl("join")
			{
				int x = i;
				public void receiveEvent(IRCEvent e)
				{
					switch(x)
					{
						case 0:
							{
								System.out.println("Joining perkosa");
								session.join("#perkosa");break;
							}
						case 1:
							{
								System.out.println("Joining tvtorrents");
								session.join("#tvtorrents");break;
							}
						case 2:
							{
								System.out.println("Joining ubuntu");
								session.join("#ubuntu");break;
							}
						case 3:
							{
								System.out.println("Joining cod4.wars");
								session.join("#cod4.wars");break;
							}
                        case 4:
                             {
                                System.out.println("Joining ubuntu");
                                session.join("#ubuntu");
                             }
                        case 5:
                             {
                                 System.out.println("Joining shadowrealm");
                                 session.join("#shadowrealm");
                             }

                        default: System.out.println("NO " + i);
					}
				}
			} , Type.CONNECT_COMPLETE);
			i++;
		}
	}
	
	public static void main(String[] args)
	{
		File file = new File(args[0]);
		new DataBot(file);
	}
}
