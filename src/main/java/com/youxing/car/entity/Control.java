package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
@SuppressWarnings("serial")
public class Control implements Serializable{
	@ApiModelProperty(value = "作废数据id")
	private Long id;
	private Long apply_id;
	private Long car_id;
	private Long driver_id;
	@ApiModelProperty(value = "状态  1-未签收 2 -已签收 3-执行中  5- 已执行 6 -已归队 7-作废")
	private String status;
	private Long createby;
	private String createdate;
	@ApiModelProperty(value = "用车人姓名")
	private String use_name;
	@ApiModelProperty(value = "目的地")
	private String end_place;
	@ApiModelProperty(value = "计划时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date plan_time;
	@ApiModelProperty(value = "返回时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date  plan_return;
	@ApiModelProperty(value = "车辆号码")
	private String car_no;
	@ApiModelProperty(value = "起始地点")
	private String start_place;	
	@ApiModelProperty(value = "驾驶员姓名")
	private String driver_name;
	@ApiModelProperty(value = "起始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date start;
	@ApiModelProperty(value = "结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date end;
	@ApiModelProperty(value = "用车人电话")
	private String use_mobile;
	@ApiModelProperty(value = "司机电话")
	private String mobile;
	@ApiModelProperty(value = "驾驶员反馈信息")
	private String feedback;
	private String type;
	private String driver_type;
	private String driver_org_name;
	private String car_org_name;
	
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDriver_type() {
		return driver_type;
	}
	public void setDriver_type(String driver_type) {
		this.driver_type = driver_type;
	}
	public String getDriver_org_name() {
		return driver_org_name;
	}
	public void setDriver_org_name(String driver_org_name) {
		this.driver_org_name = driver_org_name;
	}
	public String getCar_org_name() {
		return car_org_name;
	}
	public void setCar_org_name(String car_org_name) {
		this.car_org_name = car_org_name;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUse_mobile() {
		return use_mobile;
	}
	public void setUse_mobile(String use_mobile) {
		this.use_mobile = use_mobile;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
	public String getStart_place() {
		return start_place;
	}
	public void setStart_place(String start_place) {
		this.start_place = start_place;
	}
	public String getCar_no() {
		return car_no;
	}
	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}
	public String getUse_name() {
		return use_name;
	}
	public void setUse_name(String use_name) {
		this.use_name = use_name;
	}
	public String getEnd_place() {
		return end_place;
	}
	public void setEnd_place(String end_place) {
		this.end_place = end_place;
	}
	public Date getPlan_time() {
		return plan_time;
	}
	public void setPlan_time(Date plan_time) {
		this.plan_time = plan_time;
	}
	public Date getPlan_return() {
		return plan_return;
	}
	public void setPlan_return(Date plan_return) {
		this.plan_return = plan_return;
	}
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public Long getApply_id(){
	    return apply_id;
	}
	public void setApply_id(Long apply_id){
	    this.apply_id = apply_id;
	}
	public Long getCar_id(){
	    return car_id;
	}
	public void setCar_id(Long car_id){
	    this.car_id = car_id;
	}
	public Long getDriver_id(){
	    return driver_id;
	}
	public void setDriver_id(Long driver_id){
	    this.driver_id = driver_id;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public Long getCreateby(){
	    return createby;
	}
	public void setCreateby(Long createby){
	    this.createby = createby;
	}
	public String getCreatedate(){
		if(createdate!=null&&createdate.endsWith(".0"))
			return createdate.substring(0, createdate.length()-2);
	    return createdate;
	}
	public void setCreatedate(String createdate){
	    this.createdate = createdate;
	}

}
