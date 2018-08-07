package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;
@SuppressWarnings("serial")
public class Role implements Serializable{
	private Long id;
	private String name;
	private String descr;
	private String status;
	private Long createby;
	private Date createdate;
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
	public Long getOrg() {
		return org;
	}
	public void setOrg(Long org) {
		this.org = org;
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
	public String getDescr(){
	    return descr;
	}
	public void setDescr(String descr){
	    this.descr = descr;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
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

}
