package com.youxing.car.entity;

public class RegionRelation {
	
	private Long id;
	private Long region_id;
	private Double lon;
	private Double lat;
	//点的顺序
	private Integer index;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRegion_id() {
		return region_id;
	}
	public void setRegion_id(Long region_id) {
		this.region_id = region_id;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}

	
	
}
