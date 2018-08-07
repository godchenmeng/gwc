package com.youxing.car.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SpeedingInfo  implements Serializable{
	private Double speeding_distance;
	private Long start_time;
	private Double start_latitude;
	private Double start_longitude;
	private Long end_time;
	private Double end_latitude;
	private Double end_longitude;
	private Integer car_speed;
	private Integer limit_speed;
	public Double getSpeeding_distance() {
		return speeding_distance;
	}
	public void setSpeeding_distance(Double speeding_distance) {
		this.speeding_distance = speeding_distance;
	}
	public Long getStart_time() {
		return start_time;
	}
	public void setStart_time(Long start_time) {
		this.start_time = start_time;
	}
	public Double getStart_latitude() {
		return start_latitude;
	}
	public void setStart_latitude(Double start_latitude) {
		this.start_latitude = start_latitude;
	}
	public Double getStart_longitude() {
		return start_longitude;
	}
	public void setStart_longitude(Double start_longitude) {
		this.start_longitude = start_longitude;
	}
	public Long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}
	public Double getEnd_latitude() {
		return end_latitude;
	}
	public void setEnd_latitude(Double end_latitude) {
		this.end_latitude = end_latitude;
	}
	public Double getEnd_longitude() {
		return end_longitude;
	}
	public void setEnd_longitude(Double end_longitude) {
		this.end_longitude = end_longitude;
	}
	public Integer getCar_speed() {
		return car_speed;
	}
	public void setCar_speed(Integer car_speed) {
		this.car_speed = car_speed;
	}
	public Integer getLimit_speed() {
		return limit_speed;
	}
	public void setLimit_speed(Integer limit_speed) {
		this.limit_speed = limit_speed;
	}

	
}
