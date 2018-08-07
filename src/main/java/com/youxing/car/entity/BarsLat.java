package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class BarsLat implements Serializable{
	private Long id;
	private Long barid;
	private Double lat;
	private Double lon;

	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public Long getBarid(){
	    return barid;
	}
	public void setBarid(Long barid){
	    this.barid = barid;
	}
	public Double getLat(){
	    return lat;
	}
	public void setLat(Double lat){
	    this.lat = lat;
	}
	public Double getLon(){
	    return lon;
	}
	public void setLon(Double lon){
	    this.lon = lon;
	}

}
