package controller;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest_book;
import http.HttpResponse_book;
import http.HttpSession;
import model.User;
import util.HttpRequestUtils;

public class ListUserController extends AbstractController {
	
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
	
	@Override
	public void doGet(HttpRequest_book request, HttpResponse_book response) {
		if(!isLogined(request.getSession())) { 
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
	}
	
	//세션에 추가한 User 정보로 로그인 유무 판단하는 메서드
	private static boolean isLogined(HttpSession session) {
		Object user = session.getAttribute("user");
		if(user == null) {
			return false;
		}
		return true;
	}
	
//	private boolean isLogin(String cookieValue) {
//    	Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
//    	String value = cookies.get("logined");
//    	if(value == null) {
//    		return false;
//    	}
//    	return Boolean.parseBoolean(value);
//    }

}
