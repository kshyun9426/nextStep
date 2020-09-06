package model;

public class HeaderInfoDTO {

	private String requestMethod;
	private String requestUrl;
	
	public String getRequestMethod() {
		return requestMethod;
	}
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
	@Override
	public String toString() {
		return "requestMethod: " + requestMethod + ", requestUrl: " + requestUrl;
	}
}
