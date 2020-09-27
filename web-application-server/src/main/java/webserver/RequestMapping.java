package webserver;

import java.util.HashMap;
import java.util.Map;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

/*
 * 웹 애플리케이션에서 서비스하는 모든 URI과 Controller를 관리하고 있으며, 
 * 요청 URL에 해당하는 Controller를 반환하는 역할을 한다. 
 */
public class RequestMapping {
	
	private static Map<String,Controller> controllers = new HashMap<String, Controller>();
	
	static {
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/login", new LoginController());
		controllers.put("/user/list", new ListUserController());
	}
	
	public static Controller getController(String requestUrl) {
		return controllers.get(requestUrl);
	}
	
}
