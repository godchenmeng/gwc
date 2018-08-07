package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月26日 下午7:39:27 
 */
public class AlmDetail {
	@ApiModelProperty(value = "机构名称")
	private String orgName;
	@ApiModelProperty(value = "机构车辆数量")
	private Integer car;
	@ApiModelProperty(value = "报警车辆数量")
	private Integer almCar;
	@ApiModelProperty(value = "报警次数")
	private Integer alm;
	
	
	public Integer getAlm() {
		return alm;
	}
	public void setAlm(Integer alm) {
		this.alm = alm;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Integer getCar() {
		return car;
	}
	public void setCar(Integer car) {
		this.car = car;
	}
	public Integer getAlmCar() {
		return almCar;
	}
	public void setAlmCar(Integer almCar) {
		this.almCar = almCar;
	}
	
}


