package model;

public class ResultDTO {
	
	private String requestUrl;
	
	private String resultCode;
	
	private String jspFilePath;

	
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getJspFilePath() {
		return jspFilePath;
	}

	public void setJspFilePath(String jspFilePath) {
		this.jspFilePath = jspFilePath;
	}
	
	
}
