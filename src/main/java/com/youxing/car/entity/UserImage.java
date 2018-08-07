package com.youxing.car.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserImage implements Serializable {
	
	private Long id;
	private Long uid;//用户id
	private String fullPath;//用户头像绝对路径
	private String relativePath;//用户头像相对路径
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
}
