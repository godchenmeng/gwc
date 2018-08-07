package com.youxing.car.response;

public class ObjectResponse<T> implements Response {
	private String responseCode;
	private String responseMsg;
	private T datas;

	public ObjectResponse(String responseCode, String responseMsg, T datas) {
		this.responseCode = responseCode;
		this.responseMsg = responseMsg;
		this.datas = datas;
	}

	public ObjectResponse(T datas) {
		this.responseCode = "1";
		this.responseMsg = "success";
		this.datas = datas;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public String getResponseMsg() {
		return this.responseMsg;
	}

	public T getDatas() {
		return this.datas;
	}

	public void setDatas(T datas) {
		this.datas = datas;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String toString() {
		return "ListResponse [responseCode=" + this.responseCode + ", responseMsg=" + this.responseMsg + ", datas=" + this.datas + "]";
	}
}