package com.youxing.car.entity;

public class SmsRole {
	
	private Long id;
	private String name;
	private String descr;
	private String sendtypes;
	private Long createby;
	private String createdate;
	private String status;
	private Long org;
	private Long value;
	private String text;
	
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getSendtypes() {
		return sendtypes;
	}
	public void setSendtypes(String sendtypes) {
		this.sendtypes = sendtypes;
	}
	public Long getCreateby() {
		return createby;
	}
	public void setCreateby(Long createby) {
		this.createby = createby;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getOrg() {
		return org;
	}
	public void setOrg(Long org) {
		this.org = org;
	}
	
	
	

}
