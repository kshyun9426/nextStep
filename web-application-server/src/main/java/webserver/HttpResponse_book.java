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

public class HttpResponse_book {
	
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	private DataOutputStream dos;
	private Map<String, String> headers = new HashMap<String,String>();
	
	public HttpResponse_book(OutputStream outputStream) {
		this.dos = new DataOutputStream(outputStream);
	}
	
	public void addHeader(String field, String value) {
		headers.put(field, value);
	}
	
	//Html, Css, Javascript 파일을 직접 읽어 응답으로 보내는 메서드
	public void forward(String url) {
		try {
			byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
			if(url.endsWith(".css")) {
				headers.put("Content-Type", "text/css");
			}else if(url.endsWith(".js")) {
				headers.put("Content-Type", "application/javascript");
			}else {
				headers.put("Content-Type", "text/html;charset=utf-8");
			}
			headers.put("Content-Length", body.length + "");
			response200Header(body.length);
			responseBody(body);
		}catch(IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public void forwardBody(String body) {
		byte[] contents = body.getBytes();
		headers.put("Content-Type", "text/html;charset=utf-8");
		headers.put("Content-Length", contents.length +"");
		response200Header(contents.length);
		responseBody(contents);
	}
	
	private void response200Header(int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			processHeaders();
			dos.writeBytes("\r\n");
		}catch(IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
	
	private void processHeaders() {
		try {
			for(String key : headers.keySet()) {
            	dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
		}catch(IOException e) {
			log.error(e.getMessage());
		}
	}
	
	//다른 URL로 리다이렉트하는 메서드
	public void sendRedirect(String redirectUrl) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			processHeaders();
			dos.writeBytes("Location: " + redirectUrl + " \r\n");
			dos.writeBytes("\r\n");
		}catch(IOException e) {
			log.error(e.getMessage());
		}
	}
	
}
