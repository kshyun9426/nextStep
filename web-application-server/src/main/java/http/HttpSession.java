package http;

import java.util.HashMap;
import java.util.Map;

/*
 * 
 * 클라이언트 별 세션 데이터를 관리 할 수 있는 클래스
 */
public class HttpSession {
	
	private Map<String, Object> stateData;
	
	public HttpSession() {
		this.stateData = new HashMap<>();
	}
	
	

}
