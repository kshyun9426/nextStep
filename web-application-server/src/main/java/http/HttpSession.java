package http;

import java.util.HashMap;
import java.util.Map;

/*
 * 클라이언트 별 세션 데이터를 관리 할 수 있는 클래스
 */
public class HttpSession {
	
	private Map<String, Object> values = new HashMap<String,Object>();
	
	//클라이언트의 고유 ID
	private String id;
	
	public HttpSession(String id) {
		this.id = id;
	}
	
	//현재 세션에 할당되어 있는 고유한 세션 아이디를 반환
	public String getId() {
		return id;
	}
		
	//현재 세션에 value인자로 전달되는 객체를 name인자 이름으로 저장
	public void setAttribute(String name, Object value) {
		values.put(name,value);
	}
	
	//현재 세션에 name인자로 저장되어 있는 객체값을 찾아 반환
	public Object getAttribute(String name) {
		return values.get(name);
	}
	
	//현재 세션에 name인자로 저장되어 있는 객체 값을 삭제
	public void removeAttribute(String name) {
		values.remove(name);
	}
	
	//현재 세션에 저장되어 있는 모든 값을 삭제
	public void invalidate() {
		HttpSessions.remove(id);
	}
}
