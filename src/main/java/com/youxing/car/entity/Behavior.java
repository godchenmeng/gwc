package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
@SuppressWarnings("serial")
public class Behavior implements Serializable{
	private String id;
	private String device;
	private String serial;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date start;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date end;
	private String trip_mark;
	private String trip_distance_type;
	private Long trip_distance;
	private Long trip_fuel;
	private Long trip_duration;
	private Long trip_overspeed;
	private Integer trip_overspeed_times;
	private Integer trip_speed_avage;
	private Integer trip_speed;
	private Integer trip_idle;
	private String trip_mask;
	private Long trip_number;
	private Long trip_accelerate;
	private Long trip_decelerate;
	private Long trip_sharp;
	private Long trip_speed_20;
	private Long trip_speed_24;
	private Long trip_speed_46;
	private Long trip_speed_68;
	private Long trip_speed_81;
	private Long trip_speed_11;
	private Long trip_speed_12;
	private Long accelerate;
	private Long decelerate;
	private Long overspeed;
	private Long sharp_turn;
	private Long idlespeed;
	private Date createdate;
	private Date lastdate;
	private String date;
	
	private String org_name;
	private Double online;
	private String org;
	private Double avgLine;

	
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getLastdate() {
		return lastdate;
	}

	public void setLastdate(Date lastdate) {
		this.lastdate = lastdate;
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
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getTrip_mark() {
		return trip_mark;
	}
	public void setTrip_mark(String trip_mark) {
		this.trip_mark = trip_mark;
	}
	public String getTrip_distance_type() {
		return trip_distance_type;
	}
	public void setTrip_distance_type(String trip_distance_type) {
		this.trip_distance_type = trip_distance_type;
	}
	public Long getTrip_distance() {
		return trip_distance;
	}
	public void setTrip_distance(Long trip_distance) {
		this.trip_distance = trip_distance;
	}
	public Long getTrip_fuel() {
		return trip_fuel;
	}
	public void setTrip_fuel(Long trip_fuel) {
		this.trip_fuel = trip_fuel;
	}
	public Long getTrip_duration() {
		return trip_duration;
	}
	public void setTrip_duration(Long trip_duration) {
		this.trip_duration = trip_duration;
	}
	public Long getTrip_overspeed() {
		return trip_overspeed;
	}
	public void setTrip_overspeed(Long trip_overspeed) {
		this.trip_overspeed = trip_overspeed;
	}
	public Integer getTrip_overspeed_times() {
		return trip_overspeed_times;
	}
	public void setTrip_overspeed_times(Integer trip_overspeed_times) {
		this.trip_overspeed_times = trip_overspeed_times;
	}
	public Integer getTrip_speed_avage() {
		return trip_speed_avage;
	}
	public void setTrip_speed_avage(Integer trip_speed_avage) {
		this.trip_speed_avage = trip_speed_avage;
	}
	public Integer getTrip_speed() {
		return trip_speed;
	}
	public void setTrip_speed(Integer trip_speed) {
		this.trip_speed = trip_speed;
	}
	public Integer getTrip_idle() {
		return trip_idle;
	}
	public void setTrip_idle(Integer trip_idle) {
		this.trip_idle = trip_idle;
	}
	public String getTrip_mask() {
		return trip_mask;
	}
	public void setTrip_mask(String trip_mask) {
		this.trip_mask = trip_mask;
	}
	public Long getTrip_number() {
		return trip_number;
	}
	public void setTrip_number(Long trip_number) {
		this.trip_number = trip_number;
	}
	public Long getTrip_accelerate() {
		return trip_accelerate;
	}
	public void setTrip_accelerate(Long trip_accelerate) {
		this.trip_accelerate = trip_accelerate;
	}
	public Long getTrip_decelerate() {
		return trip_decelerate;
	}
	public void setTrip_decelerate(Long trip_decelerate) {
		this.trip_decelerate = trip_decelerate;
	}
	public Long getTrip_sharp() {
		return trip_sharp;
	}
	public void setTrip_sharp(Long trip_sharp) {
		this.trip_sharp = trip_sharp;
	}
	public Long getTrip_speed_20() {
		return trip_speed_20;
	}
	public void setTrip_speed_20(Long trip_speed_20) {
		this.trip_speed_20 = trip_speed_20;
	}
	public Long getTrip_speed_24() {
		return trip_speed_24;
	}
	public void setTrip_speed_24(Long trip_speed_24) {
		this.trip_speed_24 = trip_speed_24;
	}
	public Long getTrip_speed_46() {
		return trip_speed_46;
	}
	public void setTrip_speed_46(Long trip_speed_46) {
		this.trip_speed_46 = trip_speed_46;
	}
	public Long getTrip_speed_68() {
		return trip_speed_68;
	}
	public void setTrip_speed_68(Long trip_speed_68) {
		this.trip_speed_68 = trip_speed_68;
	}
	public Long getTrip_speed_81() {
		return trip_speed_81;
	}
	public void setTrip_speed_81(Long trip_speed_81) {
		this.trip_speed_81 = trip_speed_81;
	}
	public Long getTrip_speed_11() {
		return trip_speed_11;
	}
	public void setTrip_speed_11(Long trip_speed_11) {
		this.trip_speed_11 = trip_speed_11;
	}
	public Long getTrip_speed_12() {
		return trip_speed_12;
	}
	public void setTrip_speed_12(Long trip_speed_12) {
		this.trip_speed_12 = trip_speed_12;
	}
	public Long getAccelerate() {
		return accelerate;
	}
	public void setAccelerate(Long accelerate) {
		this.accelerate = accelerate;
	}
	public Long getDecelerate() {
		return decelerate;
	}
	public void setDecelerate(Long decelerate) {
		this.decelerate = decelerate;
	}
	public Long getOverspeed() {
		return overspeed;
	}
	public void setOverspeed(Long overspeed) {
		this.overspeed = overspeed;
	}
	public Long getSharp_turn() {
		return sharp_turn;
	}
	public void setSharp_turn(Long sharp_turn) {
		this.sharp_turn = sharp_turn;
	}
	public Long getIdlespeed() {
		return idlespeed;
	}
	public void setIdlespeed(Long idlespeed) {
		this.idlespeed = idlespeed;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	public Double getOnline() {
		return online;
	}

	public void setOnline(Double online) {
		this.online = online;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public Double getAvgLine() {
		return avgLine;
	}

	public void setAvgLine(Double avgLine) {
		this.avgLine = avgLine;
	}
	
	
	
	
}