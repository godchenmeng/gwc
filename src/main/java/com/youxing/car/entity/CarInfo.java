package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class CarInfo implements Serializable{
	private String id;
	private String device;
	private Double dianya;
	private Long mileage;
	private Long fuel;
	private Integer engine_speed;
	private Integer car_speed;
	private Double sky_flow;
	private Integer inlet;
	private Integer inlet_pass;
	private String fault_status;
	private Integer engine_fault;
	private Integer coolant;
	private Integer car_tempu;
	private Integer oil_pass;
	private Integer air_pass;
	private Double gas_position;
	private Integer engine_time;
	private String innage;
	private Integer engine_load;
	private Double long_fuel;
	private Double angle_engine;
	private Long car_mileage;
	private String senddate;
	private Double avgful;
	private String createdate;
	private Double air_sensor;
	
	
	public Double getAir_sensor() {
		return air_sensor;
	}
	public void setAir_sensor(Double air_sensor) {
		this.air_sensor = air_sensor;
	}
	public Double getAvgful() {
		return avgful;
	}
	public void setAvgful(Double avgful) {
		this.avgful = avgful;
	}
	public String getId(){
	    return id;
	}
	public void setId(String id){
	    this.id = id;
	}
	public String getDevice(){
	    return device;
	}
	public void setDevice(String device){
	    this.device = device;
	}
	public Double getDianya() {
		return dianya;
	}
	public void setDianya(Double dianya) {
		this.dianya = dianya;
	}
	public Long getMileage() {
		return mileage;
	}
	public void setMileage(Long mileage) {
		this.mileage = mileage;
	}
	public Long getFuel() {
		return fuel;
	}
	public void setFuel(Long fuel) {
		this.fuel = fuel;
	}
	public Integer getEngine_speed() {
		return engine_speed;
	}
	public void setEngine_speed(Integer engine_speed) {
		this.engine_speed = engine_speed;
	}
	public Integer getCar_speed() {
		return car_speed;
	}
	public void setCar_speed(Integer car_speed) {
		this.car_speed = car_speed;
	}
	public Double getSky_flow() {
		return sky_flow;
	}
	public void setSky_flow(Double sky_flow) {
		this.sky_flow = sky_flow;
	}
	public Integer getInlet() {
		return inlet;
	}
	public void setInlet(Integer inlet) {
		this.inlet = inlet;
	}
	public Integer getInlet_pass() {
		return inlet_pass;
	}
	public void setInlet_pass(Integer inlet_pass) {
		this.inlet_pass = inlet_pass;
	}
	public String getFault_status() {
		return fault_status;
	}
	public void setFault_status(String fault_status) {
		this.fault_status = fault_status;
	}
	public Integer getEngine_fault() {
		return engine_fault;
	}
	public void setEngine_fault(Integer engine_fault) {
		this.engine_fault = engine_fault;
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
	public Integer getOil_pass() {
		return oil_pass;
	}
	public void setOil_pass(Integer oil_pass) {
		this.oil_pass = oil_pass;
	}
	public Integer getAir_pass() {
		return air_pass;
	}
	public void setAir_pass(Integer air_pass) {
		this.air_pass = air_pass;
	}

	public Double getGas_position() {
		return gas_position;
	}
	public void setGas_position(Double gas_position) {
		this.gas_position = gas_position;
	}
	public Integer getEngine_time() {
		return engine_time;
	}
	public void setEngine_time(Integer engine_time) {
		this.engine_time = engine_time;
	}
	
	public String getInnage() {
		return innage;
	}
	public void setInnage(String innage) {
		this.innage = innage;
	}
	public Integer getEngine_load() {
		return engine_load;
	}
	public void setEngine_load(Integer engine_load) {
		this.engine_load = engine_load;
	}
	public Double getLong_fuel() {
		return long_fuel;
	}
	public void setLong_fuel(Double long_fuel) {
		this.long_fuel = long_fuel;
	}
	public Double getAngle_engine() {
		return angle_engine;
	}
	public void setAngle_engine(Double angle_engine) {
		this.angle_engine = angle_engine;
	}
	public Long getCar_mileage() {
		return car_mileage;
	}
	public void setCar_mileage(Long car_mileage) {
		this.car_mileage = car_mileage;
	}
	public String getSenddate() {
		if(senddate!=null&&senddate.endsWith(".0"))
			return senddate.substring(0, senddate.length()-2);
		return senddate;
	}
	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}
	public String getCreatedate() {
		if(createdate!=null&&createdate.equals(".0"))
			return createdate.substring(0, createdate.length()-2);
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

}
