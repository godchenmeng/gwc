package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

/**   
 * @author mars   
 * @date 2017年4月12日 上午10:12:48 
 */
public class OrgTree {
	
	private String cls;
	
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	@ApiModelProperty(value = "机构或者部门或者车辆id")
	private Long id;
	@ApiModelProperty(value = "机构或者部门或者车辆名称")
	private String text;
	@ApiModelProperty(value = "1机构或者部门 2代表是汽车")
	private String type;
	@ApiModelProperty(value = "1在线 0离线 2代表是静止 ")
	private String acc;
	private Double latitude;
	private Double longitude;
	private Boolean leaf;
	
	
	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}


