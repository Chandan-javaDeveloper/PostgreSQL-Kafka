package com.jne.response;

public class UIResponse {
	private boolean result;
	private Object data;
	private String apiKey;
	private String message;
	//private String code;
	
	public UIResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UIResponse(boolean result, Object data, String apiKey, String message) {
		super();
		this.result = result;
		this.data = data;
		this.apiKey = apiKey;
		this.message = message;
		//this.code = code;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}

	@Override
	public String toString() {
		return "UIResponse [result=" + result + ", data=" + data + ", apiKey=" + apiKey + ", message=" + message
				+ "]";
	}
	
	
	
	
}
