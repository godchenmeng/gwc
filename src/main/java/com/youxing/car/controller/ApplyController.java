package com.youxing.car.controller;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.dao.ControlDao;
import com.youxing.car.dao.MemberDao;
import com.youxing.car.entity.Apply;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Control;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Push;
import com.youxing.car.entity.Read;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ApplyService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.PushService;
import com.youxing.car.service.ReadService;
import com.youxing.car.service.UserService;
import com.youxing.car.utils.other.QueryUtilsWeb;

@Api(value = "apply", description = "用车管理")
@Controller
public class ApplyController {
	@Resource
	private ApplyService applyService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private UserService userService;
	@Resource
	private DriverService driverService;
	@Resource
	private CarService carService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private PushService pushService;
	@Resource
	private MemberService memberService;
	@Resource
	private ReadService readService;
	@Resource
	private MemberDao memberDao;
	@Resource
	private ControlService controlService;
	@Resource
	private QueryUtilsWeb queryUtilsWeb;

	private static Logger LOGGER = LoggerFactory.getLogger(ApplyController.class);

	/**
	 * 
	 * @author mars @date 2017年5月2日 上午11:13:43 @Description:
	 *         TODO(提交用车申请) @param @param apply @param @param id @param @param
	 *         request @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description = "提交用车申请")
	@ApiOperation(value = "提交用车申请    参数：status:1表示提交申请   2表示取消申请 ", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/add", method = RequestMethod.POST)
	@ResponseBody
	public Result add(Apply apply, @ApiParam(required = true, name = "uid", value = "用户id") Long uid,
			HttpServletRequest request) {

		Map<String, Object> map = new HashMap<String, Object>();
		if (apply == null || uid == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			if ("2".equals(apply.getStatus())) {
				if (apply.getId() == null) {
					return Result.instance(ResponseCode.missing_parameter.getCode(),
							ResponseCode.missing_parameter.getMsg());
				}
				applyService.modify(apply);
				return Result.success();
			}
			if (StringUtils.isNoneBlank(apply.getUse_mobile()) && apply.getUse_mobile().length() > 11) {
				return Result.instance(ResponseCode.param_format_error.getCode(),
						ResponseCode.param_format_error.getMsg());
			}
			if (apply.getUse_org() == null || apply.getPlan_car_num() == null || StringUtils.isBlank(apply.getCar())
					|| StringUtils.isBlank(apply.getPlan_time()) || StringUtils.isBlank(apply.getPlan_return())
					|| StringUtils.isBlank(apply.getStart_place()) || StringUtils.isBlank(apply.getEnd_place())) {
				return Result.instance(ResponseCode.missing_parameter.getCode(),
						ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_1);
			apply.setDd_status(Constant.APPLY_1);
			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());

			// apply.setUse_org(apply.getUse_org());
			// 汽车类型（1轿车 2 商务车 3 大巴车 5 救援车 6 供水车 7 高喷车 8泡沫车 9 城市主战车 10 登高平台车 11
			// 通信指挥车）
			// 是否紧急调度
			apply.setType("2");
			apply.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			apply.setUse_number(1);
			applyService.add(apply);

			// 查询刚添加的用车申请
			List<Apply> list = applyService.getNewApplyService();
			for (Apply app : list) {
				long apply_id = app.getId();
				long org = app.getSq_org();

				queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

				// 获取申请人机构或者直属部门下拥有审批权限的人员
				List<Member> idsMem = memberDao.getMemberIds(map);
				Read read = new Read();
				read.setApply_id(apply_id);
				// 1表示未读，2表示已读
				read.setIsread("1");
				for (Member mm : idsMem) {
					long mid = mm.getId();
					read.setMid(mid);
					readService.add(read);
				}
			}

			map.clear();
			map.put("type", "10");
			map.put("content", "您于" + apply.getCreatedate() + "填写的用车申请已提交，请等待审核人员处理。用车人:" + apply.getUse_name() + ";"
					+ "计划用车数:" + apply.getPlan_car_num() + ";" + "用车事由：" + apply.getReason());
			map.put("createdate", new Date());
			map.put("push", uid);
			map.put("title", "申请待处理");
			map.put("pass", 1);
			pushService.addRejectInfoService(map);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/apply/add", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月2日 下午12:52:47 @Description:
	 *         TODO(用车申请列表-按创建时间倒排) @param @param page @param @param
	 *         size @param @param status @param @param id @param @param
	 *         type @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description = "用车申请列表 ")
	@ApiOperation(value = "用车申请列表 ", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/list", method = RequestMethod.POST)
	@ResponseBody
	public Result list(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每页大小") Integer size,
			@ApiParam(value = "列表状态 提交-1，通过-2，驳回-3  ") String status, @ApiParam(value = "用户id") Long uid,
			@ApiParam(value = "申请-3 ") String type) {
		if (StringUtils.isBlank(status) || StringUtils.isBlank(type) || uid == null || page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			if (!"3".equals(type)) {
				return Result.instance(ResponseCode.param_format_error.getCode(),
						ResponseCode.param_format_error.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<Apply> apply = new ArrayList<Apply>();
			map.put("status", Constant.ADD_STATUS);
			map.put("startIdx", page * size);
			map.put("limit", size);
			map.put("sq_id", uid);
			map.put("types", type);
			if (status.equals("1")) {
				apply = applyService.pagebysubmited(map);
			} else if (status.equals("3")) {
				map.put("dd_status", status);
				apply = applyService.pagebyreturn(map);
			}
			return Result.success(apply);
		} catch (Exception e) {
			LOGGER.error("app/apply/list", e);
			return Result.error();
		}
	}

	@LogAnnotation(description = "用车申请 审批列表 ")
	@ApiOperation(value = "用车申请 审批列表 ", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/check/list", method = RequestMethod.POST)
	@ResponseBody
	public Result SpList(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每页大小") Integer size,
			@ApiParam(value = "列表状态 提交-1，通过-2，驳回-3  ") String status, @ApiParam(value = "用户id") Long uid,
			@ApiParam(value = "哪一种列表  审批-1 ") String type) {
		if (StringUtils.isBlank(status) || StringUtils.isBlank(type) || uid == null || page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			if (!"1".equals(type)) {
				return Result.instance(ResponseCode.param_format_error.getCode(),
						ResponseCode.param_format_error.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", Constant.ADD_STATUS);
			map.put("startIdx", page * size);
			map.put("limit", size);
			if ("2".equals(status) || "3".equals(status)) {
				map.put("status_dd", status);
			} else if ("1".equals(status)) {
				map.put("sp_status", status);
			} else {
				return Result.instance(ResponseCode.param_format_error.getCode(),
						ResponseCode.param_format_error.getMsg());
			}
			map.put("types", type);
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
				if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					map.put("list", org_list);
				} else {
					map.put("org", org);
				}
			}
			List<Apply> apply = applyService.pageBy(map);
			return Result.success(apply);
		} catch (Exception e) {
			LOGGER.error("app/apply/check/list", e);
			return Result.error();
		}
	}

	@LogAnnotation(description = "用车申请 调度列表")
	@ApiOperation(value = "用车申请 调度列表", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/control/list", method = RequestMethod.POST)
	@ResponseBody
	public Result controlList(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每页大小") Integer size,
			@ApiParam(value = "列表状态 提交-1，通过-2，驳回-3  ") String status, @ApiParam(value = "用户id") Long uid,
			@ApiParam(value = "哪一种列表调度-2   ") String type) {
		if (StringUtils.isBlank(status) || StringUtils.isBlank(type) || uid == null || page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			if (!"2".equals(type)) {
				return Result.instance(ResponseCode.param_format_error.getCode(),
						ResponseCode.param_format_error.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", Constant.ADD_STATUS);
			map.put("startIdx", page * size);
			map.put("limit", size);
			map.put("sp_status", Constant.APPLY_2);
			map.put("dd_status", status);
			map.put("types", type);
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
				org_list.add(org);
				if (!CollectionUtils.isEmpty(org_list)) {
					map.put("list", org_list);
				} else {
					map.put("org", org);
				}
			}
			List<Apply> apply = applyService.pageBy(map);
			return Result.success(apply);
		} catch (Exception e) {
			LOGGER.error("app/apply/control/list", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月2日 下午1:00:12 @Description:
	 *         TODO(用车申请审批) @param @param id @param @param uid @param @param
	 *         reason @param @param status @param @return 设定文件 @return Result
	 *         返回类型 @throws
	 */
	@LogAnnotation(description = "用车审批操作")
	@ApiOperation(value = "用车审批操作    status 传2表示同意审批   3表示驳回审批", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/check", method = RequestMethod.POST)
	@ResponseBody
	public Result check(@ApiParam(value = "申请id") Long id, @ApiParam(value = "用户id") Long uid,
			@ApiParam(value = "原因") String reason, @ApiParam(value = "审批结果") String status) {
		if (id == null || uid == null || StringUtils.isBlank(reason) || StringUtils.isBlank(status)) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			Apply app = applyService.findById(id);
			if (app == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (status.equals(app.getSp_status())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long sq = app.getSq_id();
			Map<String, Object> map = new HashMap<String, Object>();
			Apply apply = new Apply();
			apply.setId(app.getId());
			apply.setSp_status(status);
			apply.setSp_reason(reason);
			apply.setSp_time(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			apply.setSp(uid);
			apply.setSp_name(user.getUser_name());
			apply.setSp_mobile(user.getTel());
			applyService.modify(apply);

			if ("3".equals(status)) {
				map.put("type", "8");
				map.put("content", "您于" + app.getCreatedate() + "提交的用车申请被驳回，理由：" + reason);
				map.put("createdate", new Date());
				map.put("push", uid);
				map.put("title", "申请驳回");
				map.put("pass", 1);
				pushService.addRejectInfoService(map);
			}

			if ("2".equals(status)) {
				Push push = new Push();
				push.setCreatedate(new Date());
				push.setPush(sq);
				push.setType(Constant.NOTICE_TYPE_SP);
				push.setPass(Constant.PASS);
				push.setContent("您于" + app.getCreatedate() + "申请的用车审批已经通过");
				push.setTitle("申请审批通过");
				pushService.add(push);
				if (sq != null) {
					User sqU = userService.findById(sq);
					if (sqU != null) {
						String alias = sqU.getAlias();
						String plat = sqU.getPlatform();
						if (StringUtils.isNotBlank(alias)) {
							JPushUtils.SendAlert("用车申请审批", "您于" + app.getCreatedate() + "申请的用车审批已经通过", plat, alias);
						}
					}
				}
			}
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/apply/check", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月2日 下午1:14:37 @Description:
	 *         TODO(驳回调度) @param @param id @param @param uid @param @param
	 *         reason @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description = "驳回调度")
	@ApiOperation(value = "驳回调度  这个表示驳回调度  ", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/return", method = RequestMethod.POST)
	@ResponseBody
	public Result returnApply(@ApiParam(value = "申请id") Long id, @ApiParam(value = "用户id") Long uid,
			@ApiParam(value = "原因") String reason, String car_id) {
		if (id == null || StringUtils.isBlank(reason) || uid == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			Apply app = applyService.findById(id);
			if (app == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.APPLY_3.equals(app.getSp_status())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Apply apply = new Apply();
			apply.setId(app.getId());
			apply.setDd_status(Constant.APPLY_3);
			apply.setDd_reason(reason);
			apply.setDd_time(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			apply.setDd(uid);
			apply.setDd_name(user.getUser_name());
			apply.setDd_mobile(user.getTel());
			applyService.modify(apply);

			map.put("type", "9");
			map.put("content", "您于" + app.getCreatedate() + "提交的用车申请被调度人员驳回，理由：" + reason);
			map.put("createdate", new Date());
			map.put("push", uid);
			map.put("title", "调度驳回");
			map.put("pass", 1);
			pushService.addRejectInfoService(map);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/apply/return", e);
			return Result.error();
		}
	}

	@LogAnnotation(description = "用车调度")
	@ApiOperation(value = "用车调度，表示同意调度 即派遣的意思", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/control", method = RequestMethod.POST)
	@ResponseBody
	public Result control(Long id, Long uid, @ApiParam(value = "type=0代表正常调度1-代表追加调度") String type,
			@RequestParam(value = "car[]") String[] car, @RequestParam(value = "driver[]") String[] driver) {
		if (id == null || uid == null || car.length == 0 || driver.length == 0 || StringUtils.isBlank(type)) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		if (car.length != driver.length) {
			return Result.instance(ResponseCode.parameter_length.getCode(), ResponseCode.parameter_length.getMsg());
		}
		try {
			Apply app = applyService.findById(id);
			if (app == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if ("0".equals(type)) {
				if (Constant.APPLY_2.equals(app.getDd_status())) {
					return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
				}
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Apply apply = new Apply();
			apply.setDd_name(user.getUser_name());
			apply.setDd_mobile(user.getTel());
			applyService.addControl(id, car, driver, uid);
			for (String dr : driver) {
				Driver d = driverService.findById(Long.parseLong(dr));
				d.setTask_status("2");
				for (int i = 0; i < driver.length; i++) {
					String id1 = driver[i];
					long id2 = Long.parseLong(id1);
					d.setId(id2);
				}

				driverService.modify(d);
				Car c = new Car();
				for (int i = 0; i < car.length; i++) {
					long id1 = Long.parseLong(car[i]);
					c.setId(id1);
					c.setTask_status("2");

				}
				carService.modify(c);

				// Long mid = d.getUid();
				map.put("mid", uid);
				map.put("status", Constant.ADD_STATUS);
				User u = userService.findByMap(map);
				// if (u != null) {
				Push push = new Push();
				push.setCreatedate(new Date());
				push.setPush(uid);
				push.setType(Constant.NOTICE_TYPE_DD);
				push.setPass(Constant.PASS);
				push.setContent("您有一个新的任务请注意查收！");
				push.setTitle("调度成功");
				pushService.add(push);

				/*
				 * String alias = u.getAlias(); String plat = u.getPlatform();
				 * if (StringUtils.isNotBlank(alias)) {
				 * JPushUtils.SendAlert("调度任务", "您有一个新的任务请注意查收！", plat, alias);
				 * }
				 */

			}
			// }
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/apply/control", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月13日 下午5:12:34 @Description:
	 * TODO(紧急调度) @param @param apply @param @param uid @param @param
	 * car @param @param driver @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description = "紧急调度")
	@ApiOperation(value = "紧急调度", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/apply/emergent", method = RequestMethod.POST)
	@ResponseBody
	public Result emergent(@ApiParam(value = "用车申请") Apply apply, @ApiParam(value = "登录app的用户id") Long uid,
			@RequestParam(value = "cars[]") String[] cars, @RequestParam(value = "driver[]") String[] driver) {

		Map<String, Object> map = new HashMap<String, Object>();
		if (uid == null || apply == null || cars.length == 0 || driver.length == 0) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		if (cars.length != driver.length) {
			return Result.instance(ResponseCode.parameter_length.getCode(), ResponseCode.parameter_length.getMsg());
		}
		try {
			if (apply.getUse_org() == null || StringUtils.isBlank(apply.getCar())
					|| StringUtils.isBlank(apply.getPlan_time()) || StringUtils.isBlank(apply.getPlan_return())
					|| StringUtils.isBlank(apply.getStart_place()) || StringUtils.isBlank(apply.getEnd_place())) {
				return Result.instance(ResponseCode.missing_parameter.getCode(),
						ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_1);
			apply.setDd_status(Constant.APPLY_1);
			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());
			apply.setDd_name(user.getUser_name());
			apply.setSp_name(user.getUser_name());
			apply.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			applyService.add(apply, driver, cars, uid);

			// 查询刚添加的用车申请
			List<Apply> list = applyService.getNewApplyService();
			for (Apply app : list) {
				long apply_id = app.getId();
				long org = app.getSq_org();

				queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

				// 获取申请人机构或者直属部门下拥有审批权限的人员
				List<Member> idsMem = memberDao.getMemberIds(map);
				Read read = new Read();
				read.setApply_id(apply_id);
				// 1表示未读，2表示已读
				read.setIsread("1");
				for (Member mm : idsMem) {
					long mid = mm.getId();
					read.setMid(mid);
					readService.add(read);
				}
			}

			Driver d = new Driver();

			for (String driver_id : driver) {
				d.setId(Long.parseLong(driver_id));
			}
			d.setTask_status("2");
			driverService.updateTaskStatusService(d);

			Car c = new Car();

			for (String car_id : cars) {
				c.setId(Long.parseLong(car_id));
			}
			c.setTask_status("2");
			carService.modify(c);

			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/apply/emergent", e);
			return Result.error();
		}
	}

	/**
	 * 用车申请撤销
	 * 
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "用车申请撤销")
	@ApiOperation(value = "用车申请撤销", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "app/apply/repeal")
	@RequestMapping(value = "app/apply/repeal", method = RequestMethod.POST)
	@ResponseBody
	public Response getApplySqRepealApp(Long id, String sp_status, HttpServletRequest request) {

		try {
			Apply app = new Apply();
			if (id != null) {
				app.setId(id);
			}
			/*
			 * if(StringUtils.isBlank(repeal_reason)){ return new
			 * FailedResponse("请填写撤销原因！"); }else{
			 * app.setRepeal_reason(repeal_reason); }
			 */

			if (StringUtils.isNotEmpty(sp_status)) {

				if ("1".equals(sp_status)) {
					app.setSp_status("4");
				}
				if ("2".equals(sp_status)) {
					app.setDd_status("4");
				}
				applyService.modify(app);

				// 撤销以后将车辆和驾驶员归队可接受任务
				Control con = new Control();
				con.setApply_id(id);
				con = controlService.findBy(con);
				// 新建申请还未派遣任务
				if (con != null) {
					Long car_id = con.getCar_id();
					Long driver_id = con.getDriver_id();

					Driver d = new Driver();
					d.setId(driver_id);
					d.setTask_status("1");
					driverService.updateTaskStatus(d);

					Car c = new Car();
					c.setId(car_id);
					c.setTask_status("1");
					carService.modify(c);

					return new SuccessResponse();
				} else {
					return new SuccessResponse();
				}
			} else {
				return new FailedResponse();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse();
		}

	}

	/**
	 * ======================================web
	 */

	@Resource
	private ControlDao controlDao;

	@RequiresPermissions(value = "apply/list")
	@RequestMapping("apply/list")
	public String list() {
		return "apply/list";
	}

	@RequiresPermissions(value = "apply/sp")
	@RequestMapping("apply/sp")
	public String sp() {
		return "apply/sp";
	}

	/**
	 * 
	 * @author mars @date 2017年3月31日 上午9:50:17 @Description:
	 * TODO(申请审批列表) @param @param limit @param @param pageIndex @param @param
	 * request @param @return 设定文件 @return Page 返回类型 @throws
	 */
	@LogAnnotation(description = "审批分页查询")
	@ApiOperation(value = "审批分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/apply/sp")
	@RequestMapping(value = "web/apply/sp/page", method = RequestMethod.POST)
	@ResponseBody
	public Page applySp(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		String use_name = request.getParameter("use_name");
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}
		String apply_status = request.getParameter("apply_status");
		if (StringUtils.isNotBlank(apply_status)) {
			if (!"3".equals(apply_status)) {
				map.put("apply_status", apply_status);
			}
			if ("2".equals(apply_status)) {
				map.remove("apply_status");
				map.put("all", 1);
			}
		} else {
			map.put("apply_status", "1");
		}
		String startDate = request.getParameter("startDate");
		if (StringUtils.isNotBlank(startDate)) {
			map.put("start", startDate + " 00:00:00");
		}
		String endDate = request.getParameter("endDate");
		if (StringUtils.isNotBlank(endDate)) {
			map.put("end", endDate + " 23:59:59");
		}
		// User user = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
		List<Apply> list = applyService.pageByWebService(map);
		int recodes = applyService.countByWeb(map);
		return new Page<Apply>(list, recodes, limit);
	}

	// 调度页面
	@RequiresPermissions(value = "apply/dd")
	@RequestMapping("apply/dd")
	public String d() {
		return "apply/dd";
	}

	/**
	 * 
	 * @author mars @date 2017年3月31日 下午2:29:50 @Description:
	 * TODO(调度页面分页显示) @param @param limit @param @param pageIndex @param @param
	 * request @param @return 设定文件 @return Page 返回类型 @throws
	 */
	@LogAnnotation(description = "调度信息分页查询")
	@ApiOperation(value = "调度信息分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/dd")
	@RequestMapping(value = "web/apply/dd/page", method = RequestMethod.POST)
	@ResponseBody
	public Page applyDp(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		map.put("apply_status", "2");
		String apply_status = request.getParameter("apply_status");
		if (StringUtils.isNotBlank(apply_status) && !"-1".equals(apply_status)) {
			map.put("dd_status", apply_status);
		} else {
			map.put("dd_status", "1");
		}
		String use_name = request.getParameter("use_name");
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}
		String hide_org = request.getParameter("hide_org");
		if (StringUtils.isNotBlank(hide_org)) {
			// map.put("use_org", hide_org);
			Long org = Long.parseLong(hide_org);
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> o_list = organizationService.getOrganizationUser(userOrg, org);
				if (!CollectionUtils.isEmpty(o_list)) {
					o_list.add(org);
					map.put("org_list", o_list);
				} else {
					map.put("use_org", org);
				}
			}
		}
		// User user = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
		List<Apply> list = applyService.pageByWebService(map);
		int recodes = applyService.countByWeb(map);
		return new Page<Apply>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars @date 2017年3月30日 下午3:34:08 @Description:
	 * TODO(分页显示用车申请) @param @param limit @param @param pageIndex @param @param
	 * request @param @return 设定文件 @return Page 返回类型 @throws
	 */
	@LogAnnotation(description = "用车申请分页查询")
	@ApiOperation(value = "用车申请分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/list")
	@RequestMapping(value = "web/apply/page", method = RequestMethod.POST)
	@ResponseBody
	public Page apply(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		String use_name = request.getParameter("use_name");
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}
		String apply_status = request.getParameter("apply_status");
		if (StringUtils.isNotBlank(apply_status) && !"-1".equals(apply_status)) {
			map.put("apply_status", apply_status);
		}
		String hide_org = request.getParameter("hide_org");
		if (StringUtils.isNotBlank(hide_org)) {
			Long org = Long.parseLong(hide_org);
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> o_list = organizationService.getOrganizationUser(userOrg, org);
				if (!CollectionUtils.isEmpty(o_list)) {
					o_list.add(org);
					map.put("org_list", o_list);
				} else {
					map.put("use_org", org);
				}
			}
		}
		// User user = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}

		List<Apply> list = applyService.pageByWebService(map);
		int recodes = applyService.countByWeb(map);
		return new Page<Apply>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars @date 2017年3月30日 下午4:29:01 @Description:
	 * TODO(新增用车申请) @param @param apply @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "用车申请新增")
	@ApiOperation(value = "用车申请新增", httpMethod = "POST", response = Result.class, notes = "必须的参数：apply:{申请用车信息对象},plan_use_time:计划用车时间,plan_return_time:计划返回时间")
	@RequiresPermissions(value = "web/apply/add")
	@RequestMapping(value = "web/apply/add", method = RequestMethod.POST)
	@ResponseBody
	public Response add(Apply apply, String plan_return_time, String plan_use_time, HttpServletRequest request) {
		if (apply == null || StringUtils.isBlank(plan_use_time) || StringUtils.isBlank(plan_return_time)) {
			return new FailedResponse("参数错误！");
		}
		try {
			apply.setPlan_time(plan_use_time);
			apply.setPlan_return(plan_return_time);
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_1);
			apply.setDd_status(Constant.APPLY_1);
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());
			apply.setCreatedate(DateUtils.getDateTime());
			// 1--是 2--否 是否紧急调度
			apply.setType("2");
			applyService.add(apply);

			/*
			 * HashMap<String, Object> map = new HashMap<String, Object>();
			 * //查询刚添加的用车申请 List<Apply> list =
			 * applyService.getNewApplyService(); for(Apply app:list){ long org
			 * = app.getSq_org();
			 * 
			 * queryUtilsWeb.queryChildOrgsByParentId(org,map,"list");
			 * 
			 * List<Member> idsMem = memberDao.getMemberIds(map); Read read =new
			 * Read(); read.setApply_id(apply_id); read.setIsread("1");
			 * for(Member mm : idsMem){ long mid = mm.getId();暂时不用
			 * read.setMid(mid); readService.add(read); }
			 * 
			 * }
			 */

			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}

	}

	/**
	 * 
	 * @author mars @date 2017年3月31日 下午1:47:54 @Description:
	 * TODO(审批提交) @param @param id @param @param sp_reason @param @return
	 * 设定文件 @return Response 返回类型 @throws
	 */
	@LogAnnotation(description = "车辆审批")
	@ApiOperation(value = "车辆审批", httpMethod = "POST", response = Result.class, notes = "必须的参数：id:申请用车记录id,sp_reason:审批意见, sp_status:审批结果状态code")
	@RequiresPermissions(value = "web/check/apply")
	@RequestMapping(value = "web/check/apply", method = RequestMethod.POST)
	@ResponseBody
	public Response check(String id, String sp_reason, String sp_status, HttpServletRequest request) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(sp_status)) {
			return new FailedResponse("参数错误！");
		}
		Apply app = applyService.findById(Long.parseLong(id));
		if (app == null) {
			return new FailedResponse("查询不到这个用车申请！");
		}
		if (sp_status.equals(app.getSp_status())) {
			return new FailedResponse("对不起不能重复操作！");
		}
		try {
			Apply apply = new Apply();
			apply.setId(app.getId());
			apply.setSp_status(sp_status);
			apply.setSp_reason(sp_reason);
			apply.setSp_time(DateUtils.getDateTime());
			// User user = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			apply.setSp(user.getId());
			applyService.modify(apply);
			if ("2".equals(sp_status)) {
				Push push = new Push();
				push.setCreatedate(new Date());
				Long sq = app.getSq_id();
				push.setPush(sq);
				push.setType(Constant.NOTICE_TYPE_SP);
				push.setPass(Constant.PASS);
				push.setTitle("申请审批通过");
				push.setContent("您于" + app.getCreatedate() + "申请的用车审批已经通过");
				pushService.add(push);
				if (sq != null) {
					User sqU = userService.findById(sq);
					if (sqU != null) {
						String alias = sqU.getAlias();
						String plat = sqU.getPlatform();
						if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(plat)) {
							JPushUtils.SendAlert("用车申请审批", "您于" + app.getCreatedate() + "申请的用车审批已经通过", plat, alias);
						}
					}
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年3月31日 下午5:11:23 @Description:
	 * TODO(调度驳回) @param @param id @param @param sp_reason @param @param
	 * sp_status @param @return 设定文件 @return Response 返回类型 @throws
	 */
	@LogAnnotation(description = "车辆驳回")
	@ApiOperation(value = "车辆驳回", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:用车申请记录id}")
	@RequiresPermissions(value = "web/check/select")
	@RequestMapping(value = "web/check/select", method = RequestMethod.POST)
	@ResponseBody
	public Response returnApply(String id, String reason, HttpServletRequest request) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(reason)) {
			return new FailedResponse("参数错误！");
		}
		Apply app = applyService.findById(Long.parseLong(id));
		if (app == null) {
			return new FailedResponse("查询不到这个用车申请！");
		}
		if (Constant.APPLY_3.equals(app.getDd_status())) {
			return new FailedResponse("对不起不能重复驳回！");
		}
		try {
			Apply apply = new Apply();
			apply.setId(app.getId());
			apply.setDd_status(Constant.APPLY_3);
			apply.setDd_reason(reason);
			apply.setDd_time(DateUtils.getDateTime());
			// User user = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);

			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			apply.setDd(user.getId());
			applyService.modify(apply);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年4月5日 上午11:34:49 @Description:
	 * TODO(调度分配车辆和驾驶员) @param @param id @param @param car @param @param
	 * driver @param @return 设定文件 @return Response 返回类型 @throws
	 */
	@LogAnnotation(description = "车辆调度")
	@ApiOperation(value = "车辆调度", httpMethod = "POST", response = Result.class, notes = "必须的参数： id:用车申请记录id,cars:idsArray[车辆ID数组],drivers:idsArray[驾驶员ID数组]")
	@RequiresPermissions(value = "web/control/apply")
	@RequestMapping(value = "web/control/apply", method = RequestMethod.POST)
	@ResponseBody
	public Response controlApply(HttpServletRequest request, String id, @RequestParam(value = "car[]") String[] car,
			@RequestParam(value = "driver[]") String[] driver, String car_id, String driver_id) {

		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(driver_id) && StringUtils.isNotBlank(car_id)) {
			map.put("car_id", car_id);
			map.put("driver_id", driver_id);
			map.put("apply_id", id);
			controlService.removeApply(map);
			Apply apply = new Apply();
			apply.setId(Long.parseLong(id));
			apply.setDd_status("1");
			applyService.modify(apply);
		}

		if (StringUtils.isBlank(id) || car.length == 0 || driver.length == 0) {
			return new FailedResponse("参数错误！");
		}
		Apply app = applyService.findById(Long.parseLong(id));
		if (app == null) {
			return new FailedResponse("查询不到这个用车申请！");
		}
		if (Constant.APPLY_2.equals(app.getDd_status())) {
			return new FailedResponse("对不起不能重复派遣车辆！");
		}
		try {

			User u1 = new User();
			String id1 = request.getParameter("keyid");
			u1.setId(Long.parseLong(id1));
			User user = userService.findBy(u1);

			applyService.addControlWeb(id, car, driver, user.getId());

			int plan_car_num = car.length;
			Apply apply = new Apply();
			apply.setPlan_car_num(plan_car_num);
			apply.setId(Long.parseLong(id));
			applyService.modify(apply);

			for (String dr : driver) {
				Driver d = driverService.findById(Long.parseLong(dr));
				Long mid = d.getUid();
				Member mm = memberService.findById(mid);
				String driver_name = mm.getName();
				map.put("driver_name", driver_name);
				map.put("updatedate", new Date());
				for (String c : car) {
					Device dd = new Device();
					dd.setCar_id(Long.parseLong(c));
					Device de = deviceService.findBy(dd);
					String device = de.getDevice();
					map.put("device", device);
				}

				deviceService.updateDriverByDeviceService(map);
				if (mid != null) {
					map.put("mid", mid);
					map.put("status", Constant.ADD_STATUS);
					User u = userService.findByMap(map);
					if (u != null) {
						Push push = new Push();
						String date = DateUtils.getDateTime();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date1 = sdf.parse(date);

						push.setCreatedate(date1);
						push.setPush(u.getId());
						push.setType(Constant.NOTICE_TYPE_DD);
						push.setContent("您有一个新的任务请注意查收！");
						pushService.add(push);
						String alias = u.getAlias();
						String plat = u.getPlatform();
						if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(plat)) {
							JPushUtils.SendAlert("调度任务", "您有一个新的任务请注意查收！", plat, alias);
						}
					}
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 紧急调度
	 * 
	 * @param apply
	 * @param car
	 * @param driver
	 *            * @return
	 */
	@LogAnnotation(description = "紧急调度")
	@ApiOperation(value = "紧急调度", httpMethod = "POST", response = Result.class, notes = "必须的参数：apply:{申请用车信息对象},cars:idsArray[车辆ID数组],drivers:idsArray[驾驶员ID数组],plan_use_time:计划用车时间,plan_return_time:计划返回时间")
	@RequestMapping(value = "web/emergency/apply", method = RequestMethod.POST)
	@ResponseBody
	public Response emergencyApply(HttpServletRequest request, Apply apply, @RequestParam(value = "cars[]") Long[] car,
			@RequestParam(value = "drivers[]") Long[] driver) {
		String plan_use_time = request.getParameter("plan_use_time");
		String plan_return_time = request.getParameter("plan_return_time");
		if (apply == null || StringUtils.isBlank(plan_use_time) || StringUtils.isBlank(plan_return_time)) {
			return new FailedResponse("参数错误！");
		}
		if (car.length == 0 || driver.length == 0) {
			return new FailedResponse("参数错误！");
		}
		try {
			apply.setPlan_time(plan_use_time);
			apply.setPlan_return(plan_return_time);
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_2);
			apply.setDd_status(Constant.APPLY_2);
			// User user = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);

			User u1 = new User();
			String id1 = request.getParameter("keyid");
			u1.setId(Long.parseLong(id1));
			User user = userService.findBy(u1);

			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());
			apply.setCreatedate(DateUtils.getDateTime());
			// 1--是 2--否 是否紧急调度
			apply.setType("1");
			applyService.addEmControl(apply, car, driver, user.getId());
			Map<String, Object> map = new HashMap<String, Object>();
			for (Long dr : driver) {
				Driver d = driverService.findById(dr);
				Long mid = d.getUid();
				if (mid != null) {
					map.put("mid", mid);
					map.put("status", Constant.ADD_STATUS);
					User u = userService.findByMap(map);
					if (u != null) {
						Push push = new Push();
						push.setCreatedate(new Date());
						push.setPush(u.getId());
						push.setType(Constant.NOTICE_TYPE_DD);
						push.setContent("您有一个新的任务请注意查收！");
						pushService.add(push);
						String alias = u.getAlias();
						String plat = u.getPlatform();
						if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(plat)) {
							JPushUtils.SendAlert("调度任务", "您有一个新的任务请注意查收！", plat, alias);
						}
					}
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 追加调度
	 * 
	 * @param aid
	 * @param request
	 * @param car
	 * @param driver
	 * @return
	 */
	@LogAnnotation(description = "追加调度")
	@ApiOperation(value = "追加调度", httpMethod = "POST", response = Result.class, notes = "必须的参数：aid:用车申请记录id,cars:idsArray[车辆ID数组],drivers:idsArray[驾驶员ID数组]")
	@RequestMapping(value = "web/apply/add/control", method = RequestMethod.POST)
	@ResponseBody
	public Response applyAddControl(Long aid, HttpServletRequest request, @RequestParam(value = "cars[]") Long[] car,
			@RequestParam(value = "drivers[]") Long[] driver) {
		try {
			if (car.length == 0 || driver.length == 0 || car.length != driver.length || aid == null) {
				return new FailedResponse("参数错误！");
			}
			Apply app = applyService.findById(aid);
			if (app == null) {
				return new FailedResponse("查询不到这个用车申请！");
			}
			// User user = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			Long uid = user.getId();
			if (uid == null) {
				return new FailedResponse("用户id为空！");
			}
			for (int i = 0; i < car.length; i++) {
				Control ct = new Control();
				ct.setApply_id(aid);
				Long car_id = car[i];
				ct.setCar_id(car_id);
				Long dirver_id = driver[i];
				ct.setDriver_id(dirver_id);
				ct.setStatus(Constant.ADD_STATUS);
				Control find = controlDao.findBy(ct);
				if (find != null) {
					return new FailedResponse("调度信息中存在追加的派遣信息，请核实后追加！");
				}
			}
			applyService.addControlbyApply(aid, car, driver, user.getId());
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 
	 * @param limit
	 * @param pageIndex
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）用车申请分页查询")
	@ApiOperation(value = "（二期版本）用车申请分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/get/apply/page")
	@RequestMapping(value = "web/get/apply/page", method = RequestMethod.POST)
	@ResponseBody
	public Page getApplyPage(Integer limit, Integer pageIndex, String start_time, String end_time, String use_name,
			String apply_status, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Apply> list = new ArrayList<Apply>();
		int recodes = 0;
		if (pageIndex != null && limit != null) {
			map.put("startIdx", limit * pageIndex);
			map.put("limit", limit);
		}

		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		map.put("mid", id1);
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();

		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNotBlank(start_time) && StringUtils.isNotBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		if (StringUtils.isNotBlank(apply_status) && !"-1".equals(apply_status)) {
			if ("1".equals(apply_status)) {// 待审批
				map.put("sp_status", "1");
				map.put("dd_status", "1");
			}
			if ("2".equals(apply_status)) {// 待调度
				map.put("sp_status", "2");
				map.put("dd_status", "1");
			}
			if ("3".equals(apply_status)) {// 待车车
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				map.put("yca_status", "1");
			}
			if ("4".equals(apply_status)) {// 待完成
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				int[] ids = { 2, 3, 5 };
				map.put("yca_status_list", ids);
			}
			if ("5".equals(apply_status)) {// 已完成
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				map.put("yca_status", "6");
			}
			if ("6".equals(apply_status)) {// 驳回（审批+调度）
				map.put("dd_status_2", "1");
			}
			list = applyService.getApplyPageService(map);
			recodes = applyService.getApplyCountService(map);

		} else {
			list = applyService.getApplyPageService(map);
			recodes = applyService.getApplyCountService(map);
		}
		// readService.updateIsReadService(map); 已读未读暂时不启用
		return new Page<Apply>(list, recodes, limit);
	}

	/**
	 * （二期版本）用车申请统计 (统计条数)
	 * 
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）用车申请统计")
	@ApiOperation(value = "（二期版本）用车申请统计", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/count")
	@RequestMapping(value = "web/apply/count", method = RequestMethod.POST)
	@ResponseBody
	public Response getApplyCount(HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		String id = request.getParameter("keyid");

		User u = new User();
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long org = user.getOrg();

		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		Object obj = applyService.getAllNum(map);
		if (obj != null) {
			return new ObjectResponse<Object>(obj);
		} else {
			return new FailedResponse();
		}

	}

	/**
	 * （二期版本）用车申请数据导出
	 * 
	 * @param start_time
	 * @param end_time
	 * @param use_name
	 * @param apply_status
	 * @param request
	 * @param response
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）用车申请数据导出")
	@ApiOperation(value = "（二期版本）用车申请数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/export")
	@RequestMapping(value = "web/apply/export", method = RequestMethod.GET)
	@ResponseBody
	public Response ExportApplyExcel(String start_time, String end_time, String use_name, String apply_status,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		List<Apply> list = new ArrayList<Apply>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "apply数据文件" + sdf.format(System.currentTimeMillis()) + ".xls";
		// sheet名
		String sheetName = "数据明细";
		// 列名:用车人，用车人数，申请时间，开始时间，目的地，用车事由，状态
		String[] title = new String[] { "用车人", "用车人数", "申请时间", "开始时间", "目的地", "用车事由", "状态" };

		map.clear();
		if (StringUtils.isNoneBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNotBlank(apply_status)) {
			if ("1".equals(apply_status)) {// 待审批
				map.put("sp_status", "1");
				map.put("dd_status", "1");
			}
			if ("2".equals(apply_status)) {// 待调度
				map.put("sp_status", "2");
				map.put("dd_status", "1");
			}
			if ("3".equals(apply_status)) {// 待车车
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				map.put("yca_status", "1");
			}
			if ("4".equals(apply_status)) {// 待完成
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				int[] ids = { 2, 3, 5 };
				map.put("yca_status_list", ids);
			}
			if ("5".equals(apply_status)) {// 已完成
				map.put("sp_status", "2");
				map.put("dd_status", "2");
				map.put("yca_status", "6");
			}
			if ("6".equals(apply_status)) {// 驳回（审批+调度）
				map.put("dd_status_2", "1");
			}
			list = applyService.exportApplyEXcelService(map);
		}

		// 内容list
		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			Apply app = list.get(i);
			values[i][0] = app.getUse_name();
			Integer use_number = app.getUse_number();
			values[i][1] = String.valueOf(use_number);
			values[i][2] = app.getCreatedate();
			values[i][3] = app.getPlan_time();
			values[i][4] = app.getEnd_place();
			values[i][5] = app.getReason();

			String app_status = app.getApply_status();
			if ("1".equals(app_status)) {
				values[i][6] = "待审批";
			}
			if ("2".equals(app_status)) {
				values[i][6] = "待调度";
			}
			if ("3".equals(app_status)) {
				values[i][6] = "待出车";
			}
			if ("4".equals(app_status)) {
				values[i][6] = "待完成";
			}
			if ("5".equals(app_status)) {
				values[i][6] = "已完成";
			}
			if ("6".equals(app_status)) {
				values[i][6] = "驳回";
			}
			if ("7".equals(app_status)) {
				values[i][6] = "已撤销";
			}
			if ("8".equals(app_status)) {
				values[i][6] = "作废";
			}
			if ("9".equals(app_status)) {
				values[i][6] = "已反馈";
			}
		}
		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values, null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SuccessResponse();
	}

	// 自定义下载文件
	public void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				// 浏览器下载中文名文件兼容性处理
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// 自动匹配文件类型
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * （二期版本）获取当前登录用户机构或下属机构用车申请最新记录
	 * 
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）获取当前登录用户机构或下属机构用车申请最新记录")
	@ApiOperation(value = "（二期版本）获取当前登录用户机构或下属机构用车申请最新记录", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/get/first/apply")
	@RequestMapping(value = "web/get/first/apply", method = RequestMethod.GET)
	@ResponseBody
	public Response getFirstApply(HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("status", Constant.ADD_STATUS);

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
		Object obj = applyService.getApplyNewFirst(map);
		return new ObjectResponse<Object>(obj);
	}

	/**
	 * 二期版本获取审批列表
	 * 
	 * @param limit
	 * @param pageIndex
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "审批分页查询")
	@ApiOperation(value = "审批分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/get/apply/sp/page")
	@RequestMapping(value = "web/get/apply/sp/page", method = RequestMethod.POST)
	@ResponseBody
	public Page getApplySPPage(int limit, int pageIndex, String start_time, String end_time, String use_name,
			String apply_status, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Apply> list = new ArrayList<Apply>();
		int recodes = 0;
		map.put("startIdx", limit * pageIndex);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNoneBlank(start_time)) {
			map.put("start_time", start_time + " 00:00:00");
		}
		if (StringUtils.isNoneBlank(end_time)) {
			map.put("end_time", end_time + " 23:59:59");
		}

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"-1".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("sp_status", "1");
					map.put("dd_status", "1");
				}
				if ("2".equals(apply_status)) {
					List<String> ddList = new ArrayList<String>();
					map.put("sp_status", "2");
					ddList.add("1");
					ddList.add("2");
					ddList.add("3");
					map.put("ddlist", ddList);
				}
				if ("3".equals(apply_status)) {
					map.put("sp_status", "3");
					map.put("dd_status", "1");
				}
				if ("4".equals(apply_status)) {
					map.put("sp_status", "4");
					map.put("dd_status", "1");
				}
				list = applyService.getApplySpPageService(map);
				recodes = applyService.getSpCountService(map);
			} else {
				list = applyService.getApplySpPageService(map);
				recodes = applyService.getSpCountService(map);
			}
		} else {
			list = applyService.getApplySpPageService(map);
			recodes = applyService.getSpCountService(map);
		}
		return new Page<Apply>(list, recodes, limit);
	}

	/**
	 * 二期审批列表数据导出
	 * 
	 * @param start_time
	 * @param end_time
	 * @param use_name
	 * @param apply_status
	 * @param request
	 * @param response
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）用车审批数据导出")
	@ApiOperation(value = "（二期版本）用车审批数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/sp/export")
	@RequestMapping(value = "web/apply/sp/export", method = RequestMethod.GET)
	@ResponseBody
	public Response ExportSp(String start_time, String end_time, String use_name, String apply_status,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		List<Apply> list = new ArrayList<Apply>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "审批数据文件" + sdf.format(System.currentTimeMillis()) + ".xls";
		// sheet名
		String sheetName = "审批数据明细";
		// 列名:用车人，用车人数，申请时间，开始时间，目的地，用车事由，状态
		String[] title = new String[] { "用车人", "用车人数", "用车类型", "开始时间", "结束时间", "上车地点", "目的地", "用车事由", "状态" };

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNoneBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"0".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("sp_status", "1");
					map.put("dd_status", "1");
				}
				if ("2".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "1");
				}
				if ("3".equals(apply_status)) {
					map.put("sp_status", "3");
					map.put("dd_status", "1");
				}
				if ("4".equals(apply_status)) {
					map.put("sp_status", "4");
					map.put("dd_status", "1");
				}
				list = applyService.exportSpService(map);
			} else {
				list = applyService.exportSpService(map);
			}
		} else {
			list = applyService.exportSpService(map);
		}

		// 内容list
		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			Apply app = list.get(i);
			values[i][0] = app.getUse_name();
			Integer use_number = app.getUse_number();
			values[i][1] = String.valueOf(use_number);
			String car = app.getCar();
			if ("1".equals(car)) {
				values[i][2] = "轿车";
			}
			if ("2".equals(car)) {
				values[i][2] = "商务车-9座以下";
			}
			if ("3".equals(car)) {
				values[i][2] = "专项作业车";
			}
			if ("4".equals(car)) {
				values[i][2] = "货车";
			}
			if ("5".equals(car)) {
				values[i][2] = "大巴车";
			}
			values[i][3] = app.getPlan_time();
			values[i][4] = app.getPlan_return();
			values[i][5] = app.getStart_place();
			values[i][6] = app.getEnd_place();
			values[i][7] = app.getReason();

			String app_status = app.getApply_status();
			if ("1".equals(app_status)) {
				values[i][8] = "待办";
			}
			if ("2".equals(app_status)) {
				values[i][8] = "同意";
			}
			if ("3".equals(app_status)) {
				values[i][8] = "驳回";
			}
			if ("4".equals(app_status)) {
				values[i][8] = "已撤销";
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values, null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SuccessResponse();
	}

	/**
	 * 用车申请撤销
	 * 
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "用车申请撤销")
	@ApiOperation(value = "用车申请撤销", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/apply/repeal")
	@RequestMapping(value = "web/apply/repeal", method = RequestMethod.POST)
	@ResponseBody
	public Response getApplySqRepeal(Long id, String sp_status, String repeal_reason, HttpServletRequest request) {

		try {
			Apply app = new Apply();
			if (id != null) {
				app.setId(id);
			}
			if (StringUtils.isBlank(repeal_reason)) {
				return new FailedResponse("请填写撤销原因！");
			} else {
				app.setRepeal_reason(repeal_reason);
			}

			if (StringUtils.isNotEmpty(sp_status)) {

				if ("1".equals(sp_status)) {
					app.setSp_status("4");
				} else {
					// 撤销以后将车辆和驾驶员归队可接受任务
					Control con = new Control();
					con.setApply_id(id);
					con = controlService.findBy(con);
					// 新建申请还未派遣任务
					if (con != null) {
						Long car_id = con.getCar_id();
						Long driver_id = con.getDriver_id();

						Driver d = new Driver();
						d.setId(driver_id);
						d.setTask_status("1");
						driverService.updateTaskStatus(d);

						Car c = new Car();
						c.setId(car_id);
						c.setTask_status("1");
						carService.modify(c);

						// 修改状态为8
						con.setStatus("8");
						controlService.modify(con);
					} else {
						app.setDd_status("4");
					}
				}
				applyService.modify(app);
				return new SuccessResponse();
			} else {
				return new FailedResponse();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse();
		}

	}

	/**
	 * 获取调度列表 二期版本
	 * 
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @param use_name
	 * @param apply_status
	 * @param request
	 * @return
	 */
	@LogAnnotation(description = "调度分页查询")
	@ApiOperation(value = "调度分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequestMapping(value = "web/get/apply/dd/page", method = RequestMethod.POST)
	@ResponseBody
	public Page getDdPage(int limit, int pageIndex, String start_time, String end_time, String use_name,
			String apply_status, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Apply> list = new ArrayList<Apply>();
		int recodes = 0;
		map.put("startIdx", limit * pageIndex);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNoneBlank(start_time)) {
			map.put("start_time", start_time + " 00:00:00");

		}

		if (StringUtils.isNoneBlank(end_time)) {
			map.put("end_time", end_time + " 23:59:59");
		}

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"-1".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "1");
				}
				if ("2".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "2");
				}
				if ("3".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "3");
				}
				if ("4".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "4");
				}
				list = applyService.getDdPageService(map);
				recodes = applyService.getDdNum(map);
			} else {
				list = applyService.getDdPageService(map);
				recodes = applyService.getDdNum(map);
			}
		} else {
			return null;
		}
		return new Page<Apply>(list, recodes, limit);
	}

	/**
	 * 用车调度数据导出
	 * 
	 * @param start_time
	 * @param end_time
	 * @param use_name
	 * @param apply_status
	 * @param request
	 * @param response
	 * @return
	 */
	@LogAnnotation(description = "（二期版本）用车调度数据导出")
	@ApiOperation(value = "（二期版本）用车调度数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/dd/export")
	@RequestMapping(value = "web/apply/dd/export", method = RequestMethod.GET)
	@ResponseBody
	public Response ExportDd(String start_time, String end_time, String use_name, String apply_status,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		List<Apply> list = new ArrayList<Apply>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "调度数据文件" + sdf.format(System.currentTimeMillis()) + ".xls";
		// sheet名
		String sheetName = "调度数据明细";
		// 列名:用车人，用车人数，申请时间，开始时间，目的地，用车事由，状态
		String[] title = new String[] { "用车人", "用车人数", "用车类型", "开始时间", "结束时间", "上车地点", "目的地", "用车事由", "状态" };

		map.clear();
		if (StringUtils.isNoneBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"0".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "1");
				}
				if ("2".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "2");
				}
				if ("3".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "3");
				}
				if ("4".equals(apply_status)) {
					map.put("sp_status", "2");
					map.put("dd_status", "4");
				}
				list = applyService.exportDdService(map);
			} else {
				list = applyService.exportDdService(map);
			}
		} else {
			list = applyService.exportDdService(map);
		}

