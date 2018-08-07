package com.youxing.car.entity;

public class SMS {
	
	private Long id;
	private String type;
	private String senddate;
	private String content;
	private String sendtarget;
	private String sendcount;
	private String memberNames;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSenddate() {
		return senddate;
	}
	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendtarget() {
		return sendtarget;
	}
	public void setSendtarget(String sendtarget) {
		this.sendtarget = sendtarget;
	}
	public String getSendcount() {
		return sendcount;
	}
	public void setSendcount(String sendcount) {
		this.sendcount = sendcount;
	}
	public String getMemberNames() {
		return memberNames;
	}
	public void setMemberNames(String memberNames) {
		this.memberNames = memberNames;
	}

}
