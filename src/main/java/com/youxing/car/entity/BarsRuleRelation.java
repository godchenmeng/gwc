package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class BarsRuleRelation implements Serializable{
	
	private Long id;
	private Long ruleid;
	private Long carid;
	private Long fenceid;
	private String monitored_status;
	private Car car;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRuleid() {
		return ruleid;
	}
	public void setRuleid(Long ruleid) {
		this.ruleid = ruleid;
	}
	public Long getCarid() {
		return carid;
	}
	public void setCarid(Long carid) {
		this.carid = carid;
	}
	public Long getFenceid() {
		return fenceid;
	}
	public void setFence_id(Long fenceid) {
		this.fenceid = fenceid;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public String getMonitored_status() {
		return monitored_status;
	}
	public void setMonitored_status(String monitored_status) {
		this.monitored_status = monitored_status;
	}
}