		// 内容list
		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			Apply app = list.get(i);
			values[i][0] = app.getUse_name();
			Integer use_number = app.getUse_number();
			values[i][1] = String.valueOf(use_number);
			String car = app.getCar();
			if ("1".equals(car)) {
				values[i][2] = "轿车";
			}
			if ("2".equals(car)) {
				values[i][2] = "商务车-9座以下";
			}
			if ("3".equals(car)) {
				values[i][2] = "专项作业车";
			}
			if ("4".equals(car)) {
				values[i][2] = "货车";
			}
			if ("5".equals(car)) {
				values[i][2] = "大巴车";
			}
			values[i][3] = app.getPlan_time();
			values[i][4] = app.getPlan_return();
			values[i][5] = app.getStart_place();
			values[i][6] = app.getEnd_place();
			values[i][7] = app.getReason();

			String app_status = app.getApply_status();
			if ("1".equals(app_status)) {
				values[i][8] = "待办";
			}
			if ("2".equals(app_status)) {
				values[i][8] = "同意";
			}
			if ("1.5".equals(app_status)) {
				values[i][8] = "驾驶员反馈";
			}
			if ("3".equals(app_status)) {
				values[i][8] = "驳回";
			}
			if ("4".equals(app_status)) {
				values[i][8] = "已撤销";
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values, null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SuccessResponse();
	}

