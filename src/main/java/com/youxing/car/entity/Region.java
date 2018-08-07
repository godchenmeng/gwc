package com.youxing.car.entity;

import java.util.List;

public class Region {
	
	private Long id;
	private String name;
	private String type;
	private String drawing;
	private String status;
	private String createdate;
	private String createbd;
	private Double radius;
	private Long org;
	
	
	private List<RegionRelation> regionRelation;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Double getRadius() {
		return radius;
	}
	public void setRadius(Double radius) {
		this.radius = radius;
	}
	public String getDrawing() {
		return drawing;
	}
	public void setDrawing(String drawing) {
		this.drawing = drawing;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getCreatebd() {
		return createbd;
	}
	public void setCreatebd(String createbd) {
		this.createbd = createbd;
	}
	public List<RegionRelation> getRegionRelation() {
		return regionRelation;
	}
	public void setRegionRelation(List<RegionRelation> regionRelation) {
		this.regionRelation = regionRelation;
	}
	public Long getOrg() {
		return org;
	}
	public void setOrg(Long org) {
		this.org = org;
	}

	
}
