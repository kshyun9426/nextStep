package http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class HttpRequest {
	
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private String 				requestLine;
	private StringBuilder		requestBody;
	private Map<String,String> 	requestHeader;
	private int					contentLength;
	private Map<String,String>  requestParam;
	
	public HttpRequest(InputStream inputStream) {
		
		this.requestHeader = new HashMap<>();
		this.requestBody   = new StringBuilder();
		this.requestParam  = new HashMap<>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			
			//요청 헤더정보 init
	    	String line = br.readLine();
	    	
	    	this.requestLine = line;
	    	log.debug("request line : {}", line);
	    	
	    	//헤더정보 init
			while(!line.equals("")) {
				line = br.readLine();
				if(line == null || line.equals("")) {
					break;
				}
				if(line.contains("Content-Length")) {
        			contentLength = getContentLength(line);
        		}
				Pair headerPair = HttpRequestUtils.parseHeader(line);
				requestHeader.put(headerPair.getKey(), headerPair.getValue());
				log.debug("request header : {}", line);
			}
			
			if(getMethod().contentEquals("GET")) {
				String[] reqTokens = requestLine.split(" ");
				if(reqTokens[1].contains("?")) {
					requestParam = HttpRequestUtils.parseQueryString(reqTokens[1].split("\\?")[1]);
				}
			}
			
			if(getMethod().contentEquals("POST")) {
				requestBody.append(IOUtils.readData(br, contentLength));
				log.debug("request body: {}", requestBody.toString());
				requestParam = HttpRequestUtils.parseQueryString(requestBody.toString());
			}
			
		}catch(Exception e) {
			log.error(e.getMessage());
		}
		
	}
	
	private int getContentLength(String line) {
    	String[] headerTokens = line.split(":");
    	return Integer.parseInt(headerTokens[1].trim());
    }
	
	public String getMethod() {
		return requestLine.split(" ")[0];
	}
	
	public String getPath() {
		String reqPath = requestLine.split(" ")[1];
		if(reqPath.contains("?")) {
			return reqPath.split("\\?")[0];
		}
		return requestLine.split(" ")[1];
	}
	
	public String getHeader(String key) {
		return this.requestHeader.get(key);
	}
	
	public String getParameter(String key) {
		return requestParam.get(key);
	}
	
	
	
}
