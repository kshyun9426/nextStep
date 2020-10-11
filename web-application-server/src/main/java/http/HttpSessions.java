package http;

/*
 * 서버 측에서 모든 클라이언트의 세션 값을 관리하는 저장소 클래스
 */
public class HttpSessions {
	
	//현재 세션에 할당되어 있는 고유한 세션 아이디를 반환
	public String getId() {
			
	}
		
	//현재 세션에 value인자로 전달되는 객체를 name인자 이름으로 저장
	public void setAttribute(String name, Object value) {
		
	}
	
	//현재 세션에 name인자로 저장되어 있는 객체값을 찾아 반환
	public Object getAttribute(String name) {
		
	}
	
	//현재 세션에 name인자로 저장되어 있는 객체 값을 삭제
	public void removeAttribute(String name) {
		
	}
	
	//현재 세션에 저장되어 있는 모든 값을 삭제
	public void invalidate() {
		
	}
	
}
