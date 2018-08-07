package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

public class CarWarn {

	@ApiModelProperty(value = "设备编号")
	private String device;

	@ApiModelProperty(value = "报警地点")
	private String place;

	@ApiModelProperty(value = "限速")
	private String speedlimit;

	@ApiModelProperty(value = "报警描述")
	private String dec;

	@ApiModelProperty(value = "车速")
	private String speed;

	@ApiModelProperty(value = "时间")
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSpeedlimit() {
		return speedlimit;
	}

	public void setSpeedlimit(String speedlimit) {
		this.speedlimit = speedlimit;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDec() {
		return dec;
	}

	public void setDec(String dec) {
		this.dec = dec;
	}
}
