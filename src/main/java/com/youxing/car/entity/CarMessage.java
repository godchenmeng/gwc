package com.youxing.car.entity;

import java.util.Date;

/**
 * @author mars
 * @date 2017年6月22日 下午5:09:16
 */
public class CarMessage {

	private String acc;
	private Date senddate;
	private Double latitude;
	private Double longitude;
	private Integer speed = 0;
	private Double direction;
	private String device;
	private String car_no;
	private String car_org;
	private Long car_mileage;
	private Integer code = 0;
	private String orgName;
	private String driverName;
	private String send;
	private String loaction;
	private String avg;
	private Long fuel;
	private Double dianya;
	private Integer engine_speed;
	private Integer coolant;
	private Integer car_tempu;
	private Integer car_speed;
	private String car_type;
	private String type;
	private Long id;//车辆id
	private Long car_id;//车辆id
	

	private Double total_oil;//总油量
	private Double remain_oil;//剩余油量
	private String turn_state;//罐体旋转状态      0不转   1正转   2反转
	private Double fuel_level;
	private Double water_level;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public Double getDianya() {
		return dianya;
	}

	public void setDianya(Double dianya) {
		this.dianya = dianya;
	}

	public Integer getEngine_speed() {
		return engine_speed;
	}

	public void setEngine_speed(Integer engine_speed) {
		this.engine_speed = engine_speed;
	}

	public Integer getCoolant() {
		return coolant;
	}

	public void setCoolant(Integer coolant) {
		this.coolant = coolant;
	}

	public Integer getCar_tempu() {
		return car_tempu;
	}

	public void setCar_tempu(Integer car_tempu) {
		this.car_tempu = car_tempu;
	}

	public Long getFuel() {
		return fuel;
	}

	public void setFuel(Long fuel) {
		this.fuel = fuel;
	}

	public String getAvg() {
		return avg;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}

	public String getSend() {
		return send;
	}

	public void setSend(String send) {
		this.send = send;
	}

	public String getLoaction() {
		return loaction;
	}

	public void setLoaction(String loaction) {
		this.loaction = loaction;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Long getCar_mileage() {
		return car_mileage;
	}

	public void setCar_mileage(Long car_mileage) {
		this.car_mileage = car_mileage;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public Date getSenddate() {
		return senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
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

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Double getDirection() {
		return direction;
	}

	public void setDirection(Double direction) {
		this.direction = direction;
	}

	public String getCar_no() {
		return car_no;
	}

	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}

	public String getCar_org() {
		return car_org;
	}

	public void setCar_org(String car_org) {
		this.car_org = car_org;
	}

	public Integer getCar_speed() {
		return car_speed;
	}

	public void setCar_speed(Integer car_speed) {
		this.car_speed = car_speed;
	}

	public Long getCar_id() {
		return car_id;
	}

	public void setCar_id(Long car_id) {
		this.car_id = car_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getTotal_oil() {
		return total_oil;
	}

	public void setTotal_oil(Double total_oil) {
		this.total_oil = total_oil;
	}

	public Double getRemain_oil() {
		return remain_oil;
	}

	public void setRemain_oil(Double remain_oil) {
		this.remain_oil = remain_oil;
	}

	public String getTurn_state() {
		return turn_state;
	}

	public void setTurn_state(String turn_state) {
		this.turn_state = turn_state;
	}

	public Double getFuel_level() {
		return fuel_level;
	}

	public void setFuel_level(Double fuel_level) {
		this.fuel_level = fuel_level;
	}

	public Double getWater_level() {
		return water_level;
	}

	public void setWater_level(Double water_level) {
		this.water_level = water_level;
	}
	
}
