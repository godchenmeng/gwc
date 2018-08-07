package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年5月26日 下午6:07:15 
 */
public class CountPushType {
	@ApiModelProperty(value = "类型1-系统通知 2-超速 3-越界 4-非规定 5-拔插")
	private String type;
	@ApiModelProperty(value = "数量")
	private Integer num;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
	
}


