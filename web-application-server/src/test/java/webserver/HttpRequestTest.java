package webserver;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import http.HttpMethod;
import http.HttpRequest_book;

public class HttpRequestTest {

	private String testDirectory = "./src/test/resources/";
	
	@Test
	public void request_GET() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
//		HttpRequest request = new HttpRequest(in);
		HttpRequest_book request = new HttpRequest_book(in);
		
		assertEquals("GET", HttpMethod.GET.toString());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("kshyun", request.getParameter("userId"));
	}
	
	@Test
	public void request_POST() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
//		HttpRequest request = new HttpRequest(in);
		HttpRequest_book request = new HttpRequest_book(in);
		
		assertEquals("POST", HttpMethod.POST.toString());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("kshyun9426", request.getParameter("userId"));
	}
	
}
