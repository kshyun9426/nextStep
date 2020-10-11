package http;

import java.util.HashMap;
import java.util.Map;

/*
 * 서버 측에서 모든 클라이언트의 세션 값을 관리하는 저장소 클래스
 */
public class HttpSessions {
	
	private static Map<String,HttpSession> sessions = new HashMap<>();
	
	public static HttpSession getSession(String id) {
		
		HttpSession session = sessions.get(id);
		
		if(session == null) {
			session = new HttpSession(id);
			sessions.put(id, session);
			return session;
		}
		return session;
	}
	
	static void remove(String id) {
		sessions.remove(id);
	}
}
