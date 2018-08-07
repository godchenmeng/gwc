package com.youxing.car.entity;
/**   
 * @author mars   
 * @date 2017年3月24日 上午11:17:53 
 */
public class Attachment {

	private String name;
	private String url;
	private Boolean isSuccess;
	private String msg;
	private String tar;
	private String ext;
	
	
	
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getTar() {
		return tar;
	}
	public void setTar(String tar) {
		this.tar = tar;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}


