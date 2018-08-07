package com.youxing.car.response;

public class FailedResponse implements Response {
	private String responseCode;
	private String responseMsg;

	public FailedResponse() {
		this.responseCode = "0";
		this.responseMsg = "failed";
	}

	public FailedResponse(String responseCode, String responseMsg) {
		this.responseCode = responseCode;
		this.responseMsg = responseMsg;
	}

	public FailedResponse(String responseMsg) {
		this.responseCode = "0";
		this.responseMsg = responseMsg;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public String getResponseMsg() {
		return this.responseMsg;
	}
}
