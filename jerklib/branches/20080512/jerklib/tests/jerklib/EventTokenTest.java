package jerklib;

import java.util.List;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.assertTrue;

public class EventTokenTest
{

	@Test
	public void testTokenize()
	{
		String data = ":TVTorrentsBot!~PircBot@nix-555C426C.cust.blixtvik.net PRIVMSG #tvtorrents :<sammyp123> o yeh";
		EventToken t = new EventToken(data);
		
		String s = "";
		for(Token tok : t.getTokens())
		{
			s+=tok.data;
		}
		assertTrue(s.equals(data));
		
		
		List<Token> words = t.getWordTokens();
		assertTrue(words.size() == 6);
		
		assertTrue(words.get(2).data.equals("#tvtorrents"));
		
		assertTrue(words.get(2).offset ==63);
		
		assertTrue(t.substring(63).equals("#tvtorrents :<sammyp123> o yeh"));
		
		assertTrue(t.substring(63 , 11).equals("#tvtorrents"));
		
		Token token = words.get(2);
		
		assertTrue(token.len == 11);
		
		assertTrue(t.substring(token.offset , token.len).equals(token.data));
		
		assertTrue(t.concatTokens(4).equals("#tvtorrents :<sammyp123> o yeh"));
		
		
		t = new EventToken("\r\n\t");
		assertTrue(t.getTokens().size() == 1);
		assertTrue(t.getTokens().get(0).len == 3);
		assertTrue(t.getWordTokens().size() == 0);
	}
	
}
