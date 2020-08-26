package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTests {
	
	@Test
	public void 문자열_split_테스트() {
		String str = "GET /index.html HTTP/1.1";
		String[] tokens = str.split(" ");
		assertThat(tokens[1], is("/index.html"));
	}
	
	
	
}
