package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
@SuppressWarnings("serial")
public class Bars implements Serializable{
	private Long id;
	private String name;
	private String status;
	private String drawingType;
	private Double radius;
	private Long org;
	private Date createdate;
	private Long createby;
	
	private BarsRule barsRule;
	
	private List<BarsLat> barsLats;

	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getName(){
	    return name;
	}
	public void setName(String name){
	    this.name = name;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public Long getOrg(){
	    return org;
	}
	public void setOrg(Long org){
	    this.org = org;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}
	public Long getCreateby(){
	    return createby;
	}
	public void setCreateby(Long createby){
	    this.createby = createby;
	}
	public BarsRule getBarsRule() {
		return barsRule;
	}
	public void setBarsRule(BarsRule barsRule) {
		this.barsRule = barsRule;
	}
	public List<BarsLat> getBarsLats() {
		return barsLats;
	}
	public void setBarsLats(List<BarsLat> barsLats) {
		this.barsLats = barsLats;
	}
	public String getDrawingType() {
		return drawingType;
	}
	public void setDrawingType(String drawingType) {
		this.drawingType = drawingType;
	}
	public Double getRadius() {
		return radius;
	}
	public void setRadius(Double radius) {
		this.radius = radius;
	}

}
