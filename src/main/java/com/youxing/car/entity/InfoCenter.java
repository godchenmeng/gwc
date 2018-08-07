package com.youxing.car.entity;

import java.io.Serializable;
@SuppressWarnings("serial")
public class InfoCenter implements Serializable{
	
		//年检时间
		private String inspection_time;
		//保险时间
		private String insurance_time;
		//违章总次数
		private int illegal_count;
		//越界总次数
		private int inspection_count;
		//超速总次数
		private int speed;
		//超时总次数
		private int overtime;
		//拔插警报总次数
		private int ele_count;
		//无单违规用车次数
		private int noTaskCount;
		
		private String date;
		private String speedCount;
		private String orgName;
		
		//总里程
		private Double  mileSum;
		
	
		
	    public int getNoTaskCount() {
			return noTaskCount;
		}
		public void setNoTaskCount(int noTaskCount) {
			this.noTaskCount = noTaskCount;
		}
		public Double getMileSum() {
			return mileSum;
		}
		public void setMileSum(Double mileSum) {
			this.mileSum = mileSum;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getSpeedCount() {
			return speedCount;
		}
		public void setSpeedCount(String speedCount) {
			this.speedCount = speedCount;
		}
		public String getOrgName() {
			return orgName;
		}
		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}
		public String getInspection_time() {
			return inspection_time;
		}
		public void setInspection_time(String inspection_time) {
			this.inspection_time = inspection_time;
		}
		public String getInsurance_time() {
			return insurance_time;
		}
		public void setInsurance_time(String insurance_time) {
			this.insurance_time = insurance_time;
		}
		public int getIllegal_count() {
			return illegal_count;
		}
		public void setIllegal_count(int illegal_count) {
			this.illegal_count = illegal_count;
		}
		public int getInspection_count() {
			return inspection_count;
		}
		public void setInspection_count(int inspection_count) {
			this.inspection_count = inspection_count;
		}
		public int getSpeed() {
			return speed;
		}
		public void setSpeed(int speed) {
			this.speed = speed;
		}
		public int getOvertime() {
			return overtime;
		}
		public void setOvertime(int overtime) {
			this.overtime = overtime;
		}
		public int getEle_count() {
			return ele_count;
		}
		public void setEle_count(int ele_count) {
			this.ele_count = ele_count;
		}
		//id
		private Long id;
		//车牌id
		private String car_id;
		//汽车名称
		private String car_name;
		//驾驶员
		private String driver;
		//部门
		private String org;
		//发生时间
		private String happen_time;
		//解决时间
		private String solve_time;
		//车牌号
		private String car_no;
	    //违规类型（0违章，1越界，2超速，3超时，4拔插警报，5保险提醒，6年检提醒，7保养提醒
	    private String illegal_type;
	    
	    private String illegal_speed;
	    
	    private String illegal_address;
	    
	    private String limit_speed;
	    
	    private String name;
	    
	    private String status;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getCar_id() {
			return car_id;
		}
		public void setCar_id(String car_id) {
			this.car_id = car_id;
		}
		public String getCar_name() {
			return car_name;
		}
		public void setCar_name(String car_name) {
			this.car_name = car_name;
		}
		public String getDriver() {
			return driver;
		}
		public void setDriver(String driver) {
			this.driver = driver;
		}
		public String getOrg() {
			return org;
		}
		public void setOrg(String org) {
			this.org = org;
		}
		public String getHappen_time() {
			return happen_time;
		}
		public void setHappen_time(String happen_time) {
			this.happen_time = happen_time;
		}
		public String getSolve_time() {
			return solve_time;
		}
		public void setSolve_time(String solve_time) {
			this.solve_time = solve_time;
		}
		public String getCar_no() {
			return car_no;
		}
		public void setCar_no(String car_no) {
			this.car_no = car_no;
		}
		public String getIllegal_type() {
			return illegal_type;
		}
		public void setIllegal_type(String illegal_type) {
			this.illegal_type = illegal_type;
		}
		public String getIllegal_speed() {
			return illegal_speed;
		}
		public void setIllegal_speed(String illegal_speed) {
			this.illegal_speed = illegal_speed;
		}
		public String getIllegal_address() {
			return illegal_address;
		}
		public void setIllegal_address(String illegal_address) {
			this.illegal_address = illegal_address;
		}
		public String getLimit_speed() {
			return limit_speed;
		}
		public void setLimit_speed(String limit_speed) {
			this.limit_speed = limit_speed;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}


}
