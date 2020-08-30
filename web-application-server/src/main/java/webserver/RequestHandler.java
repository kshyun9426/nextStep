package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	
        	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        	byte[] body = null;
        	
        	//요청 header line 추출
        	String line = reader.readLine();
        	log.debug("request line : {}", line);
        	
        	if(line == null) {
        		return;
        	}
        	String[] tokens = line.split(" ");
        	
        	
        	if(tokens[0].equals("GET")) {
        		body = processGET(line, reader, tokens[1]);
        	}
        	
        	if(tokens[0].equals("POST")) {
        		processPOST(tokens[1], reader, tokens[1]);
        	}
        	
            DataOutputStream dos = new DataOutputStream(out);
//            byte[] body = Files.readAllBytes(new File("./webapp"+reqPath).toPath());
            
            
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos, int lengthOfBodyContent, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: "+ redirectUrl +"\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
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
   
    
    private User saveUserInfo(Map<String,String> reqUserInfo) {
    	User user = new User(reqUserInfo.get("userId")
    							, reqUserInfo.get("password")
    							, reqUserInfo.get("name")
    							, reqUserInfo.get("email"));
    	return user;
    }
    
    //GET방식 처리 메서드
    private byte[] processGET(String line, BufferedReader reader, String requestStr) throws IOException {
    	
    	while(!line.equals("")) {
    		line = reader.readLine();
    		log.debug("header : {}", line);
    	}
    	
    	//요청경로가 /user/create이면 User클래스에 요청파라미터 값 저장
    	String reqPath = requestStr;
    	String params = null;
    	
    	int reqStrIndex = requestStr.indexOf("?");
    	
    	if(reqStrIndex != -1) {
        	reqPath = requestStr.substring(0,reqStrIndex);
           	params = requestStr.substring(reqStrIndex+1);
    	}
    	
    	//요청 경로가 /user/create이라면 User객체에 요청 정보 저장
        if(reqPath.equals("/user/create")) {
        	//요청 파라미터 파싱
        	Map<String,String> reqParamInfo = HttpRequestUtils.parseQueryString(params);
        	
            //User클래스에 저장
            User user = saveUserInfo(reqParamInfo);
            log.info(user.toString());
            
            reqPath = "/index.html";
        }

        return Files.readAllBytes(new File("./webapp"+reqPath).toPath());
    }
    
    //POST방식 처리 메서드
    private void processPOST(String line, BufferedReader reader, String reqPath) throws IOException {
    	//content-length의 값 추출해야함
    	int contentLength = 0;
    	
    	//Header 정보 
    	while(!line.equals("")) {
    		line = reader.readLine();
    		
    		//Header가 마지막이라면
    		if(line.isEmpty()) {
    			break;
    		}
    		log.debug("header : {}", line);
    		
    		Pair keyValue = HttpRequestUtils.parseHeader(line);
    		if(keyValue.getKey().equals("Content-Length")) {
    			contentLength = Integer.parseInt(keyValue.getValue());
    		}
    	}
    	
    	//요청 경로가 /user/create이라면 User객체에 요청 정보 저장
    	if(reqPath.equals("/user/create")) {
    		String bodyInfo = IOUtils.readData(reader, contentLength);
        	log.debug("body : {}", bodyInfo);
        	Map<String,String> reqParamInfo = HttpRequestUtils.parseQueryString(bodyInfo);
        	
        	 //User클래스에 저장
            User user = saveUserInfo(reqParamInfo);
            log.info(user.toString());
    	}
    	
    }
   
}























