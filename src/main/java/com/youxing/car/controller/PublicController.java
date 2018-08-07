package com.youxing.car.controller;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Apply;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.CarType;
import com.youxing.car.entity.Control;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Gps;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.OrgTree;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.Task;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ApplyService;
import com.youxing.car.service.BehaviorService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.TaskService;
import com.youxing.car.service.UserService;
import com.youxing.car.utils.other.QueryUtilsWeb;

/**
 * @author mars
 * @date 2017年5月2日 上午11:23:41
 */
@Api(value = "public", description = "公用")
@Controller
public class PublicController {
	@Resource
	private OrganizationService organizationService;
	@Resource
	private MemberService memberService;
	@Resource
	private UserService userService;
	@Resource
	private QueryUtils queryUtils;
	@Resource
	private ControlService controlService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private TaskService taskService;
	@Resource
	private ApplyService applyService;
	@Resource
	private CarService carService;
	@Resource
	private DriverService driverService;
	@Resource
	private GpsService gpsService;
	@Resource
	private QueryUtilsWeb queryUtilsWeb;
	@Resource
	private OrganizationService orgService;
	
	@Resource
	private BehaviorService behaviorService;

	private static Logger LOGGER = LoggerFactory.getLogger(PublicController.class);

	/**
	 * 
	 * @author mars @date 2017年5月2日 上午11:51:47 @Description: TODO(获取用户机构树) @param @param
	 *         id @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="获取用户机构树包括车" )
	@ApiOperation(value = "获取用户机构树包括车", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "app/get/car/tree", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = OrgTree.class) })
	@ResponseBody
	public Result getOrgCarByUid(@ApiParam(required = true, name = "id", value = "用户id") String id, @ApiParam(required = true, name = "treeId", value = "树形机构或者部门的id") String treeId) {
		if (StringUtils.isBlank(id)) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		List<Organization> nodes = new ArrayList<Organization>();
		List<OrgTree> tree = new ArrayList<OrgTree>();
		List<Car> lists = new ArrayList<Car>();
		Map<Long, Organization> perMap = new HashMap<Long, Organization>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("status", Constant.ADD_STATUS);
		map.put("car_status", Constant.CHECK_STATUS);
		map.put("obd", Constant.BIND_OBD);
		try {
			User user = userService.findById(Long.valueOf(id));
			if (user == null ) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Long orgs = user.getOrg();
			if (orgs == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("status", Constant.ADD_STATUS);
			if (StringUtils.isBlank(treeId)) {
				Organization user2Org = organizationService.findById(orgs);
				if (user2Org.getParent() != null) {
					List<Long> org_list = organizationService.getOrganizationUser(user2Org, orgs);
					if (!CollectionUtils.isEmpty(org_list)) {
						org_list.add(orgs);
						maps.put("list", org_list);
					} else {
						maps.put("id", orgs);
					}
				} else {
					maps.put("root", "1");
				}
			} else {
				maps.put("parent", treeId);
			}
			List<Organization> list = organizationService.listBy(maps);
			for (Organization org : list) {
				Long orgId = org.getId();
				perMap.put(orgId, org);
				nodes.add(org);
			}
			if (StringUtils.isNotBlank(treeId)) {
				map.put("org", Long.valueOf(treeId));
				map.put("status", Constant.ADD_STATUS);
				map.put("car_status", Constant.CHECK_STATUS);
				map.put("obd", Constant.BIND_OBD);
				lists = carService.listBy(map);
			}
			TreeUtils.setLazyTree(nodes, tree, lists, treeId);
			return Result.success(tree);
		} catch (Exception e) {
			LOGGER.error("app/get/car/tree", e);
			return Result.error();
		}
	}
	@LogAnnotation(description="获取用户机构树" )
	@ApiOperation(value = "获取用户机构树", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "app/get/org/tree", method = RequestMethod.GET)
	@ResponseBody
	public Result getOrgByUid(@ApiParam(required = true, name = "id", value = "用户id") String id) {
		if (StringUtils.isBlank(id)) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		List<Organization> list = new ArrayList<Organization>();
		List<Organization> nodes = new ArrayList<Organization>();
		try {
			User user = userService.findById(Long.valueOf(id));
			if (user == null ) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Member m = new Member();
			m.setId(Long.parseLong(user.getMid()));
			Member mem = memberService.findBy(m);
			Long orgs = mem.getOrg();
			if (orgs == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("status", Constant.ADD_STATUS);
			Organization user2Org = organizationService.findById(orgs);
			if (user2Org.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(user2Org, orgs);
				if (!CollectionUtils.isEmpty(org_list)) {
					maps.put("list", org_list);
				} else {
					maps.put("id", orgs);
				}
			}
			list = organizationService.listBy(maps);
			Map<Long, Organization> perMap = new HashMap<Long, Organization>();
			for (Organization org : list) {
				if (org.getParent() == null) {
					nodes.add(org);
				}
				perMap.put(org.getId(), org);
			}
			for (Organization org : list) {
				Long parentId = org.getParent();
				if (parentId != null && perMap.containsKey(parentId)) {
					perMap.get(parentId).addChildren(org);
				}
			}
			if (nodes.size() == 0) {
				nodes.addAll(list);
			}
			return Result.success(nodes);
		} catch (Exception e) {
			LOGGER.error("app/get/org/tree", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月3日 下午3:19:19 @Description:
	 *         TODO(获取汽车的起始里程和终点OBD里程) @param @param id @param @param type @param @return
	 *         设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="获取车辆的里程" )
	@ApiOperation(value = "获取车辆的里程", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "app/get/car/mileage", method = RequestMethod.GET)
	@ResponseBody
	public Result getCarMileage(@ApiParam(value = "s本条数据id") Long id, @ApiParam(value = "数据类型1-起始里程2-结束的时候obd测量里程") String type) {
		if (id == null || StringUtils.isBlank(type)) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
			String mileage = "0";
			Control ct = controlService.findById(id);
			if (ct == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Long carId = ct.getCar_id();
			map.put("car_id", carId);
			map.put("status", Constant.ADD_STATUS);
			Device device = deviceService.findByMap(map);
			String obd = device.getDevice();
			if ("1".equals(type)) {
				//map.clear();
				map.put("device", obd);
				map.put("startIdx", 0);
				map.put("limit", 1);
				List<Gps> ci = gpsService.pageBy(map);
				if (!CollectionUtils.isEmpty(ci)) {
					Long mil = ci.get(0).getMileage();
					if (mil != null && mil > 0) {
						mileage = String.valueOf(mil / 1000);
					}
				}
				return Result.success(mileage);
			} else if ("2".equals(type)) {
				Task re = new Task();
				map.clear();
				map.put("conid", id);
				Task task = taskService.findByMap(map);
				Long sl = task.getStart_mileage();
				map.clear();
				map.put("device", obd);
				map.put("startIdx", 0);
				map.put("limit", 1);
				map.put("senddate_lte", DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
				List<Gps> end = gpsService.pageBy(map);
				if (CollectionUtils.isNotEmpty(end)) {
					Long el = end.get(0).getMileage();
					if (el != null && sl != null) {
						if (el > 0) {
							el = el / 1000;
						}
						re.setObd_mileage(el - sl);
					}
				}
				if (sl != null) {
					re.setStart_mileage(sl);
				}
				return Result.success(re);
			}
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("aapp/get/car/mileage", e);
			return Result.error();
		}

	}
	@LogAnnotation(description="根据部门获取用户" )
	@ApiOperation(value = "根据部门获取用户", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/get/org/user", method = RequestMethod.POST)
	@ResponseBody
	public Result getUserbyorg(@ApiParam(value = "部门id") Long id, @ApiParam(value = "用户id") String uid) {
		if (uid == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		Map<String, Object> map = new HashMap<>();
		map.put("org", id);
		map.put("startIdx", 0);
		map.put("limit", 100);
		List<User> users = userService.pageBy(map);
		return Result.success(users);

	}
	@LogAnnotation(description="监控所有车辆" )
	@ApiOperation(value = "监控所有车辆", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "app/get/all/car", method = RequestMethod.GET)
	@ResponseBody
	public Result getUserCar(String type,Long uid) {
		try {
			List<Gps> gps = new ArrayList<Gps>();
			
			if (uid == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null ) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			
			Organization user2Org = organizationService.findById(org);
			List<Long> org_list = organizationService.getOrganizationUser(user2Org, org);
			org_list.add(org);
			HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("org_list", org_list);
				
				if(StringUtils.isNotBlank(type)){
					map.put("type", type);
				}
				
				List<CarMessage> cn = gpsService.findGpsCurrentByDevice(map);
				if (!CollectionUtils.isEmpty(cn)) {
					for (CarMessage cam : cn) {
						Gps re = new Gps();
							double[] gPS = GPSToBaiduGPS.wgs2bd(cam.getLatitude(), cam.getLongitude());
							re.setLatitude(gPS[0]);
							re.setLongitude(gPS[1]);
							re.setDirection(cam.getDirection());
							re.setCar_no(cam.getCar_no());
							//手机端以id接收car_id
							re.setId(String.valueOf(cam.getCar_id()));
							re.setCar_type(cam.getCar_type());
							re.setType(cam.getType());
							int speed = cam.getCar_speed();
							String acc = cam.getAcc();
							//状态 1在线、2静止、0离线
							if ("0".equals(acc)) {
								re.setAcc(acc);
							} else {
								if (speed == 0) {
									re.setAcc("2");
								} else {
									re.setAcc("1");
								}
							}
							gps.add(re);
					}
				}
			
			return Result.success(gps);
		} catch (Exception e) {
			LOGGER.error("app/get/all/car", e);
			return Result.error();
		}
	}
	
	
	/**
	 * =================================web
	 */
	@ApiOperation(value = "获取登录用户机构名称", httpMethod = "POST", response = Result.class, notes = "登录必须的参数：username")
	@RequestMapping(value = "web/get/user/org", method = RequestMethod.POST)
	@ResponseBody
	public Response getOrgByName(String name,HttpServletResponse response){
		/*
		 * CorsUtil co=new CorsUtil(); co.SetHttpServletResponse(response);
		 */
		if (StringUtils.isBlank(name)) {
			return new FailedResponse();
		}
		User user = new User();
		user.setName(name);
		user.setStatus("1");
		User u = userService.findBy(user);
		try {
			if (u != null) {
				String mid = u.getMid();
				Member member = memberService.findById(Long.parseLong(mid));
				Long org = member.getOrg();
				if (org != null) {
					Organization organ = organizationService.findById(org);
					if (organ != null) {
						return new ObjectResponse<String>(organ.getName());
					}
				}
			}
		} catch (Exception e) {
			return new FailedResponse("服务器错误！");
		}
		return new FailedResponse("用户名不正确！");
	}
	
