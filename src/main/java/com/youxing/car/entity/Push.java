package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
@SuppressWarnings("serial")
public class Push implements Serializable{
	private Long id;
	@ApiModelProperty(value = "消息类型1-审批通过给申请人的消息 2-调度通过给司机的消息")
	private String type;
	@ApiModelProperty(value = "内容")
	private String content;
	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createdate;
	private Long push;
	private String read;
	@ApiModelProperty(value = "标题")
	private String title;
	@ApiModelProperty(value = "1-通过 2-未通过")
	private String pass;
	
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getType(){
	    return type;
	}
	public void setType(String type){
	    this.type = type;
	}
	public String getContent(){
	    return content;
	}
	public void setContent(String content){
	    this.content = content;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}
	public Long getPush(){
	    return push;
	}
	public void setPush(Long push){
	    this.push = push;
	}

}
