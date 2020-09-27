package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest_book;
import http.HttpResponse_book;
import model.User;

public class CreateUserController extends AbstractController {
	
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

	@Override
	public void doPost(HttpRequest_book request, HttpResponse_book response) {
		User user = new User(
				request.getParameter("userId")
				, request.getParameter("password")
				, request.getParameter("name")
				, request.getParameter("email"));
		log.debug("User : {}", user);
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}
}
