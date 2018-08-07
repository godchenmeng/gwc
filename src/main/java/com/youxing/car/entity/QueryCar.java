package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年7月5日 下午4:52:57 
 */
public class QueryCar {
	@ApiModelProperty(value = "机构或者部门或者车辆id")
	private Long id;
	@ApiModelProperty(value = "机构或者部门或者车辆名称")
	private String car_no;
	@ApiModelProperty(value = "1机构或者部门 2代表是汽车")
	private String type;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}


