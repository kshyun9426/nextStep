package http;

public enum HttpMethod {
	GET,
	POST;
	
	//현재 자신의 상태가 POST인지 여부를 판단하는 메서드
	public boolean isPost() {
		return this == POST;
	}
}
