package com.youxing.car.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@SuppressWarnings("serial")
public class Organization implements Serializable{
	
	private Boolean expanded= false;
	private String cls;
	
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	private Long id;
	private String name;
	private String short_name;
	private Long orders;
	private String address;
	private String post_code;
	private String relation;
	private String phone;
	private String mobile;
	private String texts;
	private String other;
	private Long createby;
	private Date createdate;
	private Long parent;
	private String type;
	private String status;
	private List<Organization> children = new ArrayList<Organization>();	
	private String emergency;
	private String driver;
	private String code;
	private String sex;
	private String user_name;
	private String driver_name;
	private String path;
	private String acc;
	
	
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmergency() {
		return emergency;
	}
	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}
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
	public String getShort_name(){
	    return short_name;
	}
	public void setShort_name(String short_name){
	    this.short_name = short_name;
	}
	public Long getOrders(){
	    return orders;
	}
	public void setOrders(Long orders){
	    this.orders = orders;
	}
	public String getAddress(){
	    return address;
	}
	public void setAddress(String address){
	    this.address = address;
	}
	public String getPost_code(){
	    return post_code;
	}
	public void setPost_code(String post_code){
	    this.post_code = post_code;
	}
	public String getRelation(){
	    return relation;
	}
	public void setRelation(String relation){
	    this.relation = relation;
	}
	public String getPhone(){
	    return phone;
	}
	public void setPhone(String phone){
	    this.phone = phone;
	}
	public String getMobile(){
	    return mobile;
	}
	public void setMobile(String mobile){
	    this.mobile = mobile;
	}
	public String getTexts(){
	    return texts;
	}
	public void setTexts(String texts){
	    this.texts = texts;
	}
	public String getOther(){
	    return other;
	}
	public void setOther(String other){
	    this.other = other;
	}
	public Long getCreateby(){
	    return createby;
	}
	public void setCreateby(Long createby){
	    this.createby = createby;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}
	public Long getParent(){
	    return parent;
	}
	public void setParent(Long parent){
	    this.parent = parent;
	}
	public String getType(){
	    return type;
	}
	public void setType(String type){
	    this.type = type;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public List<Organization> getChildren() {
		return children;
	}
	public void setChildren(List<Organization> children) {
		this.children = children;
	}
	public void addChildren(Organization children) {
		this.children.add(children);
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getAcc() {
		return acc;
	}
	public void setAcc(String acc) {
		this.acc = acc;
	}
	
}
