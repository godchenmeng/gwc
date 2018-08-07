package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Evaluate implements Serializable{
	private Long id;
	private String eva;
	private Long createdby;
	private String createdate;
	private Long task_id;

	public Long getId(){
	    return id;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getEva(){
	    return eva;
	}
	public void setEva(String eva){
	    this.eva = eva;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public String getCreatedate(){
		if(createdate!=null&&createdate.equals(".0"))
			return createdate.substring(0, createdate.length()-2);
		return createdate;
	}
	public void setCreatedate(String createdate){
	    this.createdate = createdate;
	}

}
