package com.youxing.car.entity;

public class CountInfo {
private Long org_id;
private String org_name;
private String car_no;
private String start_time;
private String end_time;
private Double duration;
private Long max_speed;
private String start_site;
private String end_site;

/**
 * 越界信息
 */
// 触发时间 格式:”2017-08-01 00:00:00”,
private String trigger_time;
// 触发类型 包括驶入触发，驶出触发，驶入驶出触发，类型code与电子栅栏类型一致,
private String trigger_type;
// 触发地点
private String trigger_site;

/**
 * 违章信息
 * @return
 */
 //违章时间 格式“2017-08-01 00:00:00”,
 private String violation_time;
 //违章位置,
 private String violation_site;
 //违章类型
 private String violation_type;
 //驾驶员姓名
 private String driver_name;
 
 /*
  * 驾驶信息
  */
 private double mileage;
 //驾驶时长（多少小时多少分钟）,
 private double drive_duration;
 private double fuel;
 // 三急（急加速，急减速，急转弯  格式：25/56/68）
 private String break_drive;
 
 /**
  * 三急数据
  * @return
  */
 //急加速
 private int trip_accelerate_sum;
 //急减速
 private int trip_decelerate_sum;
 //急转弯
 private int trip_sharp_sum;
 
 
 /**
  * 综合统计
  * @return
  */
 
 
 
 //时间（格式2017-08-01）
 private String  date_time;
 // 上线率%,
 private double online_rate;
 //超速次数,
 private int overspeed;
 //越界次数
 private int outside;
 //未入库次数
 private int not_return;
 //违停次数
 private int foul_park;
 //无单违规次数
 private int foul_task;
 //非规定时段次数
 private int foul_time;
 //违章次数
 private int break_rule;
public Long getOrg_id() {
	return org_id;
}
public void setOrg_id(Long org_id) {
	this.org_id = org_id;
}
public String getOrg_name() {
	return org_name;
}
public void setOrg_name(String org_name) {
	this.org_name = org_name;
}
public String getCar_no() {
	return car_no;
}
public void setCar_no(String car_no) {
	this.car_no = car_no;
}
public String getStart_time() {
	return start_time;
}
public void setStart_time(String start_time) {
	this.start_time = start_time;
}
public String getEnd_time() {
	return end_time;
}
public void setEnd_time(String end_time) {
	this.end_time = end_time;
}
public Double getDuration() {
	return duration;
}
public void setDuration(Double duration) {
	this.duration = duration;
}
public Long getMax_speed() {
	return max_speed;
}
public void setMax_speed(Long max_speed) {
	this.max_speed = max_speed;
}
public String getStart_site() {
	return start_site;
}
public void setStart_site(String start_site) {
	this.start_site = start_site;
}
public String getEnd_site() {
	return end_site;
}
public void setEnd_site(String end_site) {
	this.end_site = end_site;
}
public String getTrigger_time() {
	return trigger_time;
}
public void setTrigger_time(String trigger_time) {
	this.trigger_time = trigger_time;
}
public String getTrigger_type() {
	return trigger_type;
}
public void setTrigger_type(String trigger_type) {
	this.trigger_type = trigger_type;
}
public String getTrigger_site() {
	return trigger_site;
}
public void setTrigger_site(String trigger_site) {
	this.trigger_site = trigger_site;
}
public String getViolation_time() {
	return violation_time;
}
public void setViolation_time(String violation_time) {
	this.violation_time = violation_time;
}
public String getViolation_site() {
	return violation_site;
}
public void setViolation_site(String violation_site) {
	this.violation_site = violation_site;
}
public String getViolation_type() {
	return violation_type;
}
public void setViolation_type(String violation_type) {
	this.violation_type = violation_type;
}
public String getDriver_name() {
	return driver_name;
}
public void setDriver_name(String driver_name) {
	this.driver_name = driver_name;
}
public double getMileage() {
	return mileage;
}
public void setMileage(double mileage) {
	this.mileage = mileage;
}
public double getDrive_duration() {
	return drive_duration;
}
public void setDrive_duration(double drive_duration) {
	this.drive_duration = drive_duration;
}
public double getFuel() {
	return fuel;
}
public void setFuel(double fuel) {
	this.fuel = fuel;
}
public String getBreak_drive() {
	return break_drive;
}
public void setBreak_drive(String break_drive) {
	this.break_drive = break_drive;
}
public int getTrip_accelerate_sum() {
	return trip_accelerate_sum;
}
public void setTrip_accelerate_sum(int trip_accelerate_sum) {
	this.trip_accelerate_sum = trip_accelerate_sum;
}
public int getTrip_decelerate_sum() {
	return trip_decelerate_sum;
}
public void setTrip_decelerate_sum(int trip_decelerate_sum) {
	this.trip_decelerate_sum = trip_decelerate_sum;
}
public int getTrip_sharp_sum() {
	return trip_sharp_sum;
}
public void setTrip_sharp_sum(int trip_sharp_sum) {
	this.trip_sharp_sum = trip_sharp_sum;
}
public String getDate_time() {
	return date_time;
}
public void setDate_time(String date_time) {
	this.date_time = date_time;
}

public double getOnline_rate() {
	return online_rate;
}
public void setOnline_rate(double online_rate) {
	this.online_rate = online_rate;
}
public int getOverspeed() {
	return overspeed;
}
public void setOverspeed(int overspeed) {
	this.overspeed = overspeed;
}
public int getOutside() {
	return outside;
}
public void setOutside(int outside) {
	this.outside = outside;
}
public int getNot_return() {
	return not_return;
}
public void setNot_return(int not_return) {
	this.not_return = not_return;
}
public int getFoul_park() {
	return foul_park;
}
public void setFoul_park(int foul_park) {
	this.foul_park = foul_park;
}
public int getFoul_task() {
	return foul_task;
}
public void setFoul_task(int foul_task) {
	this.foul_task = foul_task;
}
public int getFoul_time() {
	return foul_time;
}
public void setFoul_time(int foul_time) {
	this.foul_time = foul_time;
}
public int getBreak_rule() {
	return break_rule;
}
public void setBreak_rule(int break_rule) {
	this.break_rule = break_rule;
}








}
