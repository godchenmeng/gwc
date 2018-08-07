package com.youxing.car.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;


public class GpsMsg {
	private Double latitude;
	private Double direction;
	private Double longitude;
	private Integer car_speed;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date senddate;
	
	
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
	public Integer getCar_speed() {
		return car_speed;
	}
	public void setCar_speed(Integer car_speed) {
		this.car_speed = car_speed;
	}
	public Double getDirection() {
		return direction;
	}
	public void setDirection(Double direction) {
		this.direction = direction;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
}
