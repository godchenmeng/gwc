package com.youxing.car.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CarObd implements Serializable {

	private String device;
	private String serial;
	private String obd_type;
	private String data_flow;
	private Double dianya;
	private String mileage_type;
	private Long mileage;
	private Long fuel;
	private int engine_speed;
	private int car_speed;
	private Double sky_flow;
	private int inlet;
	private int inlet_pass;
	private int fault_status;
	private int coolant;
	private int car_tempu;
	private int oil_pass;
	private int air_pass;
	private Double air_sensor;
	private Double gas_position;
	private int engine_time;
	private Long fault_mil;
	private String innage;
	private int engine_load;
	private Double long_fule;
	private Double angle_engine;
	private Long car_mileage;
	private Long car_time;
	private int mean1;
	private int mean2;
	private int mean3;
	private int mean4;
	private int total_max;
	private String acc;
	private Long senddate;
	private Long createdate;
	private String alm_id;
	private String gps_status;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private int satellites;
	private Double gps_speed;
	private Double direction;
	private Double pdop;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getObd_type() {
		return obd_type;
	}

	public void setObd_type(String obd_type) {
		this.obd_type = obd_type;
	}

	public String getData_flow() {
		return data_flow;
	}

	public void setData_flow(String data_flow) {
		this.data_flow = data_flow;
	}

	public Double getDianya() {
		return dianya;
	}

	public void setDianya(Double dianya) {
		this.dianya = dianya;
	}

	public String getMileage_type() {
		return mileage_type;
	}

	public void setMileage_type(String mileage_type) {
		this.mileage_type = mileage_type;
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

	public int getEngine_speed() {
		return engine_speed;
	}

	public void setEngine_speed(int engine_speed) {
		this.engine_speed = engine_speed;
	}

	public int getCar_speed() {
		return car_speed;
	}

	public void setCar_speed(int car_speed) {
		this.car_speed = car_speed;
	}

	public Double getSky_flow() {
		return sky_flow;
	}

	public void setSky_flow(Double sky_flow) {
		this.sky_flow = sky_flow;
	}

	public int getInlet() {
		return inlet;
	}

	public void setInlet(int inlet) {
		this.inlet = inlet;
	}

	public int getInlet_pass() {
		return inlet_pass;
	}

	public void setInlet_pass(int inlet_pass) {
		this.inlet_pass = inlet_pass;
	}

	public int getFault_status() {
		return fault_status;
	}

	public void setFault_status(int fault_status) {
		this.fault_status = fault_status;
	}

	public int getCoolant() {
		return coolant;
	}

	public void setCoolant(int coolant) {
		this.coolant = coolant;
	}

	public int getCar_tempu() {
		return car_tempu;
	}

	public void setCar_tempu(int car_tempu) {
		this.car_tempu = car_tempu;
	}

	public int getOil_pass() {
		return oil_pass;
	}

	public void setOil_pass(int oil_pass) {
		this.oil_pass = oil_pass;
	}

	public int getAir_pass() {
		return air_pass;
	}

	public void setAir_pass(int air_pass) {
		this.air_pass = air_pass;
	}

	public Double getAir_sensor() {
		return air_sensor;
	}

	public void setAir_sensor(Double air_sensor) {
		this.air_sensor = air_sensor;
	}

	public Double getGas_position() {
		return gas_position;
	}

	public void setGas_position(Double gas_position) {
		this.gas_position = gas_position;
	}

	public int getEngine_time() {
		return engine_time;
	}

	public void setEngine_time(int engine_time) {
		this.engine_time = engine_time;
	}

	public Long getFault_mil() {
		return fault_mil;
	}

	public void setFault_mil(Long fault_mil) {
		this.fault_mil = fault_mil;
	}

	public String getInnage() {
		return innage;
	}

	public void setInnage(String innage) {
		this.innage = innage;
	}

	public int getEngine_load() {
		return engine_load;
	}

	public void setEngine_load(int engine_load) {
		this.engine_load = engine_load;
	}

	public Double getLong_fule() {
		return long_fule;
	}

	public void setLong_fule(Double long_fule) {
		this.long_fule = long_fule;
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

	public Long getCar_time() {
		return car_time;
	}

	public void setCar_time(Long car_time) {
		this.car_time = car_time;
	}

	public int getMean1() {
		return mean1;
	}

	public void setMean1(int mean1) {
		this.mean1 = mean1;
	}

	public int getMean2() {
		return mean2;
	}

	public void setMean2(int mean2) {
		this.mean2 = mean2;
	}

	public int getMean3() {
		return mean3;
	}

	public void setMean3(int mean3) {
		this.mean3 = mean3;
	}

	public int getMean4() {
		return mean4;
	}

	public void setMean4(int mean4) {
		this.mean4 = mean4;
	}

	public int getTotal_max() {
		return total_max;
	}

	public void setTotal_max(int total_max) {
		this.total_max = total_max;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public Long getSenddate() {
		return senddate;
	}

	public void setSenddate(Long senddate) {
		this.senddate = senddate;
	}

	public Long getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Long createdate) {
		this.createdate = createdate;
	}

	public String getAlm_id() {
		return alm_id;
	}

	public void setAlm_id(String alm_id) {
		this.alm_id = alm_id;
	}

	public String getGps_status() {
		return gps_status;
	}

	public void setGps_status(String gps_status) {
		this.gps_status = gps_status;
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

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public int getSatellites() {
		return satellites;
	}

	public void setSatellites(int satellites) {
		this.satellites = satellites;
	}

	public Double getGps_speed() {
		return gps_speed;
	}

	public void setGps_speed(Double gps_speed) {
		this.gps_speed = gps_speed;
	}

	public Double getDirection() {
		return direction;
	}

	public void setDirection(Double direction) {
		this.direction = direction;
	}

	public Double getPdop() {
		return pdop;
	}

	public void setPdop(Double pdop) {
		this.pdop = pdop;
	}

}
