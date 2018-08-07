package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;
@SuppressWarnings("serial")
public class Member implements Serializable{
	private Long id;
	private String name;
	private String sex;
	private String card;
	private String mobile;
	private Date birth;
	private String phone;
	private Long org;
	private String status;
	private String driver;
	private Long createby;
	private Date createdate;
	private String code;
	private String oname;
	private String parent;
	private String rname;
	private String sms_type;

	public String getSms_type() {
		return sms_type;
	}

	public void setSms_type(String sms_type) {
		this.sms_type = sms_type;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getOname() {
		return oname;
	}
	public void setOname(String oname) {
		this.oname = oname;
	}
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getName(){
	    return name;
	}
	public void setName(String name){
	    this.name = name;
	}
	public String getSex(){
	    return sex;
	}
	public void setSex(String sex){
	    this.sex = sex;
	}
	public String getCard(){
	    return card;
	}
	public void setCard(String card){
	    this.card = card;
	}
	public String getMobile(){
	    return mobile;
	}
	public void setMobile(String mobile){
	    this.mobile = mobile;
	}
	public Date getBirth(){
	    return birth;
	}
	public void setBirth(Date birth){
	    this.birth = birth;
	}
	public String getPhone(){
	    return phone;
	}
	public void setPhone(String phone){
	    this.phone = phone;
	}
	public Long getOrg(){
	    return org;
	}
	public void setOrg(Long org){
	    this.org = org;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public String getDriver(){
	    return driver;
	}
	public void setDriver(String driver){
	    this.driver = driver;
	}
	public Long getCreateby(){
	    return createby;
	}
	public void setCreateby(Long createby){
	    this.createby = createby;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}
	public String getCode(){
	    return code;
	}
	public void setCode(String code){
	    this.code = code;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	
	
}
