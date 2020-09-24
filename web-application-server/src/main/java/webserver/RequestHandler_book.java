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
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler_book extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler_book.class);

    private Socket connection;
    private HttpRequest_book request;
    private HttpResponse_book response;
    
    public RequestHandler_book(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	this.request = new HttpRequest_book(in);
        	this.response = new HttpResponse_book(out);
        	String path = getDefaultPath(request.getPath());
        	
        	if(("/user/create").contentEquals(path)) {
        		User user = new User(
        				request.getParameter("userId")
        				, request.getParameter("password")
        				, request.getParameter("name")
        				, request.getParameter("email"));
        		log.debug("User : {}", user);
        		DataBase.addUser(user);
        		response.sendRedirect("/index.html");
        	}else if(("/user/login").contentEquals(path)) {
        		User user = DataBase.findUserById(request.getParameter("userId"));
        		if(user != null) {
        			if(user.login(request.getParameter("password"))) {
        				response.addHeader("Set-Cookie", "logined=true");
        				response.sendRedirect("/index.html");
        			}else {
        				response.sendRedirect("/user/login_failed.html");
        			}
        		} else {
        			response.sendRedirect("/user/login_failed.html");
        		}
        	}else if("/user/list".contentEquals(path)) {
        		if(!isLogin(request.getHeader("Cookie"))) {
        			response.sendRedirect("/user/login.html");
        			return;
        		}
        		Collection<User> users = DataBase.findAll();
        		StringBuilder sb = new StringBuilder();
        		sb.append("<table border='1'>");
        		for(User user : users) {
        			sb.append("<tr>");
        			sb.append("<td>" + user.getUserId() + "</td>");
        			sb.append("<td>" + user.getName() + "</td>");
        			sb.append("<td>" + user.getEmail() + "</td>");
        			sb.append("</tr>");
        		}
        		sb.append("</table>");
        		response.forwardBody(sb.toString());
        	}else {
        		response.forward(path);
        	}
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean isLogin(String cookieValue) {
    	Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
    	String value = cookies.get("logined");
    	if(value == null) {
    		return false;
    	}
    	return Boolean.parseBoolean(value);
    }
    
    private String getDefaultPath(String path) {
    	if(path.contentEquals("/")) {
    		return "/index.html";
    	}
    	return path;
    }
   
}























