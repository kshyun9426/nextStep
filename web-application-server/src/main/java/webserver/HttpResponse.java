package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 1) RequestHandler 클래스를 보면 응답 데이터 처리를 위한 많은 중복이 있다. 이 중복을 제거해 본다. 
 * 2) 응답 헤더 정보를 Map<String,String>으로 관리한다.
 * 3) 응답을 보낼때 Html, Css, Javascript 파일을 직접 읽어 응답으로 보내는 메서드는 forward(). 다른 URL로 리다이렉트하는 메서드는 sendRedirect()메서드를 나누어 구현한다.
 */
public class HttpResponse {
	
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	private DataOutputStream dos;
	private Map<String, String> header;
	
	public HttpResponse(OutputStream outputStream) {
		this.dos = new DataOutputStream(outputStream);
		this.header = new HashMap<>();
	}
	
	//Html, Css, Javascript 파일을 직접 읽어 응답으로 보내는 메서드
	public void forward(String url) throws Exception {
		byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		init200Header(body.length);
		make200Header(dos);
    	responseBody(dos, body);
	}
	
	//다른 URL로 리다이렉트하는 메서드
	public void sendRedirect(String url) {
		init302Header(url);
		make302Header(dos);
	}
	
	private void init200Header(int lengthOfBodyContent) {
		header.put("Content-Type", "text/html;charset=utf-8");
    	header.put("Content-Length", String.valueOf(lengthOfBodyContent));
	}
	
	private void init302Header(String redirectUrl) {
		header.put("Location", redirectUrl);
	}
	
	private void make200Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            for(String key : header.keySet()) {
            	dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void make302Header(DataOutputStream dos) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
    		for(String key : header.keySet()) {
            	dos.writeBytes(key + ": " + header.get(key) + "\r\n");
            }
    		dos.writeBytes("\r\n");
    	}catch(IOException e) {
    		log.error(e.getMessage());
    	}
    }
	
	private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	public void addHeader(String field, String value) {
		header.put(field, value);
	}
}


















