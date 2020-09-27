package controller;

import http.HttpMethod;
import http.HttpRequest_book;
import http.HttpResponse_book;

/*
 * HTTP메서드(GET, POST)에 따라 다른 처리를 할 수 있도록 만든 추상클래스 
 * 
 * AbstractController 사용 장점은 요청 URL이 같더라도 HTTP메서드가 다른 경우 새로운 Controller클래스를 생성하지 않고 Controller를 하나로 GET,POST를 모두 지원하는것이 가능해진다.
 */
public abstract class AbstractController implements Controller {

	@Override
	public void service(HttpRequest_book request, HttpResponse_book response) {
		HttpMethod method = request.getMethod();
		
		if(method.isPost()) {
			doPost(request, response);
		}else {
			doGet(request, response);
		}
	}
	
	protected void doPost(HttpRequest_book request, HttpResponse_book response) {
		
	}
	
	protected void doGet(HttpRequest_book request, HttpResponse_book response) {
		
	}
}
