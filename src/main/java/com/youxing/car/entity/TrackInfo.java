package com.youxing.car.entity;
import java.io.Serializable;

@SuppressWarnings("serial")
public class TrackInfo implements Serializable {
	private double latitude;
	private Long loc_time;
	private double longitude;
	private String create_time;
	private double direction;
	private double fuel;
	private double mileage;
	private int speed;
	private String car_type;
	private String device;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public Long getLoc_time() {
		return loc_time;
	}
	public void setLoc_time(Long loc_time) {
		this.loc_time = loc_time;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public double getDirection() {
		return direction;
	}
	public void setDirection(double direction) {
		this.direction = direction;
	}
	public double getFuel() {
		return fuel;
	}
	public void setFuel(double fuel) {
		this.fuel = fuel;
	}
	public double getMileage() {
		return mileage;
	}
	public void setMileage(double mileage) {
		this.mileage = mileage;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
} 