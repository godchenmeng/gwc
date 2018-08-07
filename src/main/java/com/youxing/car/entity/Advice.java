package com.youxing.car.entity;

import java.util.Date;

/**   
 * @author mars   
 * @date 2017年5月18日 下午1:57:19 
 */
public class Advice {

	private Long id;
	private Long createdby;
	private String content;
	private Date createdate;
	private String radio;
	
	
	public String getRadio() {
		return radio;
	}
	public void setRadio(String radio) {
		this.radio = radio;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCreatedby() {
		return createdby;
	}
	public void setCreatedby(Long createdby) {
		this.createdby = createdby;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
}


