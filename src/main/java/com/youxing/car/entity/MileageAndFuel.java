package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月22日 上午11:24:05 
 */
public class MileageAndFuel {

	@ApiModelProperty(value = "日期天数")
	private Integer day;
	@ApiModelProperty(value = "里程数")
	private Float dayMil = 0.0f;
	@ApiModelProperty(value = "油耗")
	private Float dayfuel = 0.0f;
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	public Float getDayMil() {
		return dayMil;
	}
	public void setDayMil(Float dayMil) {
		this.dayMil = dayMil;
	}
	public Float getDayfuel() {
		return dayfuel;
	}
	public void setDayfuel(Float dayfuel) {
		this.dayfuel = dayfuel;
	}
	
	
	
}
