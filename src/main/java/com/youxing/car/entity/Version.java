package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**   
 * @author mars   
 * @date 2017年5月18日 下午1:07:53 
 */
public class Version {

	private Long id;
	@ApiModelProperty(value = "app类型1-安卓 2-ios")
	private String type;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createdate;
	@ApiModelProperty(value = "最新版本号")
	private String version;
	@ApiModelProperty(value = "最新版本号带小数点")
	private String versionno;
	@ApiModelProperty(value = "升级内容")
	private String content;
	@ApiModelProperty(value = "下载地址")
	private String download;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersionno() {
		return versionno;
	}
	public void setVersionno(String versionno) {
		this.versionno = versionno;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDownload() {
		return download;
	}
	public void setDownload(String download) {
		this.download = download;
	}
	
}


