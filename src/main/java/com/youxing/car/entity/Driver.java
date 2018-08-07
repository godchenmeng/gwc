package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
@SuppressWarnings("serial")
public class Driver implements Serializable{
	private String card;
	@JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
	private Date birth;
	private String oname;
	
	
	public String getCard() {
		return card;
	}
	public void setCard(String card) {
		this.card = card;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getOname() {
		return oname;
	}
	public void setOname(String oname) {
		this.oname = oname;
	}
	@ApiModelProperty(value = "本条数据id")
	private Long id;
	private Long uid;
	private String driver_no;
	private String arrive;
	private String driver_type;
	private String p_image;
	private String d_inmage_1;
	private String d_inmage_2;
	private Long createdby;
	@ApiModelProperty(value = "驾驶员状态 工作状态-1-在岗 2 公休 3 长期事假 4 长期病假 5待岗")
	private String d_status;
	private String start;
	private String end;
	private String texts;
	private String status;
	private String createdate;
	private Integer plan;
	@ApiModelProperty(value = "驾驶员电话")
	private String mobile;
	@ApiModelProperty(value = "驾驶员姓名")
	private String name;
	@ApiModelProperty(value = "驾驶员性别 1男 2女")
	private String sex;
	@ApiModelProperty(value = "驾驶员任务状态，1无任务，2有任务")
	private String task_status;
	
	private String  t_image_1 ;
	private String  t_image_2 ;
	
	private String  id_image_1 ;
	private String  id_image_2 ;
	
	private String reason;
	private String driver_status;
	
	
	
	public String getT_image_1() {
		return t_image_1;
	}
	public void setT_image_1(String t_image_1) {
		this.t_image_1 = t_image_1;
	}
	public String getT_image_2() {
		return t_image_2;
	}
	public void setT_image_2(String t_image_2) {
		this.t_image_2 = t_image_2;
	}
	public String getTask_status() {
		return task_status;
	}
	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public Long getUid(){
	    return uid;
	}
	public void setUid(Long uid){
	    this.uid = uid;
	}
	public String getDriver_no(){
	    return driver_no;
	}
	public void setDriver_no(String driver_no){
	    this.driver_no = driver_no;
	}
	public String getArrive(){
		if(arrive!=null&&arrive.endsWith(".0"))
			return arrive.substring(0, arrive.length()-2);
	    return arrive;
	}
	public void setArrive(String arrive){
	    this.arrive = arrive;
	}
	public String getDriver_type(){
	    return driver_type;
	}
	public void setDriver_type(String driver_type){
	    this.driver_type = driver_type;
	}
	public String getP_image(){
	    return p_image;
	}
	public void setP_image(String p_image){
	    this.p_image = p_image;
	}
	public String getD_inmage_1(){
	    return d_inmage_1;
	}
	public void setD_inmage_1(String d_inmage_1){
	    this.d_inmage_1 = d_inmage_1;
	}
	public String getD_inmage_2(){
	    return d_inmage_2;
	}
	public void setD_inmage_2(String d_inmage_2){
	    this.d_inmage_2 = d_inmage_2;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public String getD_status(){
	    return d_status;
	}
	public void setD_status(String d_status){
	    this.d_status = d_status;
	}
	public String getStart(){
		if(start!=null&&start.endsWith(".0"))
			return start.substring(0, start.length()-2);
	    return start;
	}
	public void setStart(String start){
	    this.start = start;
	}
	public String getEnd(){
		if(end!=null&&end.endsWith(".0"))
			return end.substring(0, end.length()-2);
	    return end;
	}
	public void setEnd(String end){
	    this.end = end;
	}
	public String getTexts(){
	    return texts;
	}
	public void setTexts(String texts){
	    this.texts = texts;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public String getCreatedate(){
		if(createdate!=null&&createdate.equals(".0"))
			return createdate.substring(0, createdate.length()-2);
	    return createdate;
	}
	public void setCreatedate(String createdate){
	    this.createdate = createdate;
	}
	public Integer getPlan(){
	    return plan;
	}
	public void setPlan(Integer plan){
	    this.plan = plan;
	}
	
	public String getId_image_1() {
		return id_image_1;
	}
	public void setId_image_1(String id_image_1) {
		this.id_image_1 = id_image_1;
	}
	public String getId_image_2() {
		return id_image_2;
	}
	public void setId_image_2(String id_image_2) {
		this.id_image_2 = id_image_2;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getDriver_status() {
		return driver_status;
	}
	public void setDriver_status(String driver_status) {
		this.driver_status = driver_status;
	}
	
	
	
	
}
