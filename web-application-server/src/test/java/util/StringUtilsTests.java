package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import model.User;

public class StringUtilsTests {
	
	@Test
	public void 문자열_split_테스트() {
		String str = "GET /index.html HTTP/1.1";
		String[] tokens = str.split(" ");
		
		assertThat(tokens[1], is("/index.html"));
	}
	
	@Test
	public void 문자열_subString_테스트() {
		
		String str = "GET /user/create?userId=kshyun&password=tmdgus&name=seunghyun&email=ksh94%40naver.com HTTP/1.1";
		String[] tokens = str.split(" ");
		int reqStrIndex = tokens[1].indexOf("?");
    	String reqUrl = tokens[1].substring(0,reqStrIndex);
    	String reqParameter = tokens[1].substring(reqStrIndex+1);
    	
    	assertThat(reqUrl, is("/user/create"));
    	assertThat(reqParameter, is("userId=kshyun&password=tmdgus&name=seunghyun&email=ksh94%40naver.com"));
	}
	
	@Test
	public void User클래스에_요청파라미터_매핑해서_저장_테스트() {
		String params = "userId=kshyun&password=tmdgus&name=seunghyun&email=ksh94%40naver.com";
		Map<String,String> reqUserInfo = HttpRequestUtils.parseQueryString(params);
		User user = new User(reqUserInfo.get("userId"), reqUserInfo.get("password"), reqUserInfo.get("name"), reqUserInfo.get("email"));
		
		assertThat(user.getUserId(), is("kshyun"));
		assertThat(user.getPassword(), is("tmdgus"));
		assertThat(user.getName(), is("seunghyun"));
		assertThat(user.getEmail(), is("ksh94%40naver.com"));
	}
}





















