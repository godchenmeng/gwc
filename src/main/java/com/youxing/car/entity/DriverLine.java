package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * 驾驶路线
 * @author Administrator
 *
 */
public class DriverLine {
	

	private String startAddr;
	private String endAddr;
	//是否跨天行驶 1是有 0是否
	private long timeStamp;
	
	
	public String getStartAddr() {
		return startAddr;
	}
	public void setStartAddr(String startAddr) {
		this.startAddr = startAddr;
	}
	public String getEndAddr() {
		return endAddr;
	}
	public void setEndAddr(String endAddr) {
		this.endAddr = endAddr;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	@ApiModelProperty(value="设备编号")
	private String device;
	@ApiModelProperty(value="起始时间")
	private String start;
	@ApiModelProperty(value="结束时间")
	private String end;
	@ApiModelProperty(value="停留时间分钟")
	private long stopTime;
	@ApiModelProperty(value="行驶里程 kM")
	private float mileage;
	@ApiModelProperty(value="耗油 L")
	private float fuel;
	@ApiModelProperty(value="起始地点")
	private String start_name;
	@ApiModelProperty(value="结束地点")
	private String end_name;
	//是否跨天行驶 1是有 0是否
	private String type;
	
	@ApiModelProperty(value="行驶时间")
	private String runtime;
	
	@ApiModelProperty(value="百度经度")
	private Double baiduLatitude;
	
	@ApiModelProperty(value="百度纬度")
	private Double baiduLongitude;

	//距离上次行程结束时间
	private Long lastdate;

	public Long getLastdate() {
		return lastdate;
	}

	public void setLastdate(Long lastdate) {
		this.lastdate = lastdate;
	}

	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public long getStopTime() {
		return stopTime;
	}
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
	public float getMileage() {
		return mileage;
	}
	public void setMileage(float mileage) {
		this.mileage = mileage;
	}
	public String getStart_name() {
		return start_name;
	}
	public void setStart_name(String start_name) {
		this.start_name = start_name;
	}
	public String getEnd_name() {
		return end_name;
	}
	public void setEnd_name(String end_name) {
		this.end_name = end_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float getFuel() {
		return fuel;
	}
	public void setFuel(float fuel) {
		this.fuel = fuel;
	}
	public Double getBaiduLatitude() {
		return baiduLatitude;
	}
	public void setBaiduLatitude(Double baiduLatitude) {
		this.baiduLatitude = baiduLatitude;
	}
	public Double getBaiduLongitude() {
		return baiduLongitude;
	}
	public void setBaiduLongitude(Double baiduLongitude) {
		this.baiduLongitude = baiduLongitude;
	}
	
}
