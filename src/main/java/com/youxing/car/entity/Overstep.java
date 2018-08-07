package com.youxing.car.entity;
/**
* @author jianlu
* @version 创建时间：2018年1月11日 下午3:21:30
* @ClassName 越界记录表
* @Description 类描述
*/
public class Overstep {
	
	/**
	 * 主键
	 */
	private Long id;
	
	/**
	 * 机构ID
	 */
	private Long org_id;
	
	/**
	 * 车辆ID
	 */
	private Long car_id;
	
	/**
	 * 越界地址
	 */
	private String address;
	
	
	/**
	 * 创建时间
	 */
	private String createdate;
	
	
	/**
	 * 车牌号
	 */
	private String car_no;
	
	/**
	 * 部门名称
	 */
	private String org_name;
	
	/**
	 * 车辆类型名称
	 */
	private String car_type_name;
	
	/**
	 * 违规次数
	 */
	private String fence_num;
	
	/**
	 * 触发类型
	 */
	private String fence_type;
	
	/**
	 * 发生时间
	 */
	private String happen_date;

	/**
	 * 规则ID
	 */
	private Long rid;
	
	/**
	 * 规则名称
	 */
	private String rule_name;

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	public Long getOrg_id() {
		return org_id;
	}


	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}


	public Long getCar_id() {
		return car_id;
	}


	public void setCar_id(Long car_id) {
		this.car_id = car_id;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getCreatedate() {
		return createdate;
	}


	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}


	public String getCar_no() {
		return car_no;
	}


	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}


	public String getOrg_name() {
		return org_name;
	}


	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}


	public String getCar_type_name() {
		return car_type_name;
	}


	public void setCar_type_name(String car_type_name) {
		this.car_type_name = car_type_name;
	}


	public String getFence_num() {
		return fence_num;
	}


	public void setFence_num(String fence_num) {
		this.fence_num = fence_num;
	}


	public String getFence_type() {
		return fence_type;
	}


	public void setFence_type(String fence_type) {
		this.fence_type = fence_type;
	}


	public String getHappen_date() {
		return happen_date;
	}


	public void setHappen_date(String happen_date) {
		this.happen_date = happen_date;
	}


	public Long getRid() {
		return rid;
	}


	public void setRid(Long rid) {
		this.rid = rid;
	}


	public String getRule_name() {
		return rule_name;
	}


	public void setRule_name(String rule_name) {
		this.rule_name = rule_name;
	}
	
}
