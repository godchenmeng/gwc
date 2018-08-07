package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
@SuppressWarnings("serial")
public class TroubleCode implements Serializable{
	private Long id;
	private String device;
	private String serial;
	private String code;
	private String type;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createdate;

	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getDevice(){
	    return device;
	}
	public void setDevice(String device){
	    this.device = device;
	}
	public String getSerial(){
	    return serial;
	}
	public void setSerial(String serial){
	    this.serial = serial;
	}
	public String getCode(){
	    return code;
	}
	public void setCode(String code){
	    this.code = code;
	}
	public String getType(){
	    return type;
	}
	public void setType(String type){
	    this.type = type;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}

}
