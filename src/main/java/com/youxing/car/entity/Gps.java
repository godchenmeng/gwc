package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;
@SuppressWarnings("serial")
public class Gps implements Serializable{
	private String id;
	private String device;
	private String serial;
	private String obd_type;
	private String data_flow;
	private Double dianya;
	private String mileage_type;
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
	private Double air_sensor;
	private Double gas_position;
	private Integer engine_time;
	private Long fault_mil;
	private String innage;
	private Integer engine_load;
	private Double long_fuel;
	private Double angle_engine;
	private Long car_mileage;
	private Long car_time;
	private Integer mean1;
	private Integer mean2;
	private Integer mean3;
	private Integer mean4;
	private Integer total_max;
	private Date senddate;
	private Date createdate;
	private String alm_id;
	private String gps_status;
	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Integer satellites;
	private Double gps_speed;
	private Double direction;
	private Double pdop;
	private String acc;
	private String car_no;
	private String car_type;
	private Long org_id;
	private String org;
	private Double start_latitude;
	private Double start_longitude;
	private Date startdate;
	private Double moment_fuel;
	private String type;
	private Long car_id;
	private String video_device;
	private Double total_oil;//总油量
	private Double remain_oil;//剩余油量
	private String turn_state;//罐体旋转状态      0不转   1正转   2反转
	private Double fuel_level;
	private Double water_level;

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

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public Integer getEngine_time() {
		return engine_time;
	}
	public void setEngine_time(Integer engine_time) {
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
	public Long getCar_time() {
		return car_time;
	}
	public void setCar_time(Long car_time) {
		this.car_time = car_time;
	}
	public Integer getMean1() {
		return mean1;
	}
	public void setMean1(Integer mean1) {
		this.mean1 = mean1;
	}
	public Integer getMean2() {
		return mean2;
	}
	public void setMean2(Integer mean2) {
		this.mean2 = mean2;
	}
	public Integer getMean3() {
		return mean3;
	}
	public void setMean3(Integer mean3) {
		this.mean3 = mean3;
	}
	public Integer getMean4() {
		return mean4;
	}
	public void setMean4(Integer mean4) {
		this.mean4 = mean4;
	}
	public Integer getTotal_max() {
		return total_max;
	}
	public void setTotal_max(Integer total_max) {
		this.total_max = total_max;
	}
	public Date getSenddate() {
		return senddate;
	}
	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
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
	public Integer getSatellites() {
		return satellites;
	}
	public void setSatellites(Integer satellites) {
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
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
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
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Double getMoment_fuel() {
		return moment_fuel;
	}
	public void setMoment_fuel(Double moment_fuel) {
		this.moment_fuel = moment_fuel;
	}	
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	public Long getOrg_id() {
		return org_id;
	}
	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}
	public Long getCar_id() {
		return car_id;
	}
	public void setCar_id(Long car_id) {
		this.car_id = car_id;
	}
	public String getVideo_device() {
		return video_device;
	}
	public void setVideo_device(String video_device) {
		this.video_device = video_device;
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
	
}
