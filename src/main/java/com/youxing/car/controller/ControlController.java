package com.youxing.car.controller;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Control;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Evaluate;
import com.youxing.car.entity.Task;
import com.youxing.car.entity.User;
import com.youxing.car.service.CarService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.EvaluateService;
import com.youxing.car.service.TaskService;
import com.youxing.car.service.UserService;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;

@Api(value = "control", description = "归还车辆")
@Controller
public class ControlController {
	@Resource
	private ControlService controlService;
	@Resource
	private UserService userService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private DriverService driverService;
	@Resource
	private CarService carService;


	@Resource
	private TaskService taskService;
	@Resource
	private EvaluateService evaluateService;
	@Resource
	private QueryUtils queryUtils;

	private static Logger LOGGER = LoggerFactory.getLogger(ControlController.class);

	/**
	 * 
	 * @author mars @date 2017年5月2日 下午1:32:54 @Description: TODO(驾驶员任务列表) @param @param
	 *         page @param @param size @param @param id @param @return 设定文件 @return
	 *         Result 返回类型 @throws
	 */
	@LogAnnotation(description="驾驶员任务列表" )
	@ApiOperation(value = "驾驶员任务列表  1表示待签收  2 表示已经签收但是未完成列表  3任务开始列表   5 结束列表  6 归队已完成列表", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/control/list", method = RequestMethod.POST)
	@ResponseBody
	public Result list(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每页大小") Integer size, @ApiParam(value = "用户id") Long uid, String status) {
		if (uid == null || StringUtils.isBlank(status) || page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			User user = userService.findById(uid);
			if (user == null || StringUtils.isBlank(user.getMid())) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Map<String, Object> driveMap = new HashMap<>();
			driveMap.put("uid", user.getMid());
			driveMap.put("status", Constant.ADD_STATUS);
			Driver driver = driverService.findByMap(driveMap);
			List<Control> apply = new ArrayList<Control>();
			if (driver == null) {
				return Result.success(apply);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("startIdx", page * size);
			map.put("limit", size);
			map.put("driver_id", driver.getId());
			map.put("status", status);
			if (status.equals("6")) {
				apply = controlService.pageBy(map);
			} else if (status.equals("2")) {
				apply = controlService.pageByundo(map);
			} else {
				apply = controlService.pageBy(map);
			}
			return Result.success(apply);
		} catch (Exception e) {
			LOGGER.error("app/control/signup", e);
			e.printStackTrace();
			return Result.error();
		}
	}

	/**
	 * @author llh @date 2017年5月10日 下午1:27:14 @Description: TODO(签收) @param @param
	 *         id @param @param uid @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="签收操作" )
	@ApiOperation(value = "签收操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/control/signup", method = RequestMethod.POST)
	@ResponseBody
	public Result signUp(@ApiParam(value = "本条数据id") Long id, @ApiParam(value = "用户id") Long uid) {
		try {
			if (id == null || uid == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Control ct = controlService.findById(id);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.QS.equals(ct.getStatus())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null || StringUtils.isBlank(user.getMid())) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			// 根据uid寻找driverid
			Map<String, Object> driveMap = new HashMap<>();
			driveMap.put("uid", user.getMid());
			driveMap.put("status", Constant.ADD_STATUS);
			Driver driver = driverService.findByMap(driveMap);
			if (driver == null) {
				return Result.instance(ResponseCode.unknown_driver.getCode(), ResponseCode.unknown_driver.getMsg());
			}
			// 司机不匹配
			if (ct.getDriver_id() != driver.getId()) {
				return Result.instance(ResponseCode.user_already_exist.getCode(), ResponseCode.user_already_exist.getMsg());
			}
			// 是否在执行其他任务
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("driver_id", ct.getDriver_id());
			// System.out.println("------getDriver_id"+ct.getDriver_id());
			map.put("list", new String[] { "3", "5" });
			int count = controlService.countBy(map);
			// System.out.println("------getDriver_id"+count);
			if (count > 0) {
				return Result.instance(ResponseCode.un_end.getCode(), ResponseCode.un_end.getMsg());
			}
			map.clear();
			Long carId = ct.getCar_id();
			map.put("car_id", carId);
			map.put("status", Constant.ADD_STATUS);
			Device device = deviceService.findByMap(map);
			// 未绑定设备
			if (device == null || StringUtils.isBlank(device.getDevice())) {
				return Result.instance(ResponseCode.no_device.getCode(), ResponseCode.no_device.getMsg());
			}

			Control control = new Control();
			Task task = new Task();
			task.setConid(id);
			task.setCreatedby(uid);
			task.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			task.setDriver(driver.getId());
			control.setId(id);
			control.setStatus(Constant.QS);
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("conid", id);
			Task taskResult = taskService.findByMap(queryMap);
			if (taskResult != null) {
				return Result.instance(ResponseCode.task_already_exit.getCode(), ResponseCode.task_already_exit.getMsg());

			}
			taskService.add(task);
			controlService.modify(control);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/control/signup", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月2日 下午1:47:06 @Description: TODO(开始执行任务) @param @param
	 *         id @param @param uid @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="开始执行任务操作" )
	@ApiOperation(value = "开始执行任务操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/control/start", method = RequestMethod.POST)
	@ResponseBody
	public Result startPlan(@ApiParam(value = "本条数据id") Long id, @ApiParam(value = "用户id") Long uid, String mileage) {
		try {
			if (id == null || uid == null || StringUtils.isBlank(mileage)) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Control ct = controlService.findById(id);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.KS.equals(ct.getStatus())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null || StringUtils.isBlank(user.getMid())) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			// 根据uid寻找driverid
			Map<String, Object> driveMap = new HashMap<>();
			driveMap.put("uid", user.getMid());
			driveMap.put("status", Constant.ADD_STATUS);
			Driver driver = driverService.findByMap(driveMap);
			if (driver == null) {
				return Result.instance(ResponseCode.unknown_driver.getCode(), ResponseCode.unknown_driver.getMsg());
			}
			// 司机不匹配
			if (ct.getDriver_id() != driver.getId()) {
				return Result.instance(ResponseCode.user_already_exist.getCode(), ResponseCode.user_already_exist.getMsg());
			}
			// 是否在执行其他任务
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("driver_id", ct.getDriver_id());
			map.put("list", new String[] { "3", "5" });
			int count = controlService.countBy(map);
			if (count > 0) {
				return Result.instance(ResponseCode.un_end.getCode(), ResponseCode.un_end.getMsg());
			}
			map.clear();
			Long carId = ct.getCar_id();
			map.put("car_id", carId);
			map.put("status", Constant.ADD_STATUS);
			Device device = deviceService.findByMap(map);
			// 未绑定设备
			if (device == null || StringUtils.isBlank(device.getDevice())) {
				return Result.instance(ResponseCode.no_device.getCode(), ResponseCode.no_device.getMsg());
			}
			Control control = new Control();
			Task task = new Task();
			task.setConid(id);
			task.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			task.setStart(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			task.setStart_mileage(Long.parseLong(mileage));
			task.setResult("1");
			control.setId(id);
			control.setStatus(Constant.KS);
			controlService.modify(control, task, null);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/control/start", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月3日 上午11:39:35 @Description: TODO(结束任务) @param @param
	 *         id @param @param uid @param @param end @param @param mileage @param @param
	 *         obd @param @param cost @param @param eva @param @return 设定文件 @return
	 *         Result 返回类型 @throws
	 */
	@LogAnnotation(description="结束任务操作" )
	@ApiOperation(value = "结束任务操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/control/end", method = RequestMethod.POST)
	@ResponseBody
	public Result endPlan(@ApiParam(value = "本条数据id 是Control的id") Long id, @ApiParam(value = "用户id") Long uid, @ApiParam(value = "执行结果  1-完成/2-未完成") String result,
			@ApiParam(value = "终止里程") String end, @ApiParam(value = "修改费用") String cost, @ApiParam(value = "评价") String eva, @ApiParam(value = "gps测量里程") String obd) {
		try {
			if (id == null || uid == null || StringUtils.isBlank(result) || StringUtils.isBlank(end) || StringUtils.isBlank(cost) || StringUtils.isBlank(eva) || StringUtils.isBlank(obd)) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Control ct = controlService.findById(id);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.JS.equals(ct.getStatus())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null || StringUtils.isBlank(user.getMid())) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			// 根据uid寻找driverid
			Map<String, Object> driveMap = new HashMap<>();
			driveMap.put("uid", user.getMid());
			driveMap.put("status", Constant.ADD_STATUS);
			Driver driver = driverService.findByMap(driveMap);
			if (driver == null) {
				return Result.instance(ResponseCode.unknown_driver.getCode(), ResponseCode.unknown_driver.getMsg());
			}

			if (ct.getDriver_id() != driver.getId()) {
				return Result.instance(ResponseCode.user_already_exist.getCode(), ResponseCode.user_already_exist.getMsg());
			}
			// 结束任务
			ct.setStatus(Constant.JS);
			Task task = new Task();
			task.setConid(id);
			task.setEnd_mileage(Long.parseLong(end));
			task.setEnd(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			task.setObd_mileage(Long.parseLong(obd));

			task.setCost(Double.parseDouble(cost));
			task.setResult(result);
			task.setEva(eva);
			// 添加评论
			Evaluate evaluate = new Evaluate();
			evaluate.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			evaluate.setCreatedby(uid);
			evaluate.setEva(eva);
			evaluate.setTask_id(id);
			controlService.modify(ct, task, evaluate);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/control/end", e);
			return Result.error();
		}
	}
	@LogAnnotation(description="归队操作" )
	@ApiOperation(value = "归队操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/control/back", method = RequestMethod.POST)
	@ResponseBody
	public Result back(@ApiParam(value = "本条数据id 是Control的id") Long id, @ApiParam(value = "用户id") Long uid,String car_id) {
		try {
 			if (id == null || uid == null || car_id == null || "".equals(car_id)) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Control ct = controlService.findById(id);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.GD.equals(ct.getStatus())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null || StringUtils.isBlank(user.getMid())) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			// 根据uid寻找driverid
			Map<String, Object> driveMap = new HashMap<>();
			driveMap.put("uid", user.getMid());
			driveMap.put("status", Constant.ADD_STATUS);
			Driver driver = driverService.findByMap(driveMap);
			if (driver == null) {
				return Result.instance(ResponseCode.unknown_driver.getCode(), ResponseCode.unknown_driver.getMsg());
			}

			if (ct.getDriver_id() != driver.getId()) {
				return Result.instance(ResponseCode.user_already_exist.getCode(), ResponseCode.user_already_exist.getMsg());
			}
			// 归队
			ct.setStatus(Constant.GD);

			controlService.modify(ct, driver);
			String did=user.getMid();
			Driver d=new Driver();
			d.setUid(Long.parseLong(did));
			d.setTask_status("1");
			driverService.modify(d);
			
			Car c=new Car();
			c.setId(Long.parseLong(car_id));
			c.setTask_status("1");
			carService.modify(c);
			
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/control/back", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月13日 下午2:18:43
	 * @Description: TODO(查看用车申请的详细调度列表)
	 * @param @param id
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="调度列表" )
	@ApiOperation(value = "调度列表", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = Control.class) })
	@RequestMapping(value = "app/control/apply/list", method = RequestMethod.POST)
	@ResponseBody
	public Result getByApply(@ApiParam(value = "本条数据id 是指用车申请的id") String id) {
		List<Control> list = new ArrayList<Control>();
		try {
			if (StringUtils.isBlank(id)) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("apply", Long.parseLong(id));
			map.put("status", 1);
			list = controlService.listApply(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/control/apply/list", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月13日 下午2:18:14
	 * @Description: TODO(派遣列表)该接口暂时没有用到
	 * @param @param uid
	 * @param @param page
	 * @param @param size
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="派遣列表" )
	@ApiOperation(value = "派遣列表", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = Control.class) })
	@RequestMapping(value = "app/control/list/all", method = RequestMethod.POST)
	@ResponseBody
	public Result getByApply(@ApiParam(required = true, value = "用户id") Long uid, @ApiParam(required = true, value = "哪一页") Integer page, @ApiParam(required = true, value = "每页大小") Integer size) {
		List<Control> list;
		if (uid == null || page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			queryUtils.queryByOrg(String.valueOf(uid), map);
			map.put("startIdx", page * size);
			map.put("limit", size);
			list = controlService.pageAllControl(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/control/list/all", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月13日 下午2:17:59
	 * @Description: TODO(某个调度作废)
	 * @param @param uid
	 * @param @param cid
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="调度作废" )
	@ApiOperation(value = "调度作废", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = Result.class) })
	@RequestMapping(value = "app/control/cancel", method = RequestMethod.GET)
	@ResponseBody
	public Result CancelControl(@ApiParam(required = true, value = "用户id") Long uid, @ApiParam(required = true, value = "作废数据id") Long cid,String car_id,String driver_id) {
		if (uid == null || cid == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			Control ct = controlService.findById(cid);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			if (Constant.ZF.equals(ct.getStatus())) {
				return Result.instance(ResponseCode.repeat_operate.getCode(), ResponseCode.repeat_operate.getMsg());
			}
			Control ne = new Control();
			ne.setStatus(Constant.ZF);
			ne.setId(cid);
			controlService.modify(ne);
			
			Driver d=new Driver();
			d.setId(Long.parseLong(driver_id));
			d.setTask_status("1");
			driverService.updateTaskStatusService(d);
			
			Car c=new Car();
			c.setId(Long.parseLong(car_id));
			c.setTask_status("1");
			carService.modify(c);
			
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/control/cancel", e);
			return Result.error();
		}
	}