	@LogAnnotation(description = "派遣分页查询")
	@ApiOperation(value = "派遣分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/get/apply/pq/page")
	@RequestMapping(value = "web/get/apply/pq/page", method = RequestMethod.POST)
	@ResponseBody
	public Page getPqPage(int limit, int pageIndex, String start_time, String end_time, String use_name,
			String apply_status, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Apply> list = new ArrayList<Apply>();
		int recodes = 0;
		map.put("startIdx", limit * pageIndex);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"0".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("yca_status", "1");
				}
				if ("2".equals(apply_status)) {
					map.put("yca_status", "9");
				}
				if ("3".equals(apply_status)) {
					List<Object> statusList = new ArrayList<Object>();
					statusList.add(2);
					statusList.add(3);
					statusList.add(5);
					map.put("statusList", statusList);
				}
				if ("4".equals(apply_status)) {
					map.put("yca_status", "8");
				}
				if ("5".equals(apply_status)) {
					map.put("yca_status", "6");
				}
				if ("6".equals(apply_status)) {
					map.put("yca_status", "7");
				}
				list = applyService.getPqPageService(map);
				recodes = applyService.getPqCountService(map);
			} else {
				list = applyService.getPqPageService(map);
				recodes = applyService.getPqCountService(map);
			}
		} else {
			list = applyService.getPqPageService(map);
			recodes = applyService.getPqCountService(map);
		}
		return new Page<Apply>(list, recodes, limit);
	}

	@LogAnnotation(description = "（二期版本）用车调度数据导出")
	@ApiOperation(value = "（二期版本）用车调度数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/pq/export")
	@RequestMapping(value = "web/apply/pq/export", method = RequestMethod.GET)
	@ResponseBody
	public Response ExportPq(String start_time, String end_time, String use_name, String apply_status,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		List<Apply> list = new ArrayList<Apply>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "派遣数据文件" + sdf.format(System.currentTimeMillis()) + ".xls";
		// sheet名
		String sheetName = "派遣数据明细";
		// 列名:申请人，用车人，用车人电话，用车事由，用车人数，车型，用车开始时间，用车结束时间，上车地点，目的地，备注，驾驶员姓名，驾驶员电话，车牌号
		String[] title = new String[] { "申请人", "用车人", "用车人电话", "用车事由", "用车人数", "车型", "用车开始时间", "用车结束时间", "上车地点", "目的地",
				"备注", "驾驶员姓名", "驾驶员电话", "车牌号" };

		map.clear();
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNoneBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNotBlank(apply_status)) {
			if (!"0".equals(apply_status)) {
				if ("1".equals(apply_status)) {
					map.put("yca_status", "1");
				}
				if ("2".equals(apply_status)) {
					map.put("yca_status", "9");
				}
				if ("3".equals(apply_status)) {
					List<Object> statusList = new ArrayList<Object>();
					statusList.add(2);
					statusList.add(3);
					statusList.add(5);
					map.put("statusList", statusList);
				}
				if ("4".equals(apply_status)) {
					map.put("yca_status", "8");
				}
				if ("5".equals(apply_status)) {
					map.put("yca_status", "6");
				}
				if ("6".equals(apply_status)) {
					map.put("yca_status", "7");
				}
			}
		}
		list = applyService.exportpqService(map);

		// 内容list
		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			Apply app = list.get(i);
			// 列名:申请人，用车人，用车人电话，用车事由，用车人数，车型，用车开始时间，用车结束时间，上车地点，目的地，备注，驾驶员姓名，驾驶员电话，车牌号
			values[i][0] = app.getSq_name();
			values[i][1] = app.getUse_name();
			values[i][2] = app.getReason();
			values[i][3] = app.getUse_mobile();
			Integer k = app.getUse_number();
			if (k != null) {
				values[i][4] = String.valueOf(k);
			} else {
				values[i][4] = "0";
			}
			values[i][4] = String.valueOf(k);
			String car = app.getCar();
			if ("1".equals(car)) {
				values[i][5] = "轿车";
			}
			if ("2".equals(car)) {
				values[i][5] = "商务车-9座以下";
			}
			if ("3".equals(car)) {
				values[i][5] = "专项作业车";
			}
			if ("4".equals(car)) {
				values[i][5] = "货车";
			}
			if ("5".equals(car)) {
				values[i][5] = "大巴车";
			}
			values[i][6] = app.getPlan_time();
			values[i][7] = app.getPlan_return();
			values[i][8] = app.getStart_place();
			values[i][9] = app.getEnd_place();
			values[i][10] = app.getReason();
			values[i][11] = app.getDriver_name();
			values[i][12] = app.getDriver_mobile();
			values[i][13] = app.getCar_no();
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values, null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SuccessResponse();
	}

	@LogAnnotation(description = "申请单编辑")
	@ApiOperation(value = "申请单编辑", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/apply/modify")
	@RequestMapping(value = "web/apply/modify", method = RequestMethod.POST)
	@ResponseBody
	public Response updateApply(Apply apply, HttpServletRequest request) {
		if (apply == null) {
			return new FailedResponse("参数错误！");
		}
		try {
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_1);
			apply.setDd_status(Constant.APPLY_1);
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());
			apply.setCreatedate(DateUtils.getDateTime());
			// 1--是 2--否 是否紧急调度
			apply.setType("2");
			applyService.modify(apply);

			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}

	}

	@LogAnnotation(description = "紧急调度")
	@ApiOperation(value = "紧急调度", httpMethod = "POST", response = Result.class, notes = "必须的参数：apply:{申请用车信息对象}")
	@RequiresPermissions(value = "web/set/emergency/apply")
	@RequestMapping(value = "web/set/emergency/apply", method = RequestMethod.POST)
	@ResponseBody
	public Response setEmergency(Apply apply, HttpServletRequest request) {
		if (apply == null) {
			return new FailedResponse("参数错误！");
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			apply.setPlan_time(apply.getPlan_time());
			apply.setPlan_return(apply.getPlan_return());
			apply.setStatus(Constant.ADD_STATUS);
			apply.setSp_status(Constant.APPLY_2);
			apply.setDd_status(Constant.APPLY_1);
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			apply.setSq_id(user.getId());
			apply.setSq_org(user.getOrg());
			apply.setCreatedate(DateUtils.getDateTime());
			// 1--是 2--否 是否紧急调度
			apply.setType("1");
			applyService.add(apply);

			// 查询刚添加的用车紧急调度申请
			List<Apply> list = applyService.getNewApplyService();
			for (Apply app : list) {
				long apply_id = app.getId();
				long org = app.getSq_org();

				queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

				// 获取申请人机构或者直属部门下拥有审批权限的人员
				List<Member> idsMem = memberDao.getMemberIds(map);
				Read read = new Read();
				read.setApply_id(apply_id);
				// 1表示未读，2表示已读
				read.setIsread("1");
				for (Member mm : idsMem) {
					long mid = mm.getId();
					read.setMid(mid);
					readService.add(read);
				}

			}

			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}

	}

	@LogAnnotation(description = "调度数据分页查询")
	@ApiOperation(value = "调度数据分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,use_name:用车人姓名,apply_status:处理状态,startDate:申请开始时间,endDate:申请结束时间")
	@RequiresPermissions(value = "web/apply/dd/data/page")
	@RequestMapping(value = "web/apply/dd/data/page", method = RequestMethod.POST)
	@ResponseBody
	public Page getDdData(Integer limit, Integer pageIndex, String start_time, String end_time, String use_name,
			String use_org, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Apply> list = new ArrayList<Apply>();
		int recodes = 0;

		if (limit != null && pageIndex != null) {
			map.put("startIdx", limit * pageIndex);
			map.put("limit", limit);
		}

		map.put("status", Constant.ADD_STATUS);

		if (StringUtils.isNotBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNotBlank(use_org)) {
			map.put("use_org", use_org);
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		list = applyService.getDdDataService(map);
		recodes = applyService.getDdDataCountService(map);
		return new Page<Apply>(list, recodes, limit);
	}

	@LogAnnotation(description = "（二期版本）用车调度数据导出")
	@ApiOperation(value = "（二期版本）用车调度数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： limit: 15,pageIndex: 0,use_name:用车人姓名apply_status:调度状态,hide_org:机构id")
	@RequiresPermissions(value = "web/apply/dd/data/export")
	@RequestMapping(value = "web/apply/dd/data/export", method = RequestMethod.GET)
	@ResponseBody
	public Response ExportDdData(String start_time, String end_time, String use_name, String use_org,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		List<Apply> list = new ArrayList<Apply>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "调度数据文件" + sdf.format(System.currentTimeMillis()) + ".xls";
		// sheet名
		String sheetName = "调度数据明细";
		// 列名:用车人、用车人数、用车部门、预定开始时间、预定结束时间、上车地点、目的地、用车事由、用车数
		String[] title = new String[] { "用车人", "用车人数", "用车部门", "开始时间", "结束时间", "上车地点", "目的地", "用车事由", "用车数" };

		map.clear();
		if (StringUtils.isNoneBlank(use_name)) {
			map.put("use_name", use_name);
		}
		if (StringUtils.isNoneBlank(use_org)) {
			map.put("use_org", use_org);
		}
		if (StringUtils.isNoneBlank(start_time) && StringUtils.isNoneBlank(end_time)) {
			map.put("start_time", start_time + " 00:00:00");
			map.put("end_time", end_time + " 23:59:59");
		}

		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		queryUtilsWeb.queryChildOrgsByParentId(org, map, "list");

		// 内容list
		list = applyService.exportDdDataService(map);

		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			Apply app = list.get(i);
			// 列名:用车人、用车人数、用车部门、预定开始时间、预定结束时间、上车地点、目的地、用车事由、用车数
			values[i][0] = app.getUse_name();
			Integer use_number = app.getUse_number();
			if (use_number == null) {
				values[i][1] = "0";
			} else {
				values[i][1] = String.valueOf(use_number);
			}

			values[i][2] = app.getUser_org_name();
			values[i][3] = app.getPlan_time();
			values[i][4] = app.getPlan_return();
			values[i][5] = app.getStart_place();
			values[i][6] = app.getEnd_place();
			values[i][7] = app.getReason();

			Integer pc = app.getPlan_car_num();
			if (pc == null) {
				values[i][8] = "0";
			} else {
				values[i][8] = String.valueOf(pc);
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values, null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SuccessResponse();
	}

}