	/**
	 * 
	 * @author mars
	 * @date 2017年4月20日 下午1:22:39
	 * @Description: TODO(车辆品牌的json)
	 * @param @return 设定文件
	 * @return List<CarType> 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取车辆品牌" )
	@ApiOperation(value = "获取车辆品牌", httpMethod = "GET", response = Car.class)
	@RequestMapping(value="web/car/type", method = RequestMethod.GET)
	@ResponseBody
	public List<CarType> type() {
		List<CarType> list = new ArrayList<CarType>();
		com.alibaba.fastjson.JSONArray parseArray = JSON.parseArray(Constant.CAR_TYPE_JSON);
		for (int i = 0; i < parseArray.size(); i++) {
			com.alibaba.fastjson.JSONObject jb = parseArray.getJSONObject(i);
			String name = jb.getString("text");
			list.add(new CarType(name, name));
		}
		return list;
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月8日 下午3:19:46
	 * @Description: TODO(车牌号模糊查询)
	 * @param @param name
	 * @param @return 设定文件
	 * @return List<Car> 返回类型
	 * @throws
	 */
	@LogAnnotation(description="车牌号模糊查询" )
	@ApiOperation(value = "车牌号模糊查询", httpMethod = "GET", response = Car.class,notes ="name")
	@RequestMapping(value = "web/car/load", method = RequestMethod.GET)
	@ResponseBody
	public List<CarType> load(String name, HttpServletRequest request) {
		List<CarType> ct = new ArrayList<CarType>();
		List<Car> list = new ArrayList<Car>();
		if (StringUtils.isBlank(name)) {
			return ct;
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		
		User u1 =new User();
		String id = request.getParameter("keyid");
		u1.setId(Long.parseLong(id));
		User u = userService.findBy(u1);
		
		Long u_org = u.getOrg();
		
		maps.put("org", Long.toString(u_org));
		maps.put("car_no_like", name);
		maps.put("status", Constant.ADD_STATUS);
		maps.put("car_status", Constant.ADD_STATUS);
		maps.put("obd", Constant.BIND_OBD);
		list = carService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				CarType type = new CarType();
				type.setText(list.get(i).getCar_no());
				type.setValue(list.get(i).getId().toString());
				ct.add(type);
			}
		}
		return ct;
	}
	/**
	 * 
	 * @author zzb
	 * @date 2017年7月4日16:35:43
	 * @Description: TODO(车牌号模糊查询改造)
	 * @param @param name
	 * @param @return 设定文件
	 * @return List<Car> 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="车牌号模糊查询" )
	@ApiOperation(value = "车牌号模糊查询", httpMethod = "POST", response = Car.class, notes ="{name:查询的车牌号}")
	@RequestMapping(value = "web/car/loads",method = RequestMethod.POST)
	@ResponseBody
	public List<CarType> loads(String name, HttpServletRequest request) {
		List<CarType> ct = new ArrayList<CarType>();
		List<Car> list = new ArrayList<Car>();
		if (StringUtils.isBlank(name)) {
			return ct;
		}
		HashMap<String, Object> maps = new HashMap<String, Object>();
		queryUtilsWeb.queryByOrg(request, maps);
		if (maps.get("id") != null) {
			String org = maps.get("id").toString();
			maps.remove("id");
			maps.put("org", org);
		}
		
		maps.put("car_no_like", name);
		maps.put("status", Constant.ADD_STATUS);
		maps.put("car_status", Constant.ADD_STATUS);
		maps.put("obd", Constant.BIND_OBD);
		list = carService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				CarType type = new CarType();
				type.setText(list.get(i).getCar_no());
				type.setValue(list.get(i).getId().toString());
				ct.add(type);
			}
		}
		return ct;
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年4月20日 下午1:22:52
	 * @Description: TODO(检测设备是否重复使用)
	 * @param @param device
	 * @param @return 设定文件    
	 * @return String 返回类型    
	 * @throws
	 */
	@LogAnnotation(description="设备号唯一验证" )
	@ApiOperation(value = "设备号唯一验证", httpMethod = "GET", response = Car.class, notes ="{device:设备编号}")
	@RequestMapping(value = "web/device/no",method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String checkNames(String device) {
		if (StringUtils.isBlank(device)) {
			return "";
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("device", device);
		maps.put("status", Constant.ADD_STATUS);
		List<Device> list = deviceService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			return "设备已经被使用！";
		}
		return "";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年4月20日 下午1:43:21
	 * @Description: TODO(检查车牌号是否被占用)
	 * @param @param device
	 * @param @return 设定文件    
	 * @return String 返回类型
	 * @throws
	 */
	@LogAnnotation(description="车牌号唯一验证" )
	@ApiOperation(value = "车牌号唯一验证", httpMethod = "GET", response = Car.class, notes ="{car_no:车牌号}")
	@RequestMapping(value = "web/car/no", method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String checkCar(String car_no) {
		if (StringUtils.isBlank(car_no)) {
			return "";
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("car_no", car_no);
		maps.put("status", Constant.ADD_STATUS);
		Car c = carService.findByMap(maps);
		if (c != null) {
			return "车牌号已经被使用！";
		}
		return "";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月12日 下午1:29:12
	 * @Description: TODO(调度车辆过滤当前正在执行调度或者调度时间在该申请的计划用车时间内)
	 * @param @param limit
	 * @param @param pageIndex
	 * @param @param request
	 * @param @return 设定文件
	 * @return Page 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="获取调度车辆，过滤不符合条件的车辆" )
	@ApiOperation(value = "获取调度车辆，过滤不符合条件的车辆", httpMethod = "POST", response = Car.class, notes =" { limit: 15,pageIndex: 0,apply:用车申请记录id,car_no_r:车牌号,show_name_car:机构名称}")
	@RequestMapping(value="web/car/page/filter",method = RequestMethod.POST)
	@ResponseBody
	public Page car(int limit, int pageIndex, String apply, HttpServletRequest request) {
		List<Car> list = new ArrayList<Car>();
		int recodes = 0;
		if (StringUtils.isNotBlank(apply)) {
			Apply app = applyService.findById(Long.valueOf(apply));
			if (app != null) {
				String start = app.getPlan_time();
				String end = app.getPlan_return();
				if (start != null && end != null) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("car_id", "1");
					map.put("start", start);
					map.put("end", end);
					List<String> ids = controlService.listByControl(map);
					map.clear();
					if (CollectionUtils.isNotEmpty(ids)) {
						map.put("lists", ids);
					}
					String car_no = request.getParameter("car_no_r");
					if (StringUtils.isNotBlank(car_no)) {
						map.put("car_no", car_no);
					}

					String orgna = request.getParameter("show_name_car");
					if (StringUtils.isNotBlank(orgna)) {
						map.put("orgname", orgna);
					}
					map.put("startIdx", pageIndex*limit);
					map.put("limit", limit);
					map.put("status", Constant.ADD_STATUS);
					map.put("car_status", Constant.CHECK_STATUS);
					map.put("obd", Constant.BIND_OBD);
					map.put("task_status", Constant.ADD_STATUS);
					//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
					
					User u =new User();
					String id = request.getParameter("keyid");
					u.setId(Long.parseLong(id));
					User user = userService.findBy(u);
					
					Long orgId = user.getOrg();
					Organization userOrg = organizationService.findById(orgId);
					if (userOrg.getParent() != null) {
						List<Long> org_list = organizationService.getOrganizationUser(userOrg, orgId);
						if (!CollectionUtils.isEmpty(org_list)) {
							map.put("list", org_list);
						} else {
							map.put("org", orgId);
						}
					}
					list = carService.pageByWebService(map);
					recodes = carService.countByWeb(map);
				}
			}
		}else{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
			map.put("status", Constant.ADD_STATUS);
			String co = request.getParameter("car_no_r");
			if (StringUtils.isNotBlank(co)) {
				map.put("car_no", co);
			}
			String type = request.getParameter("type");
			if (StringUtils.isNotBlank(type) && !"-1".equals(type)) {
				map.put("type", type);
			}
			String show_name_car = request.getParameter("show_name_car");
			if (StringUtils.isNotBlank(show_name_car) ) {
				map.put("orgname", show_name_car);
			}
			String org = request.getParameter("hide_org");
			if (StringUtils.isNotBlank(org)) {
				//map.put("org", org);
				Long org1 = Long.parseLong(org);
				Organization userOrg = organizationService.findById(org1);
				if (userOrg.getParent() != null) {
					List<Long> o_list = organizationService.getOrganizationUser(userOrg, org1);
					if (!CollectionUtils.isEmpty(o_list)) {
						o_list.add(org1);
						map.put("list", o_list);
					} else {
						map.put("org", org1);
					}
				}
			} else {
				User u =new User();
				String id1 = request.getParameter("keyid");
				u.setId(Long.parseLong(id1));
				User user = userService.findBy(u);
				Long orgId = user.getOrg();
				Organization userOrg = orgService.findById(orgId);
				if (userOrg.getParent() != null) {
					List<Long> org_list = orgService.getOrganizationUser(userOrg, orgId);
					if (!CollectionUtils.isEmpty(org_list)) {
						org_list.add(orgId);
						map.put("list", org_list);
					} else {
						map.put("org", orgId);
					}
				}
			}
			String check = request.getParameter("check");
			if (StringUtils.isNotBlank(check) && !"-1".equals(check)) {
				map.put("car_status", check);
			}
			String obd = request.getParameter("obd");
			if (StringUtils.isNotBlank(obd) && !"-1".equals(obd)) {
				map.put("obd", obd);
			}
			String status = request.getParameter("status");
			if (StringUtils.isNotBlank(status) && !"-1".equals(status)) {
				map.put("status", status);
			}
			map.put("task_status", Constant.ADD_STATUS);
			list = carService.pageByWebService(map);
			recodes = carService.countByWeb(map);
		}
		return new Page<Car>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月12日 下午1:29:30
	 * @Description: TODO(调度驾驶员过滤当前正在执行调度或者调度时间在该申请的计划用车时间内)
	 * @param @param limit
	 * @param @param pageIndex
	 * @param @param request
	 * @param @return 设定文件  
	 * @return Page 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取调度车辆，过滤不符合条件的驾驶员" )
	@ApiOperation(value = "获取调度车辆，过滤不符合条件的驾驶员", httpMethod = "POST", response = Car.class, notes ="  limit: 15, pageIndex: 0, apply:用车申请记录id, driver_name:驾驶员姓名,driver_type:准驾车型")
	@RequestMapping(value="web/driver/page/filter",method = RequestMethod.POST)
	@ResponseBody
	public Page driver(int limit, int pageIndex, String apply, HttpServletRequest request) {
		List<Driver> list = new ArrayList<Driver>();
		int recodes = 0;
		if (StringUtils.isNotBlank(apply)) {
			Apply app = applyService.findById(Long.valueOf(apply));
			if (app != null) {
				String start = app.getPlan_time();
				String end = app.getPlan_return();
				if (start != null && end != null) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("driver_id", "1");
					map.put("start", start);
					map.put("end", end);
					List<String> ids = controlService.listByControl(map);
					map.clear();
					if (CollectionUtils.isNotEmpty(ids)) {
						map.put("lists", ids);
					}
					String name = request.getParameter("driver_name");
					if (StringUtils.isNotBlank(name)) {
						map.put("driver_name", name);
					}
					String type = request.getParameter("driver_type");
					if (StringUtils.isNotBlank(type)) {
						map.put("driver_type", type);
					}
					map.put("startIdx", pageIndex*limit);
					map.put("limit", limit);
					//map.put("d_status", Constant.DRIVER_STATE_1);
					map.put("status", Constant.ADD_STATUS);
					map.put("task_status", Constant.ADD_STATUS);
					//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
					User u =new User();
					String id = request.getParameter("keyid");
					u.setId(Long.parseLong(id));
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
					list = driverService.pageByWebService(map);
					recodes = driverService.pageByWebCount(map);
				}
			}
		}else{
			 HashMap<String, Object> map = new HashMap<String, Object>();
			    map.put("startIdx", pageIndex * limit);
			    map.put("limit", limit);
			    map.put("status", Constant.ADD_STATUS);
			    String name = request.getParameter("driver_name");
			    if (StringUtils.isNotBlank(name)) {
			      map.put("driver_name", name);
			    }
			    String no = request.getParameter("driver_no");
			    if (StringUtils.isNotBlank(no)) {
			      map.put("driver_no", no);
			    }
			    String driver_type = request.getParameter("driver_type");
			    if (StringUtils.isNotBlank(driver_type) && !"-1".equals(driver_type)) {
			      map.put("driver_type", driver_type);
			    }

			    //User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
			    
			    	User u =new User();
					String id = request.getParameter("keyid");
					u.setId(Long.parseLong(id));
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
			    map.put("task_status", Constant.ADD_STATUS);
			    //map.put("d_status", Constant.DRIVER_STATE_1);
			    list = driverService.pageByWebService(map);
			    recodes = driverService.countByWeb(map);
		}
		return new Page<Driver>(list, recodes, limit);
	}
	
	@LogAnnotation(description="获取表字段名称" )
	@ApiOperation(value = "获取表字段名称", httpMethod = "GET", response = Car.class, notes ="{car_no:车牌号}")
	@RequestMapping(value = "web/get/columns", method = RequestMethod.GET)
	@ResponseBody
	public List<Selects>  getColumns(String table_name) {
		if (StringUtils.isBlank(table_name)) {
			return null;
		}
		if(table_name.equalsIgnoreCase("yx_gps")){
			table_name = "yx_gps_current";
		}
		List<Selects> list = userService.getColumn(table_name);
		Selects selects = new Selects();
		selects.setText("车辆类型");
		selects.setValue("text");
		list.add(selects);
		return list;
		
	}
	
	@LogAnnotation(description="根据表名称获取表的信息" )
	@ApiOperation(value = "根据表名称获取表的信息", httpMethod = "POST", response = Car.class, notes ="{car_no:车牌号}")
	@TranslationControl(value = "count/gps")
	@RequestMapping(value = "web/get/tableinfo", method = RequestMethod.POST)
	@ResponseBody
	public Page<HashMap<Object, String>> getTableInfo(final Integer limit,final Integer pageIndex,final String table_name,
			final String col,final String start_time,final String end_time,final String device) throws ParseException{
				ExecutorService executor = Executors.newCachedThreadPool();  
		        FutureTask future=new FutureTask(new Callable() {  
		            public  Page<HashMap<Object,String>>  call() throws Exception {  
		                return getInfo(limit, pageIndex, table_name, col, start_time, end_time, device); 
		            }  
		        });  
		        executor.submit(future);
		        executor.shutdown();
		        Page<HashMap<Object,String>>  result = new Page<HashMap<Object,String>>();
		        result.setRows(new ArrayList<HashMap<Object,String>>());
		        try{  
		        	 result = (Page<HashMap<Object, String>>) future.get(1000 * 10, TimeUnit.MILLISECONDS) ;
			        }catch (InterruptedException e) {  
			            future.cancel(true); 
			            result.setError(Constant.GET_INTERRUPT);
			            return result;
			           
			        }catch (ExecutionException e) {  
			            future.cancel(true);  
			            result.setError(Constant.GET_EXCEPTION);
			            return result;
			        }catch (TimeoutException e) {  
			            future.cancel(true);  
			            result.setError(Constant.GET_OVERTIME);
			            return result;
			        } 
				return result;
					     		
	}
	
	
	public  Page<HashMap<Object,String>>  getInfo(Integer limit,Integer pageIndex,String table_name,String col,String start_time,String end_time,String device) throws ParseException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (limit == null || pageIndex == null || StringUtils.isBlank(table_name) || StringUtils.isBlank(col)) {
			return null;
		} else {
			String[] strings = col.split(",");
			List<String> s = new ArrayList<>();
			for (String str : strings) {
				if (str.contains("senddate")) {
					str = " date_format(" + str + ", '%Y-%m-%d %H:%i:%s') senddate";
				} else if (str.contains("createdate")) {
					str = " date_format(" + str + ", '%Y-%m-%d %H:%i:%s') createdate";
				} else if (str.contains("end")) {
					str = " date_format(" + str + ", '%Y-%m-%d %H:%i:%s') end";
				} else if (str.contains("start")) {
					str = " date_format(" + str + ", '%Y-%m-%d %H:%i:%s') start";
				}
				s.add(str);
			}
			String join = org.apache.commons.lang3.StringUtils.join(s, ",");
			map.put("col", join);
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
			map.put("device", "'" + device + "'");
			// 如果是yx_gps_current这张表不限制时间，设备
			if ("yx_gps_current".equals(table_name)) {
				if (StringUtils.isNotBlank(device)) {
					map.put("device", "'" + device + "'");
				}
				if (StringUtils.isNotBlank(start_time) && StringUtils.isNotBlank(end_time)) {
					String fromDate = simpleFormat.format(simpleFormat.parse(start_time));
					String toDate = simpleFormat.format(simpleFormat.parse(end_time));
					map.put("startTime", "'" + fromDate + "'");
					map.put("endTime", "'" + toDate + "'");
				}
			} else {
				if (StringUtils.isBlank(device) || StringUtils.isBlank(start_time) || StringUtils.isBlank(end_time)) {
					return null;
				} else if ("yx_behavior".equals(table_name)) {
					String fromDate = simpleFormat.format(simpleFormat.parse(start_time));
					String toDate = simpleFormat.format(simpleFormat.parse(end_time));
					map.put("start", "'" + fromDate + "'");
					map.put("end", "'" + toDate + "'");
				} else {
					map.put("startTime", "'" + start_time + "'");
					map.put("endTime", "'" + end_time + "'");
				}
			}

			map.put("table_name", table_name);
			List<HashMap<Object, String>> list = new ArrayList<>();
			try {
				list = behaviorService.findTableInfo(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			int recodes = behaviorService.findTableInfoNum(map);
			return new Page<HashMap<Object, String>>(list, recodes, limit);
		}

	}
	
	/**
	 * 
	 * @param request
	 * @param type  1：一直无数据   3：三天无数据  7：7天无数据
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="三日，七日，一直无数据查询" )
	@ApiOperation(value = "三日，七日，一直无数据查询", httpMethod = "POST", response = Car.class, notes ="{car_no:车牌号}")
	@RequestMapping(value = "web/gps/device/data", method = RequestMethod.POST)
	@ResponseBody
	public  Page<HashMap<String,Object>> getDeviceInfo(Integer limit,Integer pageIndex,HttpServletRequest request,String type,String org_str) throws ParseException{
		HashMap<String,Object> map = new HashMap<String,Object>();
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		int count = 0;
		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}
		
		if(StringUtils.isNotBlank(org_str)){
			String [] ss= org_str.split(",");
			map.put("org_list", ss);
		}
		String col_str = request.getParameter("col");

		if(StringUtils.isNotBlank(col_str)){
			if("1".equals(type)){
				List<String> strList =  new ArrayList<String>();
			    List<String> colList = java.util.Arrays.asList(col_str.split(","));
			    for(String str : colList){
			    	if(str.contains("ygs")){
			    		String subStr = str.substring(str.indexOf(".")+1);
			    		str = " '一直无数据无gps数据' as " + subStr;
			    	}
			    	strList.add(str);
			    }
			    map.put("col_str", org.apache.commons.lang3.StringUtils.join(strList, ","));
				list  = gpsService.getDeviceByNever(map);
				count = gpsService.getDeviceByNeverNum(map);
			}else if("3".equals(type)){
				String[] strings = col_str.split(",");
				List<String> s = new ArrayList<>();
			    for(String str : strings){
			    	if(str.contains("senddate")){
			    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') senddate";
			    		}else if(str.contains("createdate")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') createdate";
				    		}
			    		else if(str.contains("end")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') end";
				    		}
			    		else if(str.contains("start")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') start";
				    		}
			    	s.add(str);
			    }
			    String join = org.apache.commons.lang3.StringUtils.join(s, ",");
				map.put("col_str", join);
				list  = gpsService.getDeviceByThree(map);
				count = gpsService.getDeviceByThreeNum(map);
			}else if("7".equals(type)){
				String[] strings = col_str.split(",");
				List<String> s = new ArrayList<>();
			    for(String str : strings){
			    	if(str.contains("senddate")){
			    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') senddate";
			    		}else if(str.contains("createdate")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') createdate";
				    		}
			    		else if(str.contains("end")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') end";
				    		}
			    		else if(str.contains("start")){
				    		str = " date_format("+str+", '%Y-%m-%d %H:%i:%s') start";
				    		}
			    	s.add(str);
			    }
			    String join = org.apache.commons.lang3.StringUtils.join(s, ",");
				map.put("col_str", join);
				list  = gpsService.getDeviceBySeven(map);
				count = gpsService.getDeviceBySevenNum(map);
			}
		}
		return new Page<HashMap<String,Object>>(list, count, limit);
					     		
	}
	
	
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="三日，七日，一直无数据查询导出" )
	@ApiOperation(value = "三日，七日，一直无数据查询导出", httpMethod = "POST", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/gps/device/data/ex", method = RequestMethod.POST)
	@ResponseBody
	public Response exportExcel(HttpServletResponse response,HttpServletRequest request,String type,String org_str) throws ParseException {
		String col_str = request.getParameter("col");
		List<String> col_names = new ArrayList<String>();
		List<String> col_names2 = new ArrayList<String>();
		List<String> col_moments = new ArrayList<String>();
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		String[] title = new String[]{};
		// 文件名
		String fileName = "";
		// sheet名
		String sheetName = "数据明细";
		
		if(StringUtils.isNotBlank(org_str) && org_str != null){
			String [] ss= org_str.split(",");
			map.put("org_list", ss);
		}
		
		if(StringUtils.isNotBlank(col_str) && col_str != null){
			JSONArray colJson = JSONArray.fromObject(col_str);
			// 列名
			title = new String[colJson.size()];
			for(int i = 0;i<colJson.size();i++){
				JSONObject job = colJson.getJSONObject(i);
				//列的注释
				String col_mement = job.getString("text").toString();
				col_moments.add(col_mement);
				
				
				title[i] = col_mement;
				String col_name = job.getString("value").toString();
				col_names.add(col_name);
				
				String col_name2 = job.getString("value").toString();
				if(col_name.contains("ygs")){
					String subStr = col_name.substring(col_name2.indexOf(".")+1);
					col_name2 = " '一直无数据无gps数据'   as " + subStr;
				}
				col_names2.add(col_name2);
			}
			
			String col = org.apache.commons.lang.StringUtils.join(col_names.toArray(),","); 
			String col2 = org.apache.commons.lang.StringUtils.join(col_names2.toArray(),","); 
			
			if("1".equals(type)){
				map.put("col_str", col2);
				list  = gpsService.getDeviceByNeverEx(map);
				fileName = "一直无数据" + ".xls";
			}else if("3".equals(type)){
				map.put("col_str", col);
				list  = gpsService.getDeviceByThreeEx(map);
				fileName = "三日无数据" + ".xls";
			}else if("7".equals(type)){
				map.put("col_str", col);
				list  = gpsService.getDeviceBySevenEx(map);
				fileName = "七日无数据" +  ".xls";
			}
		}else{
			return  new FailedResponse("请选择需要导出的列！");
		}
		String[][] values = null;
		if(list.size() > 0){
			values = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				values[i] = new String[title.length];
				// 将对象内容转换成string
				Map maps = list.get(i);
				Iterator iter = maps.entrySet().iterator();
				int j = 0;
				while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				values[i][j] = val+"";
				j++;
				}
			}
		}else{
			values = new String[1][];
			values[0] = new String[1];
			values[0][0] = "无数据可下载，请重新查询";
		}

			HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
					null);

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
				response.setHeader("Content-Disposition", "attachment;filename="
						+ fileName);
				response.addHeader("Pargam", "no-cache");
				response.addHeader("Cache-Control", "no-cache");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	

	@LogAnnotation(description="获取机构列表" )
	@ApiOperation(value = "获取机构列表", httpMethod = "GET", response = Car.class, notes ="{car_no:车牌号}")
	@RequestMapping(value = "web/get/org/list", method = RequestMethod.GET)
	@ResponseBody
	public List<Selects>  getColumns(HttpServletRequest request) {
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		queryUtilsWeb.getOrgList(request, map);
		List<Selects> list = orgService.findOrgNameAndIds(map);
		return list;
		
	}

}
