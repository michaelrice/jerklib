package jerklib;


import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertTrue;

public class EventTokenTest
{
	@Test
	public void parseTest1()
	{
		EventToken eventToken = new EventToken(":kubrick.freenode.net 353 mohadib_ = ##java :m0` derjohn mr_daniel Dry4d shadowarts victori_ erikwt Advant skoptelov _W_ Sargun inimesekene TdC_VgA df00z prgrmr shabble fex[ delskorch Ragnor blbrown paddax vlii pa kadams ntolo hashman beasty rollins lataffe xea Zaph0d^ amitev Kwitschibo wyvern` fez Fasga HelloMeow oxi bimbo dequeued jlindsay ferret_0567 ordain meeper Apocalisp EnginA pr3d4t0r traxy joed RedDisc NoReGreT Paks vinse linxeh Rytmis goki_work pithen savetheWorld cofeineSunshine" );
		assertTrue(eventToken.getPrefix() , eventToken.getPrefix().equals("kubrick.freenode.net"));
		
		assertTrue(eventToken.getCommand().equals("353"));
		
		assertTrue(eventToken.args().size()+"" , eventToken.args().size() == 4);
	}
	
	
	
	@Test
	public void parseTest2()
	{
		EventToken eventToken = new EventToken(":NeWtoz!jimmy@nix-2F996C9F.dhcp.aldl.mi.charter.com PRIVMSG #tvtorrents :I should be in bed, so I'm going to bed");
		assertTrue(eventToken.getPrefix() , eventToken.getPrefix().equals("NeWtoz!jimmy@nix-2F996C9F.dhcp.aldl.mi.charter.com"));
		
		assertTrue(eventToken.getCommand().equals("PRIVMSG"));
		
		assertTrue(eventToken.args().size()+"" , eventToken.args().size() == 2);
	}
	
	@Test
	public void parseTest3()
	{
		EventToken eventToken = new EventToken("PING :irc.nixgeeks.com");
		assertTrue(eventToken.getPrefix() , eventToken.getPrefix().equals(""));
		
		assertTrue(eventToken.getCommand().equals("PING"));
		
		assertTrue(eventToken.args().size()+"" , eventToken.args().size() == 1);
		assertTrue(eventToken.arg(0).equals("irc.nixgeeks.com"));
	}
	
	
	
	
	
	
}
