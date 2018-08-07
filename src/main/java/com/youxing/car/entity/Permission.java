package com.youxing.car.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@SuppressWarnings("serial")
public class Permission implements Serializable{
	private Long id;
	private String name;
	private Long parent;
	private String status;
	private String descr;
	private Long createdby;
	private Date createdate;
	private List<Permission> children = new ArrayList<Permission>();
	private Boolean expanded = false;
	private String url;
	private String css;
	private Integer orders;
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	//是否菜单
	private String type;
	
	public Integer getOrders() {
		return orders;
	}
	public void setOrders(Integer orders) {
		this.orders = orders;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	public Long getParent(){
	    return parent;
	}
	public void setParent(Long parent){
	    this.parent = parent;
	}
	public String getStatus(){
	    return status;
	}
	public void setStatus(String status){
	    this.status = status;
	}
	public String getDescr(){
	    return descr;
	}
	public void setDescr(String descr){
	    this.descr = descr;
	}
	public Long getCreatedby(){
	    return createdby;
	}
	public void setCreatedby(Long createdby){
	    this.createdby = createdby;
	}
	public Date getCreatedate(){
	    return createdate;
	}
	public void setCreatedate(Date createdate){
	    this.createdate = createdate;
	}
	public List<Permission> getChildren() {
		return children;
	}
	public void addChildren(Permission children) {
		this.children.add(children);
	}
	public Boolean getExpanded() {
		return expanded;
	}
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}
	public void setChildren(List<Permission> children) {
		this.children = children;
	}
}
