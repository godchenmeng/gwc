package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Device implements Serializable{
	

	private String org;
	
	
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	private Long id;
	private String car_no;
	private String device;
	private String sim;
	private String version;
	private String status;
	private Long createdby;
	private String createdate;
	private String updatedate;
	private Long car_id;
	private String driver_name;
	private String connectway;
	private String remark;

	
	
	
	public String getConnectway() {
		return connectway;
	}
	public void setConnectway(String connectway) {
		this.connectway = connectway;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getCar_no(){
	    return car_no;
	}
	public void setCar_no(String car_no){
	    this.car_no = car_no;
	}
	public String getDevice(){
	    return device;
	}
	public void setDevice(String device){
	    this.device = device;
	}
	public String getSim(){
	    return sim;
	}
	public void setSim(String sim){
	    this.sim = sim;
	}
	public String getVersion(){
	    return version;
	}
	public void setVersion(String version){
	    this.version = version;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public String getCreatedate(){
		if(createdate!=null&&createdate.endsWith(".0"))
			return createdate.substring(0,  createdate.length()-2);
	    return createdate;
	}
	public void setCreatedate(String createdate){
	    this.createdate = createdate;
	}
	public String getUpdatedate(){
		if(updatedate!=null&&updatedate.endsWith(".0"))
			return updatedate.substring(0, updatedate.length()-2);	    
		return updatedate;
	}
	public void setUpdatedate(String updatedate){
	    this.updatedate = updatedate;
	}
	public Long getCar_id(){
	    return car_id;
	}
	public void setCar_id(Long car_id){
	    this.car_id = car_id;
	}

}
