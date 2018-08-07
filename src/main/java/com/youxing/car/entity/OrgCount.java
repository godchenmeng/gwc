package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月26日 下午3:24:31 
 */

public class OrgCount {
	@ApiModelProperty(value = "公里数")
	private Float mil;
	@ApiModelProperty(value = "油耗")
	private Float fuel;
	@ApiModelProperty(value = "急驾驶")
	private Integer acce;
	@ApiModelProperty(value = "急减速和急刹车")
	private Integer dece;
	@ApiModelProperty(value = "急转弯")
	private Integer sharp;
	@ApiModelProperty(value = "机构id")
	private Long org;
	@ApiModelProperty(value = "机构名称")
	private String orgName;
	
	@ApiModelProperty(value = "车牌号码")
	private String car_no;
	
	@ApiModelProperty(value = "汽车运行时间")
	private Long sec;
	private String day;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public Long getSec() {
		return sec;
	}
	public void setSec(Long sec) {
		this.sec = sec;
	}
	public Long getOrg() {
		return org;
	}
	public void setOrg(Long org) {
		this.org = org;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Float getMil() {
		return mil;
	}
	public void setMil(Float mil) {
		this.mil = mil;
	}
	public Float getFuel() {
		return fuel;
	}
	public void setFuel(Float fuel) {
		this.fuel = fuel;
	}
	public Integer getAcce() {
		return acce;
	}
	public void setAcce(Integer acce) {
		this.acce = acce;
	}
	public Integer getDece() {
		return dece;
	}
	public void setDece(Integer dece) {
		this.dece = dece;
	}
	public Integer getSharp() {
		return sharp;
	}
	public void setSharp(Integer sharp) {
		this.sharp = sharp;
	}
	
	
}


