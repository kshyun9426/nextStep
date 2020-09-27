package http;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;


public class RequestLine {
	
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
	private HttpMethod method;
	
	private String path;
	private Map<String,String> params = new HashMap<String,String>();
	
	public RequestLine(String requestLine) {
		log.debug("request line : {}", requestLine);
		String[] tokens = requestLine.split(" ");
		if(tokens.length != 3) {
			throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
		}
		/*
		 * POST와 같이 하드코딩 되어 있는 값을 변경하기
		 * 
		 * 상수 값이 서로 연관되어 있는 경우 자바의 enum을 쓰기 적합한 곳이다. 독립적으로 존재하는 상수 값은 굳이 enum으로 추가할 필요는 없지만 남자(M),여자(F) 또는
		 * 북쪽(NORTH), 남쪽(SOUTH), 서쪽(WEST), 동쪽(EAST)와 같이 상수 값이 연관성을 가지는 경우 enum을 사용하기 적합하다. 
		 */
		method = HttpMethod.valueOf(tokens[0]);
		if(method.isPost()) {
			path = tokens[1];
			return;
		}
		
		int index = tokens[1].indexOf("?");
		if(index == -1) {
			path = tokens[1];
		}else {
			path = tokens[1].substring(0, index);
			params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
		}
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public Map<String,String> getParams() {
		return params;
	}
}
