package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;
@SuppressWarnings("serial")
public class RolePermission implements Serializable{
	private Long id;
	private Long rid;
	private Long pid;
	private Long createdby;
	private Date createdate;

	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public Long getRid(){
	    return rid;
	}
	public void setRid(Long rid){
	    this.rid = rid;
	}
	public Long getPid(){
	    return pid;
	}
	public void setPid(Long pid){
	    this.pid = pid;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}

}