	/**
	 * @author llh
	 * @date 2017年5月16日 下午4:57:56
	 * @Description: TODO(已派遣列表 根据申请id)
	 * @param @param uid
	 * @param @param aid
	 * @param @param page
	 * @param @param size
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="已派遣列表  根据申请id" )
	@ApiOperation(value = "已派遣列表  根据申请id", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = Control.class) })
	@RequestMapping(value = "app/control/list/byaid", method = RequestMethod.POST)
	@ResponseBody
	public Result getByApplyid(@ApiParam(required = true, value = "用户id") Long uid, @ApiParam(required = true, value = "申请id") Long aid, @ApiParam(required = true, value = "哪一页") Integer page,
			@ApiParam(required = true, value = "每页大小") Integer size) {
		List<Control> list = new ArrayList<Control>();
		if (uid == null || page == null || size == null || aid == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			queryUtils.queryByOrg(String.valueOf(uid), map);
			map.put("startIdx", page);
			map.put("limit", size);
			map.put("apply_id", aid);
			list = controlService.pageBy(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/control/list/byaid", e);
			return Result.error();
		}
	}
	
	
	
	/**
	 * ================web
	 */
	
	/**
	* @author mars   
	* @date 2017年4月5日 下午7:36:53 
	* @Description: TODO(查看用车申请调度信息) 
	* @param @param id
	* @param @return    设定文件 
	* @return List<Control>    返回类型 
	* @throws
	 */
	@LogAnnotation(description="派遣列表" )
	@ApiOperation(value = "派遣列表 ", httpMethod = "GET", response = Result.class)
	@RequestMapping(value="web/control/list", method = RequestMethod.GET)
	@ResponseBody
	public List<Control> getByApplyWeb(String id) {
		List<Control> list = new ArrayList<Control>();
		if (StringUtils.isBlank(id)) {
			return list;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("apply", Long.parseLong(id));
		list = controlService.listByWebService(map);
		return list;
	}
	
	/**
	 * 二期版本签收操作
	 * @param id
	 * @return
	 */
	@LogAnnotation(description="二期版本签收操作" )
	@ApiOperation(value = "二期版本签收操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value="web/apply/pq/sign", method = RequestMethod.POST)
	@ResponseBody
	public Response pqSign(String id,HttpServletRequest request) {
		Control con = new Control();
		if (StringUtils.isNotBlank(id)) {
			 con = controlService.findById(Long.parseLong(id));
		}
		
		String status = con.getStatus();
		if("8".equals(status)){
			return new FailedResponse("此单子已被撤销，请刷新页面！");
		}else{
			con.setId(Long.parseLong(id));
			con.setStatus("2");
			controlService.modify(con);
			
			String uid = request.getParameter("keyid");
			Task task = new Task();
			task.setConid(Long.parseLong(id));
			task.setCreatedby(Long.parseLong(uid));
			task.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			task.setDriver(con.getDriver_id());
		
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("conid", id);
			Task taskResult = taskService.findByMap(queryMap);
			if (taskResult == null) {
				taskService.add(task);
				Control control = new Control();
				control.setId(Long.parseLong(id));
				control.setStatus(Constant.QS);
				controlService.modify(control);
			}else{
				return new FailedResponse("请不要重复签收！");
			}
		}
		return new SuccessResponse();
	}
	
	@LogAnnotation(description="二期版本签收操作" )
	@ApiOperation(value = "二期版本签收操作", httpMethod = "POST", response = Result.class)
	@RequestMapping(value="web/apply/pq/feedback", method = RequestMethod.POST)
	@ResponseBody
	public Response pqFeedback(String id,String feedback) {
		Control con = new Control();
		
		if(StringUtils.isEmpty(feedback)){
			return new FailedResponse("请填写反馈信息！");
		}
		if (StringUtils.isNotBlank(id)) {
			 con = controlService.findById(Long.parseLong(id));
		}
		
		String status = con.getStatus();
		if("8".equals(status)){
			return new FailedResponse("此单子已被撤销，请刷新页面！");
		}if("1".equals(status)){
			con.setId(Long.parseLong(id));
			con.setStatus("9");
			con.setFeedback(feedback);
			controlService.modify(con);
		}
		return new SuccessResponse();
	}
}
