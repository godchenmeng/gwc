package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月26日 下午6:45:17 
 */
public class AlmNotice {

	@ApiModelProperty(value = "超速")
	private Integer speed;
	@ApiModelProperty(value = "越界")
	private Integer over;
	@ApiModelProperty(value = "违章")
	private Integer bk;
	@ApiModelProperty(value = "断电")
	private Integer outage;
	@ApiModelProperty(value = "非规定时间")
	private Integer time;
	@ApiModelProperty(value = "机构id")
	private Long org;
	@ApiModelProperty(value = "机构名称")
	private String orgName;
	@ApiModelProperty(value = "车辆总数")
	private Integer car;
	@ApiModelProperty(value = "其他")
	private Integer other;
	
	
	public Integer getOther() {
		return other;
	}
	public void setOther(Integer other) {
		this.other = other;
	}
	public Integer getCar() {
		return car;
	}
	public void setCar(Integer car) {
		this.car = car;
	}
	public Integer getSpeed() {
		return speed;
	}
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
	public Integer getOver() {
		return over;
	}
	public void setOver(Integer over) {
		this.over = over;
	}
	public Integer getBk() {
		return bk;
	}
	public void setBk(Integer bk) {
		this.bk = bk;
	}
	public Integer getOutage() {
		return outage;
	}
	public void setOutage(Integer outage) {
		this.outage = outage;
	}
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
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
	
}


