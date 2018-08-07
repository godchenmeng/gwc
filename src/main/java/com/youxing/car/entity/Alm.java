package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月22日 下午2:55:49 
 */
public class Alm {

	@ApiModelProperty(value = "日期天数")
	private Integer day;
	@ApiModelProperty(value = "次数")
	private Integer count;
	@ApiModelProperty(value = "报警类型")
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}


