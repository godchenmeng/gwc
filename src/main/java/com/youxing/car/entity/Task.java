package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class Task implements Serializable{
	private Long id;
	private String start;
	private String end;
	private Long start_mileage;
	private Long end_mileage;
	private Long mileage;
	private Long obd_mileage;
	private Double cost;
	private Long createdby;
	private String createdate;
	private Long conid;
	private Long driver;
	private String status;
	private String result;
	private String eva;
	
	
	public String getEva() {
		return eva;
	}
	public void setEva(String eva) {
		this.eva = eva;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getConid() {
		return conid;
	}
	public void setConid(Long conid) {
		this.conid = conid;
	}
	public Long getDriver() {
		return driver;
	}
	public void setDriver(Long driver) {
		this.driver = driver;
	}
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getStart(){
	    return start;
	}
	public void setStart(String start){
	    this.start = start;
	}
	public String getEnd(){
	    return end;
	}
	public void setEnd(String end){
	    this.end = end;
	}
	public Long getStart_mileage(){
	    return start_mileage;
	}
	public void setStart_mileage(Long start_mileage){
	    this.start_mileage = start_mileage;
	}
	public Long getEnd_mileage(){
	    return end_mileage;
	}
	public void setEnd_mileage(Long end_mileage){
	    this.end_mileage = end_mileage;
	}
	public Long getMileage(){
	    return mileage;
	}
	public void setMileage(Long mileage){
	    this.mileage = mileage;
	}
	public Long getObd_mileage(){
	    return obd_mileage;
	}
	public void setObd_mileage(Long obd_mileage){
	    this.obd_mileage = obd_mileage;
	}
	public Double getCost(){
	    return cost;
	}
	public void setCost(Double cost){
	    this.cost = cost;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public String getCreatedate(){
		if(createdate!=null&&createdate.endsWith(".0"))
			return createdate.substring(0, createdate.length()-2);
	    return createdate;
	}
	public void setCreatedate(String createdate){
	    this.createdate = createdate;
	}

}
