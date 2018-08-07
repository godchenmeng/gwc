package com.youxing.car.entity;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Apply implements Serializable {
	private Long id;
	@ApiModelProperty(value = "申请人id")
	private Long sq_id;
	@ApiModelProperty(value = "申请人name")
	private String sq_name;
	@ApiModelProperty(value = "申请人机构id")
	private Long sq_org;
	private String sq_org_name;
	@ApiModelProperty(value = "申请人电话")
	private String sq_mobile;
	@ApiModelProperty(value = "用车人电话")
	private String use_name;
	@ApiModelProperty(value = "用车人机构")
	private Long use_org;
	private String user_org_name;
	@ApiModelProperty(value = "用车人电话")
	private String use_mobile;
	@ApiModelProperty(value = "用车人数")
	private Integer use_number;
	@ApiModelProperty(value = "用车人级别")
	private String use_level;
	private String send;
	@ApiModelProperty(value = "计划用车数")
	private Integer plan_car_num;
	
	@ApiModelProperty(value = "计划用车时间")
	private String plan_time;
	
	@ApiModelProperty(value = "计划返回时间")
	private String plan_return;
	
	@ApiModelProperty(value = "起始地点")
	private String start_place;
	@ApiModelProperty(value = "结束地点")
	private String end_place;
	@ApiModelProperty(value = "用车类型")
	private String car;
	@ApiModelProperty(value = "用车事由")
	private String reason;
	@ApiModelProperty(value = "备注")
	private String texts;
	@ApiModelProperty(value = "审批状态")
	private String sp_status;
	@ApiModelProperty(value = "调度状态")
	private String dd_status;
	@ApiModelProperty(value = "数据状态")
	private String status;
	
	@ApiModelProperty(value = "审批时间")
	private String sp_time;
	
	@ApiModelProperty(value = "备注")
	private String return_reason;
	
	@ApiModelProperty(value = "调度时间")
	private String dd_time;
	
	@ApiModelProperty(value = "创建时间")
	private String createdate;
	
	@ApiModelProperty(value = "审批意见")
	private String sp_reason;
	@ApiModelProperty(value = "调度意见")

	private String dd_reason;
	@ApiModelProperty(value = "审批人id")

	private Long sp;
	@ApiModelProperty(value = "状态描述")
	private String status_dec;
	@ApiModelProperty(value = "是否紧急调度 1--是 2--否")
	private String type;
	private String dd_name;
	private String dd_mobile;
	private String sp_name;
	private String types;
	private String apply_status;
	private String car_no;
	private String driver_name;
	private String car_id;
	private String driver_id;
	private String repeal_reason;
	private String isread;
	private String driver_mobile;
	private String pq_status;
	private String pq_id;
	private String feedback;
	private String type_name;
	
	
	public String getDriver_mobile() {
		return driver_mobile;
	}

	public void setDriver_mobile(String driver_mobile) {
		this.driver_mobile = driver_mobile;
	}

	public String getIsread() {
		return isread;
	}

	public void setIsread(String isread) {
		this.isread = isread;
	}

	public String getCar_no() {
		return car_no;
	}

	public void setCar_no(String car_no) {
		this.car_no = car_no;
	}

	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public String getCar_id() {
		return car_id;
	}

	public void setCar_id(String car_id) {
		this.car_id = car_id;
	}

	public String getDriver_id() {
		return driver_id;
	}

	public void setDriver_id(String driver_id) {
		this.driver_id = driver_id;
	}

	public String getApply_status() {
		return apply_status;
	}

	public void setApply_status(String apply_status) {
		this.apply_status = apply_status;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDd_name() {
		return dd_name;
	}

	public void setDd_name(String dd_name) {
		this.dd_name = dd_name;
	}

	public String getDd_mobile() {
		return dd_mobile;
	}

	public void setDd_mobile(String dd_mobile) {
		this.dd_mobile = dd_mobile;
	}

	public String getSp_name() {
		return sp_name;
	}

	public void setSp_name(String sp_name) {
		this.sp_name = sp_name;
	}

	public String getSp_mobile() {
		return sp_mobile;
	}

	public void setSp_mobile(String sp_mobile) {
		this.sp_mobile = sp_mobile;
	}

	private String sp_mobile;
	
	public String getStatus_dec() {
		if("1".equals(dd_status)&&"1".equals(sp_status)){
			if("3".equals(types)){				
				return "已提交";
			}
			if("1".equals(types)){				
				return "待审批";
			}
		}
		if("2".equals(sp_status)&&"1".equals(dd_status))
		{
			if("3".equals(types)){				
				return "已审核";
			}
			if("1".equals(types)){				
				return "已通过";
			}
			if("2".equals(types)){				
				return "未调度";
			}
		}
		if("2".equals(sp_status)&&"2".equals(dd_status))
		{
			if("3".equals(types)||"2".equals(types)){				
				return "已调度";
			}
			if("1".equals(types)){				
				return "已通过";
			}
		}
		if("3".equals(sp_status)){
			if("3".equals(types)){				
				return "审批驳回";
			}
			if("1".equals(types)){				
				return "已驳回";
			}
		}
		if("3".equals(dd_status)){
			if("3".equals(types)||"2".equals(types)){				
				return "调度驳回";
			}
			if("1".equals(types)){				
				return "已通过";
			}
		}
		return status_dec;
	}

	public void setStatus_dec(String status_dec) {
		this.status_dec = status_dec;
	}

	private Long dd;

	public String getSq_name() {
		return sq_name;
	}

	public void setSq_name(String sq_name) {
		this.sq_name = sq_name;
	}

	public String getSq_org_name() {
		return sq_org_name;
	}

	public void setSq_org_name(String sq_org_name) {
		this.sq_org_name = sq_org_name;
	}

	public String getUser_org_name() {
		return user_org_name;
	}

	public void setUser_org_name(String user_org_name) {
		this.user_org_name = user_org_name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSq_id() {
		return sq_id;
	}

	public void setSq_id(Long sq_id) {
		this.sq_id = sq_id;
	}

	public Long getSq_org() {
		return sq_org;
	}

	public void setSq_org(Long sq_org) {
		this.sq_org = sq_org;
	}

	public String getSq_mobile() {
		return sq_mobile;
	}

	public void setSq_mobile(String sq_mobile) {
		this.sq_mobile = sq_mobile;
	}

	public String getUse_name() {
		return use_name;
	}

	public void setUse_name(String use_name) {
		this.use_name = use_name;
	}

	public Long getUse_org() {
		return use_org;
	}

	public void setUse_org(Long use_org) {
		this.use_org = use_org;
	}

	public String getUse_mobile() {
		return use_mobile;
	}

	public void setUse_mobile(String use_mobile) {
		this.use_mobile = use_mobile;
	}

	public Integer getUse_number() {
		return use_number;
	}

	public void setUse_number(Integer use_number) {
		this.use_number = use_number;
	}

	public String getUse_level() {
		return use_level;
	}

	public void setUse_level(String use_level) {
		this.use_level = use_level;
	}

	public String getSend() {
		return send;
	}

	public void setSend(String send) {
		this.send = send;
	}

	public Integer getPlan_car_num() {
		return plan_car_num;
	}

	public void setPlan_car_num(Integer plan_car_num) {
		this.plan_car_num = plan_car_num;
	}

	public String getPlan_time() {
		if(plan_time!=null&&plan_time.endsWith(".0")){
			return plan_time.substring(0, plan_time.length()-2);
		}
		return plan_time;
	}

	public void setPlan_time(String plan_time) {
		this.plan_time = plan_time;
	}

	public String getPlan_return() {
		if(plan_return!=null&&plan_return.endsWith(".0")){			
			return plan_return.substring(0, plan_return.length()-2);
		}
		return plan_return;
	}

	public void setPlan_return(String plan_return) {
		this.plan_return = plan_return;
	}

	public String getStart_place() {
		return start_place;
	}

	public void setStart_place(String start_place) {
		this.start_place = start_place;
	}

	public String getEnd_place() {
		return end_place;
	}

	public void setEnd_place(String end_place) {
		this.end_place = end_place;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getTexts() {
		return texts;
	}

	public void setTexts(String texts) {
		this.texts = texts;
	}

	public String getSp_status() {
		return sp_status;
	}

	public void setSp_status(String sp_status) {
		this.sp_status = sp_status;
	}

	public String getDd_status() {
		return dd_status;
	}

	public void setDd_status(String dd_status) {
		this.dd_status = dd_status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSp_time() {
		if(sp_time!=null&&sp_time.endsWith(".0")){
			return sp_time.substring(0, sp_time.length()-2);
		}
		return sp_time;
	}

	public void setSp_time(String sp_time) {
		this.sp_time = sp_time;
	}

	public String getReturn_reason() {
		return return_reason;
	}

	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}

	public String getDd_time() {
		if(dd_time!=null&&dd_time.endsWith(".0")){
			return dd_time.substring(0, dd_time.length()-2);
		}
		return dd_time;
	}

	public void setDd_time(String dd_time) {
		this.dd_time = dd_time;
	}

	public String getCreatedate() {
		if(createdate!=null&&createdate.endsWith(".0")){			
			return createdate.substring(0, createdate.length()-2);
		}
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getSp_reason() {
		return sp_reason;
	}

	public void setSp_reason(String sp_reason) {
		this.sp_reason = sp_reason;
	}

	public String getDd_reason() {
		return dd_reason;
	}

	public void setDd_reason(String dd_reason) {
		this.dd_reason = dd_reason;
	}

	public Long getSp() {
		return sp;
	}

	public void setSp(Long sp) {
		this.sp = sp;
	}

	public Long getDd() {
		return dd;
	}

	public void setDd(Long dd) {
		this.dd = dd;
	}

	public String getRepeal_reason() {
		return repeal_reason;
	}

	public void setRepeal_reason(String repeal_reason) {
		this.repeal_reason = repeal_reason;
	}

	public String getPq_status() {
		return pq_status;
	}

	public void setPq_status(String pq_status) {
		this.pq_status = pq_status;
	}

	public String getPq_id() {
		return pq_id;
	}

	public void setPq_id(String pq_id) {
		this.pq_id = pq_id;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getType_name() {
		return type_name;
	}

	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	

}
