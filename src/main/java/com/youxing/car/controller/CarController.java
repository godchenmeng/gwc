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

import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Apply;
import com.youxing.car.entity.BarsLat;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Dictionary;
import com.youxing.car.entity.Gps;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.QueryCar;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.TroubleCode;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ApplyService;
import com.youxing.car.service.BarsLatService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DictionaryService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.TroubleCodeService;
import com.youxing.car.service.UserService;


@Api(value = "car", description = "车辆管理")
@Controller
public class CarController {
	@Resource
	private CarService carService;
	@Resource
	private DictionaryService dicService;
	@Resource
	private UserService userService;
	@Resource
	private GpsService gpsService;
	@Resource
	private QueryUtils queryUtils;
	@Resource
	private ApplyService applyService;
	@Resource
	private ControlService controlService;
	@Resource
	private OrganizationService orgService;
	@Resource
	private TroubleCodeService troubleCodeService;
	@Resource
	private BarsLatService barsLatService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private DriverService driverService;
	
	private static Logger LOGGER = LoggerFactory.getLogger(CarController.class);
	/**
	 * 
	* @author mars   
	* @date 2017年5月2日 下午2:07:17 
	* @Description: TODO(车辆列表) 
	* @param @param page
	* @param @param size
	* @param @param id
	* @param @return    设定文件 
	* @return Result    返回类型 
	* @throws
	 */
	@LogAnnotation(description="车辆列表" )
	@ApiOperation(value = "车辆列表", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = Car.class) })
	@RequestMapping(value = "app/car/list", method = RequestMethod.POST)
	@ResponseBody
	public Result list(
			@ApiParam(required = true,value = "哪一页") Integer page,
			@ApiParam(required = true,value = "用车申请id") Long apply,
			@ApiParam(required = true,value = "每页大小") Integer size,
			@ApiParam(required = true,value = "用户id") Long uid,
			String start,String end
			) {
		
		if (uid == null||apply==null||page==null||size==null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		}
		try {
		List<Car> list = new ArrayList<Car>();
		if(apply > 0){
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Apply app = applyService.findById(apply);
			if (app != null) {
				String startTime = app.getPlan_time();
				String endTime = app.getPlan_return();
				if(StringUtils.isNotBlank(startTime)&&StringUtils.isNotBlank(endTime)){
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("car_id", "1");
					map.put("start", startTime);
					map.put("end", endTime);
					List<String> ids = controlService.listByControl(map);
					map.clear();
					if (CollectionUtils.isNotEmpty(ids)) {
					map.put("lists", ids);
				}
					map.put("startIdx", page*size);
					map.put("limit", size);
					map.put("car_status",Constant.ADD_STATUS);
					map.put("obd", Constant.BIND_OBD);
					map.put("status", Constant.ADD_STATUS);
					queryUtils.queryByOrg(String.valueOf(uid), map);
					map.put("task_status", Constant.ADD_STATUS);
					list = carService.pageCar(map);
				}
			}
		}else{
				if(StringUtils.isNotBlank(start)&&StringUtils.isNotBlank(end)){
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("car_id", "1");
					map.put("start", start);
					map.put("end", end);
					List<String> ids = controlService.listByControl(map);
					map.clear();
					if (CollectionUtils.isNotEmpty(ids)) {
						map.put("lists", ids);
					}
					map.put("startIdx", page*size);
					map.put("limit", size);
					map.put("car_status", Constant.CHECK_STATUS);
					map.put("obd", Constant.BIND_OBD);
					map.put("status", Constant.ADD_STATUS);
					queryUtils.queryByOrg(String.valueOf(uid), map);
					map.put("task_status", Constant.ADD_STATUS);
					list = carService.pageCar(map);
				}
		}
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/car/list",e);
			return Result.error();
		}
	}
	/**
	 * 
	* @author mars   
	* @date 2017年5月26日 上午11:17:26 
	* @Description: TODO(车辆查询) 
	* @param @param car
	* @param @return    设定文件 
	* @return Result    返回类型 
	* @throws
	 */
	@LogAnnotation(description="根据车牌号查询车辆" )
	@ApiOperation(value = "根据车牌号查询车辆", httpMethod = "GET", response = Car.class)
	@RequestMapping(value = "app/car/query", method = RequestMethod.GET)
	@ResponseBody
	public Result queryByCar(@ApiParam(value = "查询内容") String car,String name){
		try {
			List<Car> list = new ArrayList<Car>();
			List<QueryCar> tree = new ArrayList<QueryCar>();
			if(StringUtils.isBlank(car)){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("car_no_like", car);
			map.put("car_status", Constant.CHECK_STATUS);
			map.put("obd", Constant.BIND_OBD);
			map.put("status", Constant.ADD_STATUS);
			map.put("name", name);
			long oid = orgService.findIdByNameService(name);
			
			List idList1=new ArrayList();
			List<Organization> idList = orgService.findIdsByIdService(oid);
			for(Organization ids : idList){
				Long id=ids.getId();
				idList1.add(id);
			}
			idList1.add(oid);
			map.put("list", idList1);
			
			list = carService.listByNameService(map);
			for(Car cars:list){
				QueryCar ot = new QueryCar();
				ot.setId(cars.getId());
				ot.setCar_no(cars.getCar_no());
				ot.setType("2");
				tree.add(ot);
			}
			return Result.success(tree);
		} catch (Exception e) {
			LOGGER.error("app/car/query",e);
			return Result.error();
		}
	}
	/**
	 * 
	* @author mars   
	* @date 2017年5月27日 下午5:03:06 
	* @Description: TODO(车况检测) 
	* @param @param device
	* @param @return    设定文件 
	* @return Result    返回类型 
	* @throws
	 */
	@LogAnnotation(description="车况检测" )
	@ApiOperation(value = "车况检测", httpMethod = "GET", response = Car.class)
	@RequestMapping(value = "app/car/check", method = RequestMethod.GET)
	@ResponseBody
	public Result checkCar(@ApiParam(value = "设备") String device){
		try {
			Map<String,Integer> result= new HashMap<String, Integer>();
			if(StringUtils.isBlank(device)){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("device", device);
			map.put("type", "P");
			result.put("P", troubleCodeService.countByType(map));
			map.put("type", "B");
			result.put("B", troubleCodeService.countByType(map));
			map.put("type", "C");
			result.put("C", troubleCodeService.countByType(map));
			map.put("type", "U");
			result.put("U", troubleCodeService.countByType(map));
			return Result.success(result);
		} catch (Exception e) {
			LOGGER.error("app/car/check",e);
			return Result.error();
		}
	}
	/**
	 * 
	* @author mars   
	* @date 2017年5月27日 下午5:02:37 
	* @Description: TODO(车辆故障详情) 
	* @param @param device
	* @param @param type
	* @param @param page
	* @param @param size
	* @param @return    设定文件 
	* @return Result    返回类型 
	* @throws
	 */
	@LogAnnotation(description="故障详情" )
	@ApiOperation(value = "故障详情", httpMethod = "GET", response = Car.class)
	@RequestMapping(value = "app/car/check/list", method = RequestMethod.GET)
	@ResponseBody
	public Result listCheckCar(@ApiParam(value = "设备") String device,@ApiParam(value = "故障类型") String type,@ApiParam(value = "哪一页")Integer page,@ApiParam(value = "每页大小")Integer size){
		try {
			List<TroubleCode> list = new ArrayList<TroubleCode>();
			if(StringUtils.isBlank(device)||StringUtils.isBlank(type)||page==null||size==null){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("device", device);
			map.put("type", type);
			map.put("startIdx", page*size);
			map.put("limit", size);
			list = troubleCodeService.pageBy(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/car/check/list",e);
			return Result.error();
		}
	}
	
	/**
	 * 
	* @author mars   
	* @date 2017年5月27日 下午5:02:55 
	* @Description: TODO(车辆电子栅栏) 
	* @param @param car
	* @param @return    设定文件 
	* @return Result    返回类型 
	* @throws
	 */
	@LogAnnotation(description="车辆电子栅栏" )
	@ApiOperation(value = "车辆电子栅栏", httpMethod = "GET",notes="返回经纬度均是百度坐标", response = Car.class)
	@ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = BarsLat.class) })
	@RequestMapping(value = "app/car/bar", method = RequestMethod.GET)
	@ResponseBody
	public Result listCarBar(@ApiParam(value = "车辆id") Long car){
		try {
			List<List<BarsLat>> lists = new ArrayList<List<BarsLat>>();
			List<BarsLat> barid_list = new ArrayList<BarsLat>();
			List<BarsLat> list = new ArrayList<BarsLat>();
			if(car==null){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Car cars = carService.findById(car);
			if(cars == null){
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("car", car);
			barid_list = barsLatService.listBarid(map);
			for (BarsLat barsLat : barid_list) {
				map.clear();
				map.put("barid", barsLat.getBarid());
				list = barsLatService.listBy(map);
				lists.add(list);
			}
			return Result.success(lists);
		} catch (Exception e) {
			LOGGER.error("app/car/bar",e);
			return Result.error();
		}
	}
	/*
	@ApiOperation(value = "根据申请id获取车详细信息", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/car/msg", method = RequestMethod.POST)
	@ResponseBody
	public Result getCardetailMsg(@ApiParam(value = "申请ID") Long id){
		
		Map<String, Object> mapapp = new HashMap<String, Object>();
		mapapp.put("apply_id", id);
		Control control=controlService.findByMap(mapapp);
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", control.getCar_id());
        Car car=carService.findByMap(map);
        return Result.success(car);
	}
	
	*/
	
	/**
	 * 车辆数查询
	 * @return
	 */
	@LogAnnotation(description="车辆数查询" )
	@ApiOperation(value = "车辆数查询", httpMethod = "POST",notes="返回车辆总数，执行任务中的车辆数", response = Car.class)
	@RequestMapping(value = "app/car/countCar", method = RequestMethod.POST)
	@ResponseBody
	public Result countCar(String name){
		HashMap<String, Object> result = new HashMap<>();
		try {
			if(StringUtils.isBlank(name)||name.equals("null")){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Long oid = orgService.findIdByNameService(name);
			if(oid == null){
				return Result.success(result);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<Organization>  list = orgService.countChildService(oid);
			List<Long> list2=new ArrayList<Long>();
			for(Organization o:list){
				long id=o.getId();
				list2.add(id);
			}
			list2.add(oid);
			map.put("lists", list2);
			result = carService.countCarService(map);
			return Result.success(result);
		} catch (Exception e) {
			LOGGER.error("app/car/countCar",e);
			return Result.error();
		}
	}
	
	
	
	
	 /*
	  * =======================================web
	  */
	
	@RequiresPermissions(value = "car/list")
	@RequestMapping("car/list")
	public String list() {
		return "car/list";
	}
	
	/**
	 * 
	 * @author mars @date 2017年3月23日 下午4:37:52 @Description:
	 * TODO(返回某个机构下的所有汽车) @param @param id @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description="获取机构下绑定obd的汽车" )
    @ApiOperation(value = "获取机构下绑定obd的汽车", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:机构id,parent:机构父级id}")
	@RequestMapping(value="web/org/car/load", method = RequestMethod.GET)
	@ResponseBody
	public List<Selects> getOrgCar(String id) {
		List<Selects> list = new ArrayList<Selects>();
		if (StringUtils.isBlank(id)) {
			return list;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("org", Long.parseLong(id));
		map.put("status", Constant.ADD_STATUS);
		map.put("car_status", Constant.ADD_STATUS);
		map.put("obd", Constant.UNBIND_OBD);
		list = carService.listByOrg(map);
		return list;
	}
    
    

	/**
	 * 
	 * @author mars @date 2017年3月24日 上午10:08:41 @Description:
	 * TODO(这里用一句话描述这个方法的作用) @param @param car--汽车信息 @param @param
	 * type--保存类型1，车辆未审核提交 2 车辆提交并且审核 @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description="新增车辆" )
 	@ApiOperation(value = "新增车辆", httpMethod = "POST", response = Result.class, notes = "必须的参数：{car:{车辆对象},intimes:入单位时间}")
	@RequiresPermissions(value = "web/car/add")
	@RequestMapping(value="web/car/add",method=RequestMethod.POST)
	@ResponseBody
	public Response add(Car car, HttpServletRequest request, String intimes) {
		if (car == null || StringUtils.isBlank(intimes)) {
			return new FailedResponse("参数错误！");
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("car_no", car.getCar_no() + "");
		//maps.put("car_status", Constant.ADD_STATUS);
		//maps.put("status", Constant.ADD_STATUS);
		List<Car> list = carService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			return new FailedResponse("车牌被占用！");
		}

		HashMap<String, String> maps2 = new HashMap<String, String>();
		maps2.put("vin", car.getVin());
		List<Car> list2 = carService.listBy(maps2);
		if (CollectionUtils.isNotEmpty(list2)) {
			return new FailedResponse("车架号已经存在！");
		}
		try {
//			User user = (User) request.getSession()
//					.getAttribute(Constant.SESSION_KEY);
			User u =new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);
			
			//Date date = DateUtils.getDateTime(intimes);
			car.setCreatedby(user.getId());
			car.setIntime(intimes);
			car.setCreatetime(DateUtils.getDateTime());
			car.setStatus(Constant.ADD_STATUS);
			car.setCar_status(Constant.UN_CHECK_STATUS);
			car.setObd(Constant.UNBIND_OBD);
			car.setCar_image(FileUtils.checkUploadFile(car.getCar_image()));
			car.setDriver_image_1(FileUtils.checkUploadFile(car.getDriver_image_1()));
			car.setDriver_image_2(FileUtils.checkUploadFile(car.getDriver_image_2()));
			carService.add(car);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年3月24日 下午3:17:59 @Description:
	 * TODO(车辆信息分页查询) @param @param limit @param @param pageIndex @param @param
	 * request @param @return 设定文件 @return Page 返回类型 @throws 
	 */
	@LogAnnotation(description="车辆分页查询" )
 	@ApiOperation(value = "车辆分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数： {limit: 15,pageIndex: 0,car_no_r:type:-1show_name:hide_org:check:-1status:-1}")
	@RequestMapping(value="web/car/page",method=RequestMethod.POST)
	@ResponseBody
	public Page car(int limit, int pageIndex, HttpServletRequest request) {
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
//			User user = (User) request.getSession()
//					.getAttribute(Constant.SESSION_KEY);
			
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
		List<Car> list = carService.pageByWebService(map);
		int recodes = carService.countByWeb(map);
		/*
		User u =new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);
		long org1 = user.getOrg();
		List<Organization> lists=orgService.countChildService(org1);
		
		List  list2 = new ArrayList();
		for(Organization o : lists){
			long id = o.getId();
			list2.add(id);
		}
		list2.add(org1);
		map.put("lists", list2);
		int recodes1 = carService.countBy(map);*/
		return new Page<Car>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars @date 2017年3月27日 下午9:42:14 @Description:
	 * TODO(车辆修改) @param @param car @param @param intimes @param @return
	 * 设定文件 @return Response 返回类型 @throws   
	 */
	@LogAnnotation(description="车辆编辑" )
 	@ApiOperation(value = "车辆编辑", httpMethod = "POST", response = Result.class, notes = "必须的参数：{car:{车辆对象},intimes:入单位时间}")
	@RequiresPermissions(value = "web/car/modify")
 	@RequestMapping(value = "web/car/modify",method=RequestMethod.POST)
	@ResponseBody
	public Response modify(Car car, String intimes) {
		// if (car == null || StringUtils.isBlank(car.getId().toString())) {
		// return new FailedResponse("参数错误！");
		// }
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("car_no_like", car.getCar_no() + "");
		List<Car> list = carService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			if (list.size() == 1 && !list.get(0).getId().equals(car.getId())) {
				return new FailedResponse("车牌被占用！");
			}
		}

		String vin = car.getVin();
		String driverNo = car.getDriver_no();
		Map<String, Object> maps1 = new HashMap<String, Object>();
		maps1.put("id", car.getId());
		maps1.put("vin", vin);
		maps1.put("driver_no", driverNo);
		
		List<Car> cars = carService.listByDriverNoOrVin(maps1);
		for(Car carTemp : cars) {
			if(vin.equals(carTemp.getVin())) {
				return new FailedResponse("车架号已经存在！");
			} else if(driverNo.equals(carTemp.getDriver_no())) {
				return new FailedResponse("行驶证号已经存在！");
			}
		}
		
		try {
			if (StringUtils.isNotBlank(intimes)) {
			//	Date date = DateUtils.parseDate(intimes);
				car.setIntime(intimes);
			}
			car.setCar_image(FileUtils.checkUploadFile(car.getCar_image()));
			car.setDriver_image_1(FileUtils.checkUploadFile(car.getDriver_image_1()));
			car.setDriver_image_2(FileUtils.checkUploadFile(car.getDriver_image_2()));
			carService.modify(car);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年4月20日 下午1:51:23 @Description:
	 * TODO(审核车辆) @param @param ids @param @return 设定文件 @return Response
	 * 返回类型 @throws 
	 */
	@LogAnnotation(description="车辆信息提交审核" )
 	@ApiOperation(value = "车辆信息提交审核", httpMethod = "POST", response = Result.class, notes = "必须的参数： {ids:idsArray[车辆id数组]}")
	@RequiresPermissions(value = "web/car/check")
	@RequestMapping(value = "web/car/check", method = RequestMethod.POST)
	@ResponseBody
	public Response check(@RequestParam(value = "ids[]") String[] ids,String car_status,String reason) {
		if (ids.length == 0) {
			return new FailedResponse("参数错误");
		}
		try {
			for (String id : ids) {
				Car car = carService.findById(Long.parseLong(id));
				if (car != null) {
					Car cr = new Car();
					cr.setId(car.getId());
					cr.setCar_status(Constant.CHECK_STATUS);
					carService.modify(cr);
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年4月20日 下午1:55:27 @Description:
	 * TODO(删除车辆) @param @param ids @param @return 设定文件 @return Response
	 * 返回类型 @throws  
	 */
	@LogAnnotation(description="车辆删除" )
 	@ApiOperation(value = "车辆删除", httpMethod = "POST", response = Result.class, notes = "必须的参数： {ids:idsArray[车辆id数组]}")
	@RequiresPermissions(value = "web/car/delete")
	@RequestMapping(value = "web/car/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
		if (ids.length == 0) {
			return new FailedResponse("参数错误");
		}
		try {
			for (String id : ids) {
				Car mb = carService.findById(Long.parseLong(id));
				if (mb != null) {
					Car mbs = new Car();
					mbs.setId(mb.getId());
					mbs.setStatus(Constant.REMOVE_STATUS);
					carService.modifyCarAndDevice(mbs,mbs.getId());
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	
	/**
	 * 
	 * @Function: CarController::getListCarAndDriver
	 * @Description: 返回当前正在调度中的车辆和司机列表
	 * @return 
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年6月7日 上午9:49:32 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年6月7日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="获取当前正在调度中的车辆和司机列表" )
 	@ApiOperation(value = "获取当前正在调度中的车辆和司机列表", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "web/car/driver", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getListCarAndDriver() {
		JSONArray list = new JSONArray();
		try {
			String status = Constant.KS;
			//String status = Constant.WQS;
			list = carService.listCarAndDriverByStatus(status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	@LogAnnotation(description="根据当前用户权限，获取用户权限下可视车辆的状态值" )
	@ApiOperation(value = "根据当前用户权限，获取用户权限下可视车辆的状态值", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "web/get/car/status", method = RequestMethod.GET)
	@ResponseBody
	public List<Gps> getCarStatus(HttpServletRequest request) {
 		
 		try {
 		User u =new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);
		
		long org = user.getOrg();
		HashMap map = new HashMap();
		List<Organization>  list1 = orgService.countChildService(org);
		List list2=new ArrayList();
		for(Organization o:list1){
			long id=o.getId();
			list2.add(id);
		}
		list2.add(org);
		map.put("list2", list2);
		List<Gps> list3 = gpsService.getCarStatusService(map);
		return list3;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
 	
 	
	/**
	 * gy
	 * @param parent_id
	 * @return
	 */
 	@LogAnnotation(description="获取车辆类型" )
 	@ApiOperation(value = "获取车辆类型", httpMethod = "GET", response = Car.class)
	@RequestMapping(value = "web/get/car/type", method = RequestMethod.GET)
	@ResponseBody
	public Response findtextAndValue(String  parent_id){
 		Map<String, Object> map = new HashMap<String, Object>();
 		List<Dictionary> list = new ArrayList<Dictionary>();
		if(!"".equals(parent_id) && parent_id != null){
			map.put("parent_id", parent_id);
			list = dicService.findtextAndValue(map);
			return new ListResponse<Dictionary>(list);
		}else{
			return new FailedResponse("参数错误");
		}
	}
 	
 	
 	
 	@LogAnnotation(description="获取设备绑定车辆的车辆类型名称" )
 	@ApiOperation(value = "获取设备绑定车辆的车辆类型名称", httpMethod = "POST", response = Car.class)
	@RequestMapping(value = "web/get/device/car/type", method = RequestMethod.POST)
	@ResponseBody
	public Response getCDCode(HttpServletRequest request){
 		Map<String, Object> map = new HashMap<String, Object>();
 		String uid = request.getParameter("keyid");
 		User u =new User();
		u.setId(Long.parseLong(uid));
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
		
		List<Object> list = carService.getCDCode(map);
		return new ObjectResponse<Object>(list);
		
	}
 	
 	
 	@LogAnnotation(description="获取设备绑定车辆的车辆类型名称" )
 	@ApiOperation(value = "获取设备绑定车辆的车辆类型名称", httpMethod = "GET", response = Car.class)
	@RequestMapping(value = "web/dictionary/list", method = RequestMethod.GET)
	@ResponseBody
	public Response getDictionaryList(String  parentId,HttpServletRequest request){
 		
 		Map<String, Object> map = new HashMap<String, Object>();
 		if(StringUtils.isBlank(parentId)){
 			return new FailedResponse("参数错误");
 		}else{
 			map.put("parent_id", parentId);
 		}
 		List<Dictionary> list = dicService.listBy(map);
 		return new ListResponse<Dictionary>(list);
		
	}
 	


}
