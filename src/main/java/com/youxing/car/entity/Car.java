package com.youxing.car.entity;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
@SuppressWarnings("serial")
public class Car implements Serializable{
	
	private String oname;
	private String cname;
	private String insurance_time;
	private String inspection_time;
	private Long mt_mileage;
	private Long current_mileage;
	private Device device;
	private int relation_num;
	private String text;
	
	
	
	
	public int getRelation_num() {
		return relation_num;
	}
	public void setRelation_num(int relation_num) {
		this.relation_num = relation_num;
	}
	public String getOname() {
		return oname;
	}
	public void setOname(String oname) {
		this.oname = oname;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getInsurance_time() {
		return insurance_time;
	}
	public void setInsurance_time(String insurance_time) {
		this.insurance_time = insurance_time;
	}
	public String getInspection_time() {
		return inspection_time;
	}
	public void setInspection_time(String inspection_time) {
		this.inspection_time = inspection_time;
	}
	public Long getMt_mileage() {
		return mt_mileage;
	}
	public void setMt_mileage(Long mt_mileage) {
		this.mt_mileage = mt_mileage;
	}
	public Long getCurrent_mileage() {
		return current_mileage;
	}
	public void setCurrent_mileage(Long current_mileage) {
		this.current_mileage = current_mileage;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	@ApiModelProperty(value = "数据id")
	private Long id;
	private String brand;
	private Integer ov;
	private String ov_type;
	private String intime;
	@ApiModelProperty(value = "车辆类型")
	private String type;
	private String engine_no;
	private String vin;
	private Long mileage;
	private Long org;
	private String driver_no;
	private String createtime;
	private String texts;
	private String car_image;
	private String driver_image_1;
	private String driver_image_2;
	private String car_no;
	@ApiModelProperty(value = "车辆状态")
	private String status;
	private Long createdby;
	private String car_status;
	private String obd;
	@ApiModelProperty(value = "计划任务数")
	private Integer plan;
	private String acc;
	private Double latitude;
	private Double longitude;
	private String task_status;
	private String shipping_image_1;
	private String shipping_image_2;
	private String reason;
	private String type_name;
	//车辆别名
	private String self_num;
	//燃油类型 
	private String fule_type;
	//燃油类型名称 
	private String fule_type_text;
	//车牌颜色，1蓝色、2黄色、3白色、4黑色、5绿色
	private String car_no_color;
	
	
	
	public String getShipping_image_1() {
		return shipping_image_1;
	}
	public void setShipping_image_1(String shipping_image_1) {
		this.shipping_image_1 = shipping_image_1;
	}
	public String getShipping_image_2() {
		return shipping_image_2;
	}
	public void setShipping_image_2(String shipping_image_2) {
		this.shipping_image_2 = shipping_image_2;
	}
	public String getTask_status() {
		return task_status;
	}
	public void setTask_status(String task_status) {
		this.task_status = task_status;
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
	public Long getId(){
	    return id;
	}
	public void setId(Long id){
	    this.id = id;
	}
	public String getBrand(){
	    return brand;
	}
	public void setBrand(String brand){
	    this.brand = brand;
	}
	public Integer getOv(){
	    return ov;
	}
	public void setOv(Integer ov){
	    this.ov = ov;
	}
	public String getOv_type(){
	    return ov_type;
	}
	public void setOv_type(String ov_type){
	    this.ov_type = ov_type;
	}
	public String getIntime(){
		if(intime!=null&&intime.endsWith(".0"))
			return intime.substring(0, intime.length()-2);
	    return intime;
	}
	public void setIntime(String intime){
	    this.intime = intime;
	}
	public String getType(){
	    return type;
	}
	public void setType(String type){
	    this.type = type;
	}
	public String getEngine_no(){
	    return engine_no;
	}
	public void setEngine_no(String engine_no){
	    this.engine_no = engine_no;
	}
	public String getVin(){
	    return vin;
	}
	public void setVin(String vin){
	    this.vin = vin;
	}
	public Long getMileage(){
	    return mileage;
	}
	public void setMileage(Long mileage){
	    this.mileage = mileage;
	}
	public Long getOrg(){
	    return org;
	}
	public void setOrg(Long org){
	    this.org = org;
	}
	public String getDriver_no(){
	    return driver_no;
	}
	public void setDriver_no(String driver_no){
	    this.driver_no = driver_no;
	}
	public String getCreatetime(){
		if(createtime!=null&&createtime.endsWith(",0"))
			return createtime.substring(0, createtime.length()-2);
	    return createtime;
	}
	public void setCreatetime(String createtime){
	    this.createtime = createtime;
	}
	public String getTexts(){
	    return texts;
	}
	public void setTexts(String texts){
	    this.texts = texts;
	}
	public String getCar_image(){
	    return car_image;
	}
	public void setCar_image(String car_image){
	    this.car_image = car_image;
	}
	public String getDriver_image_1(){
	    return driver_image_1;
	}
	public void setDriver_image_1(String driver_image_1){
	    this.driver_image_1 = driver_image_1;
	}
	public String getDriver_image_2(){
	    return driver_image_2;
	}
	public void setDriver_image_2(String driver_image_2){
	    this.driver_image_2 = driver_image_2;
	}
	public String getCar_no(){
	    return car_no;
	}
	public void setCar_no(String car_no){
	    this.car_no = car_no;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public String getCar_status(){
	    return car_status;
	}
	public void setCar_status(String car_status){
	    this.car_status = car_status;
	}
	public String getObd(){
	    return obd;
	}
	public void setObd(String obd){
	    this.obd = obd;
	}
	public Integer getPlan(){
	    return plan;
	}
	public void setPlan(Integer plan){
	    this.plan = plan;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getSelf_num() {
		return self_num;
	}
	public void setSelf_num(String self_num) {
		this.self_num = self_num;
	}
	public String getFule_type() {
		return fule_type;
	}
	public void setFule_type(String fule_type) {
		this.fule_type = fule_type;
	}
	public String getFule_type_text() {
		return fule_type_text;
	}
	public void setFule_type_text(String fule_type_text) {
		this.fule_type_text = fule_type_text;
	}
	public String getCar_no_color() {
		return car_no_color;
	}
	public void setCar_no_color(String car_no_color) {
		this.car_no_color = car_no_color;
	}
	

}
