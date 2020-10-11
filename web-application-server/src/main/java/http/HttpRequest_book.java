package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;

/*
 * InputStream에 담겨있는 데이터를 필요한 형태로 파싱 후 객체의 필드에 저장하는 역할만 한다. 
 */
public class HttpRequest_book {
	
	private static final Logger log = LoggerFactory.getLogger(HttpRequest_book.class);
	
	private Map<String,String> headers = new HashMap<>();
	private Map<String,String> params = new HashMap<>();
	private RequestLine requestLine;
	
	public HttpRequest_book(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    	String line = br.readLine();
	    	if(line == null) {
	    		return;
	    	}
	    	
	    	requestLine = new RequestLine(line);
	    	
	    	line = br.readLine();
	    	while(line != null && !line.contentEquals("")) {
	    		log.debug("header : {}", line);
	    		String[] tokens = line.split(":");
	    		headers.put(tokens[0].trim(), tokens[1].trim());
	    		line = br.readLine();
	    	}
	    	
	    	if(getMethod() == HttpMethod.POST) {
	    		String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
	    		params = HttpRequestUtils.parseQueryString(body);
	    	}else {
	    		params = requestLine.getParams();
	    	}
		}catch(IOException io) {
			log.error(io.getMessage());
		}
	}
	
	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}
	
	public String getPath() {
		return requestLine.getPath();
	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public String getParameter(String name) {
		return params.get(name);
	}
	
	public HttpCookie getCookies() {
		return new HttpCookie(getHeader("Cookie"));
	}
	
	public HttpSession getSession() {
		return HttpSessions.getSession(getCookies().getCookie("JSESSIONID"));
	}
}
