package com.youxing.car.entity;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BarsRule implements Serializable{
	private Long id;
	private Long barid;
	private String type;
	private String startdate;
	private String enddate;
	private List<BarsRuleRelation> barsRuleRelations;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBarid() {
		return barid;
	}
	public void setBarid(Long barid) {
		this.barid = barid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public List<BarsRuleRelation> getBarsRuleRelation() {
		return barsRuleRelations;
	}
	public void setBarsRuleRelation(List<BarsRuleRelation> barsRuleRelations) {
		this.barsRuleRelations = barsRuleRelations;
	}


}
