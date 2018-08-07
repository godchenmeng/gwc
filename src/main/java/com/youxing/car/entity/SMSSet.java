package com.youxing.car.entity;

public class SMSSet {
	private Long id;
	private String  sendtarget;
	private String sendphones;
	private String sendname;
	private String types;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSendtarget() {
		return sendtarget;
	}
	public void setSendtarget(String sendtarget) {
		this.sendtarget = sendtarget;
	}
	public String getSendphones() {
		return sendphones;
	}
	public void setSendphones(String sendphones) {
		this.sendphones = sendphones;
	}
	public String getSendname() {
		return sendname;
	}
	public void setSendname(String sendname) {
		this.sendname = sendname;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}

}
