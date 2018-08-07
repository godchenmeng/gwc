package com.youxing.car.entity;

public class ROCRelation {
	
	private Long id;
	private Long region;
	private Long org;
	private Long car_id;
	private String type;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRegion() {
		return region;
	}
	public void setRegion(Long region) {
		this.region = region;
	}
	public Long getOrg() {
		return org;
	}
	public void setOrg(Long org) {
		this.org = org;
	}
	public Long getCar_id() {
		return car_id;
	}
	public void setCar_id(Long car_id) {
		this.car_id = car_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
