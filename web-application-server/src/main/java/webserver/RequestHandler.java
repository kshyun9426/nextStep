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

import db.DataBase;
import model.HeaderInfoDTO;
import model.ResultDTO;
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
        	ResultDTO resultDTO = new ResultDTO();
        	
        	//요청 header line 추출
        	String line = reader.readLine();
        	
        	//요청 url추출
        	HeaderInfoDTO headerInfoDTO = extractReqUrl(line);
        	if(headerInfoDTO == null) {
        		return;
        	}
        	log.info(headerInfoDTO.toString());
        	resultDTO.setRequestUrl(headerInfoDTO.getRequestUrl());
        	DataOutputStream dos = new DataOutputStream(out);
        	if(headerInfoDTO.getRequestMethod().equals("GET")) {
        		resultDTO = processGET(headerInfoDTO.getRequestUrl(), reader, dos);
        		processGETResponse(resultDTO, dos);
        	}
        	
        	if(headerInfoDTO.getRequestMethod().equals("POST")) {
        		resultDTO = processPOST(headerInfoDTO.getRequestUrl(), reader, dos);
        		processPOSTResponse(resultDTO, dos);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private HeaderInfoDTO extractReqUrl(String headerLine) {
    	HeaderInfoDTO result = new HeaderInfoDTO();
    	log.debug("request line : {}", headerLine);
    	
    	if(headerLine == null) {
    		return null;
    	}
    	String[] tokens = headerLine.split(" ");
    	result.setRequestMethod(tokens[0]);
    	result.setRequestUrl(tokens[1]);
    	return result;
    }
    
    private void processGETResponse(ResultDTO resultDTO, DataOutputStream dos) throws IOException {
    	byte[] body = Files.readAllBytes(new File("./webapp"+resultDTO.getJspFilePath()).toPath());
    	response200Header(dos, body.length);
    	responseBody(dos, body);
    }
    
    
    private void processPOSTResponse(ResultDTO resultDTO, DataOutputStream dos) throws IOException {
    	byte[] body = null;
    	if(resultDTO.getRequestUrl().equals("/user/create")) {
    		if(resultDTO.getResultCode().equals("100")) {
    			body = Files.readAllBytes(new File("/webapp/index.html").toPath());
    			response302Header(dos, body.length, "/index.html");
    		}
    	}else if(resultDTO.getRequestUrl().equals("/user/login")) {
    		if(resultDTO.getResultCode().equals("100")) {
    			body = Files.readAllBytes(new File("/webapp/index.html").toPath());
    			response200CookieHeader(dos, body.length, "true");
    		}else {
    			body = Files.readAllBytes(new File("/webapp/user/login_failed.html").toPath());
    			response200CookieHeader(dos, body.length, "false");
    		}
    	}
    	responseBody(dos, body);
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
    
    private void response200CookieHeader(DataOutputStream dos, int lengthOfBodyContent, String isLogined) {
    	try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=" + isLogined);
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void processLogin(DataOutputStream dos, ResultDTO resultDTO) {
    	try {
    		byte[] body = Files.readAllBytes(new File("./webapp"+resultDTO.getJspFilePath()).toPath());
        	if(resultDTO.getResultCode().equals("100")) {
        		response200CookieHeader(dos, body.length, "true");
        	}else {
        		response200CookieHeader(dos, body.length, "false");
        	}
        	responseBody(dos,body);
    	}catch(Exception e) {
    		e.printStackTrace();
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
   
    
    private User mapUserInfo(Map<String,String> reqUserInfo) {
    	User user = new User(reqUserInfo.get("userId")
    							, reqUserInfo.get("password")
    							, reqUserInfo.get("name")
    							, reqUserInfo.get("email"));
    	return user;
    }
    
    //GET방식 처리 메서드
    private ResultDTO processGET(String requestStr, BufferedReader reader, DataOutputStream dos) throws IOException {
    	ResultDTO resultDTO = new ResultDTO();
    	resultDTO.setRequestUrl(requestStr);
    	String params = null;
    	User user = null;
    	String tempStr = requestStr;
    	
    	while(!tempStr.equals("")) {
    		tempStr = reader.readLine();
    		log.debug("header : {}", tempStr);
    	}
    	
    	int reqStrIndex = requestStr.indexOf("?");
    	
    	if(reqStrIndex != -1) {
        	resultDTO.setRequestUrl(requestStr.substring(0,reqStrIndex));
           	params = requestStr.substring(reqStrIndex+1);
    	}
    	
    	//요청 경로가 /user/create이라면 User객체에 요청 정보 저장
        if(resultDTO.getRequestUrl().equals("/user/create")) {
        	//요청 파라미터 파싱
        	Map<String,String> reqParamInfo = HttpRequestUtils.parseQueryString(params);
        	
            //User클래스에 저장
            user = mapUserInfo(reqParamInfo);
            log.info(user.toString());
            resultDTO.setJspFilePath("/index.html");
            resultDTO.setResultCode("100");
        }else if(resultDTO.getRequestUrl().equals("/index.html")) {
        	resultDTO.setJspFilePath("/index.html");
            resultDTO.setResultCode("100");
        }else if(resultDTO.getRequestUrl().equals("/user/login.html")) {
        	resultDTO.setJspFilePath("/user/login.html");
            resultDTO.setResultCode("100");
        }else if(resultDTO.getRequestUrl().equals("/user/form.html")) {
        	resultDTO.setJspFilePath("/user/form.html");
            resultDTO.setResultCode("100");
        }
        return resultDTO;
    }
    
    //POST방식 처리 메서드
    private ResultDTO processPOST(String requestStr, BufferedReader reader, DataOutputStream dos) throws IOException {
    	//content-length의 값 추출해야함
    	int contentLength = 0;
    	ResultDTO resultDTO = new ResultDTO();
    	resultDTO.setRequestUrl(requestStr);
    	String tempStr = requestStr;
    	
    	//Header 정보 
    	while(!tempStr.equals("")) {
    		tempStr = reader.readLine();
    		
    		//Header가 마지막이라면
    		if(tempStr.isEmpty()) {
    			break;
    		}
    		log.debug("header : {}", tempStr);
    		
    		Pair keyValue = HttpRequestUtils.parseHeader(tempStr);
    		if(keyValue.getKey().equals("Content-Length")) {
    			contentLength = Integer.parseInt(keyValue.getValue());
    		}
    	}
    	
    	Map<String,String> reqParamInfo = getBodyInfo(reader,contentLength);
    	
    	//요청 경로가 /user/create이라면 User객체에 요청 정보 저장
    	if(resultDTO.getRequestUrl().equals("/user/create")) {
        	 //User클래스에 저장
            User user = mapUserInfo(reqParamInfo);
            log.info(user.toString());
            DataBase.addUser(user);
            resultDTO.setResultCode("100");
            resultDTO.setJspFilePath("/index.html");
    	}else if(resultDTO.getRequestUrl().equals("/user/login")) {
    		User loginUser = DataBase.findUserById(reqParamInfo.get("userId"));
    		//회원가입이 되어있다면 
    		if(loginUser != null) {
    			resultDTO.setResultCode("100");
    			resultDTO.setJspFilePath("/index.html");
    		}else {
    			resultDTO.setResultCode("-100");
    			resultDTO.setJspFilePath("/user/login_failed.html");
    		}
    	}
    	return resultDTO;
    }
    
    private Map<String,String> getBodyInfo(BufferedReader reader, int contentLength) throws IOException {
    	String bodyInfo = IOUtils.readData(reader, contentLength);
		log.debug("body: {}", bodyInfo);
		return HttpRequestUtils.parseQueryString(bodyInfo);
    }
   
}























