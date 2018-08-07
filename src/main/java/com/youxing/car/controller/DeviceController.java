package com.youxing.car.controller;

import com.youxing.car.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.CarService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.util.Result;



@Api(value = "device", description = "device")
@Controller
public class DeviceController {
        @Resource
        private DeviceService deviceService;
        @Resource
        private CarService carService;
        
        @Resource
        private UserService userService;
         
     	@Resource
     	private OrganizationService organizationService;
     	
    	@Resource
    	private MemberService memberService;

     	@RequiresPermissions(value = "device/list")
     	@RequestMapping("device/list")
     	public String list() {
     		return "device/list";
     	}

     	/**
     	 * 
     	 * @author mars
     	 * @date 2017年3月23日 上午11:14:24
     	 * @Description: TODO(分页查询设备页面)
     	 * @param @param limit
     	 * @param @param pageIndex
     	 * @param @param request
     	 * @param @return 设定文件  
     	 * @return Page 返回类型
     	 * @throws
     	 */
     	@LogAnnotation(description="分页查询设备" )
     	@ApiOperation(value = "分页查询设备", httpMethod = "POST", response = Result.class, notes = "必须的参数：{limit:15, pageIndex:0,car_no_r:车牌号,device_qr:设备号,hide_org:机构id}")
     	@TranslationControl(value = "device/list")
     	@RequestMapping(value="web/device/page",method=RequestMethod.POST)
     	@ResponseBody
     	public Page<Device> device(int limit, int pageIndex, HttpServletRequest request) {
     		HashMap<String, Object> map = new HashMap<String, Object>();
     		map.put("startIdx", pageIndex*limit);
     		map.put("limit", limit);
     		map.put("status", Constant.ADD_STATUS);
     		String name = request.getParameter("car_no_r");
     		if (StringUtils.isNotBlank(name)) {
     			map.put("car_no", name);
     		}
     		String no = request.getParameter("device_qr");
     		if (StringUtils.isNotBlank(no)) {
     			map.put("device", no);
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
    					map.put("org", org);
    				}
    			}
     		} else {
     		//	User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
     			
     			User u =new User();
     			String id1 = request.getParameter("keyid");
     			u.setId(Long.parseLong(id1));
     			User user = userService.findBy(u);
     			String mid = user.getMid();
     			Member member = memberService.findById(Long.parseLong(mid));
     			Long org = member.getOrg();
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
     		}
     		map.put("car_status", Constant.ADD_STATUS);
     		map.put("obd", Constant.ADD_STATUS);

     		List<Device> list = deviceService.pageByWebService(map);
     		int recodes = deviceService.countByWeb(map);
     		return new Page<Device>(list, recodes, limit);
     	}
     	
     	
     	@LogAnnotation(description="历史查询列表" )
     	@ApiOperation(value = "历史查询列表", httpMethod = "GET", response = Result.class)
     	@RequestMapping(value="web/search/device/list",method=RequestMethod.GET)
     	@ResponseBody
     	public List<Device> getSearchHistoryDevices(HttpServletRequest request) {
     		List<Device> devices = new ArrayList<Device>();
     		Cookie[] cookies = request.getCookies();
     		boolean cookiesIsSave = false;
     		if(null != cookies){
     			for(Cookie cookie : cookies){
     				if(cookie.getName().equals("searchDeviceHistory1")){
     					cookiesIsSave = true;
     				}
                 }
     		}
     		try{
     			if(cookiesIsSave){
     				Map<String,Cookie> cookieMap = HttpUtils.ReadCookieMap(request);
     				for(int i=1;i<7;i++){
     					Long carId = cookieMap.get("searchCarIdHistory" + i).getValue().equals("") ? 0L : Long.parseLong(cookieMap.get("searchCarIdHistory" + i).getValue());
     					if(carId != 0){
     						Device device = new Device();
     						device.setCar_no(URLDecoder.decode(cookieMap.get("searchCarNoHistory" + i).getValue(), "UTF-8"));
     						device.setDevice(cookieMap.get("searchDeviceHistory" + i).getValue());
     						
     						device.setCar_id(carId);
     						//device.setStatus(BaiduMapUtils.GetCarStatus(device.getDevice()));
     						devices.add(device);
     					}
     				}
     			}
     		}catch(Exception e){
     			e.printStackTrace();
     		}
     		return devices;
     	}
     	
     	/**
     	 * 
     	 * @author mars
     	 * @date 2017年3月23日 下午4:39:50
     	 * @Description: TODO(添加设备)
     	 * @param @param device
     	 * @param @return 设定文件
     	 * @return Response 返回类型  
     	 * @throws
     	 */
     	@LogAnnotation(description="添加设备" )
     	@ApiOperation(value = "添加设备", httpMethod = "POST", response = Result.class, notes = "必须的参数：{device:{设备信息对象}}")
     	@TranslationControl(value = "device/add")
     	@RequestMapping(value="web/device/add", method = RequestMethod.POST)
     	@ResponseBody
     	public Response add(Device device, HttpServletRequest request) {
     		if (device == null) {
     			return new FailedResponse("参数错误");
     		}
     		String no = device.getDevice();
     		if(StringUtils.isBlank(no)){
     			return new FailedResponse("设备编号为空");
     		}
     		HashMap<String, String> maps = new HashMap<String, String>();
     		maps.put("device", no);
     		maps.put("status", Constant.ADD_STATUS);
     		List<Device> list = deviceService.listBy(maps);
     		if (CollectionUtils.isNotEmpty(list)) {
     			return new FailedResponse("设备编号为"+no+"已经被使用");
     		}
     		Long ci = device.getCar_id();
     		if (ci == null) {
     			return new FailedResponse("参数错误");
     		}
     		try {
     			device.setCreatedate(DateUtils.getDateTime());
     			// 从登陆用户的session里面取
     			//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
     			
     			User u =new User();
     			String id1 = request.getParameter("keyid");
     			u.setId(Long.parseLong(id1));
     			User user = userService.findBy(u);
     			
     			device.setStatus(Constant.ADD_STATUS);
     			device.setCreatedby(user.getId());
     			deviceService.add(device, ci);
     			return new SuccessResponse();
     		} catch (Exception e) {
     			e.printStackTrace();
     			return new FailedResponse("服务器错误");
     		}
     	}

     	/**
     	 * 
     	 * @author mars
     	 * @date 2017年3月23日 下午5:39:18
     	 * @Description: TODO(设备修改)
     	 * @param @param device
     	 * @param @return 设定文件
     	 * @return Response 返回类型  
     	 * @throws
     	 */
     	@LogAnnotation(description="设备更新" )
     	@ApiOperation(value = "设备更新", httpMethod = "POST", response = Result.class, notes = "必须的参数：{device:{设备信息对象}}")
     	@TranslationControl(value = "device/modify")
     	@RequestMapping(value="web/device/modify",method=RequestMethod.POST)
     	@ResponseBody
     	public Response modify(Device device) {
     		if (device == null||StringUtils.isBlank(device.getDevice())) {
     			return new FailedResponse("参数错误");
     		}
     		HashMap<String, String> maps = new HashMap<String, String>();
     		maps.put("device", device.getDevice());
     		maps.put("status",Constant.ADD_STATUS);
     		List<Device> list = deviceService.listBy(maps);
     		if(CollectionUtils.isNotEmpty(list)){
     			if(list.size()==1){
     				Long de = device.getId();
     				if(!list.get(0).getId().equals(de)){
     					return new FailedResponse("设备号码重复！");
     				}
     			}else{
     				return new FailedResponse("设备号码重复！");
     			}
     		}
     		HashMap<String, String> maps1 = new HashMap<String, String>();
     		maps1.put("sim", device.getSim());
     		maps1.put("status",Constant.ADD_STATUS);
     		List<Device> list1 = deviceService.listBy(maps1);
     		if(CollectionUtils.isNotEmpty(list1)){
     			if(list1.size()==1){
     				Long de = device.getId();
     				if(!list1.get(0).getId().equals(de)){
     					return new FailedResponse("sim卡号重复！");
     				}
     			}else{
     				return new FailedResponse("sim卡号重复！");
     			}
     		}
     		try {
     			device.setUpdatedate(DateUtils.getDateTime());
     			// 从登陆用户的session里面取
     			deviceService.modify(device);
     			return new SuccessResponse();
     		} catch (Exception e) {
     			e.printStackTrace();
     			return new FailedResponse("服务器错误");
     		}
     	}

     	/**
     	 * 
     	 * @author mars
     	 * @date 2017年3月23日 下午5:53:34
     	 * @Description: TODO(删除设备不删除车)
     	 * @param @param ids
     	 * @param @return 设定文件
     	 * @return Response 返回类型
     	 * @throws
     	 */
     	@LogAnnotation(description="删除设备不删除车" )
     	@ApiOperation(value = "删除设备不删除车", httpMethod = "POST", response = Result.class, notes = "必须的参数：{ids:ids[设备id数组]}")
     	@TranslationControl(value = "device/delete")
     	@RequestMapping(value = "web/device/delete", method = RequestMethod.POST)
     	@ResponseBody
     	public Response deleteDevice(@RequestParam(value = "ids[]") String[] ids) {
     		if (ids.length == 0) {
     			return new FailedResponse("参数错误");
     		}
     		try {
     			for (String id : ids) {
     				Device mb = deviceService.findById(Long.parseLong(id));
     				if (mb != null) {
     					deviceService.modifyCar(mb);//逻辑删除设备不删除车
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
     	 * @author mars
     	 * @date 2017年3月23日 下午5:53:34
     	 * @Description: TODO(删除设备删除车)
     	 * @param @param ids
     	 * @param @return 设定文件
     	 * @return Response 返回类型
     	 * @throws
     	 */
     	@LogAnnotation(description="删除设备删除车" )
     	@ApiOperation(value = "删除设备删除车", httpMethod = "POST", response = Result.class, notes = "必须的参数：{ids:ids[设备id数组]}")
    	@TranslationControl(value = "device/delete")
     	@RequestMapping(value = "web/all/delete", method = RequestMethod.POST)
     	@ResponseBody
     	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
     		if (ids.length == 0) {
     			return new FailedResponse("参数错误");
     		}
     		try {
     			for (String id : ids) {
     				Device mb = deviceService.findById(Long.parseLong(id));
     				if (mb != null) {
     					deviceService.modify(mb,mb.getCar_id());//逻辑删除设备和车
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
     	 * @Function: DeviceController::checkSim
     	 * @Description: 设备注册时同一sim卡号只能注册一个设备检查
     	 * @param sim
     	 * @return String
     	 * @version: v1.1.0
     	 * @author: Banji Uoocent
     	 * @date: 2017年5月19日 上午9:54:13   
     	 *
     	 * Modification History:
     	 * Date         Author          Version            Description
     	 *-------------------------------------------------------------
     	 * 2017年5月19日     Banji Uoocent          v1.1.0               修改原因
     	 */
     	@LogAnnotation(description="SIM唯一验证" )
    	@ApiOperation(value = "SIM唯一验证",httpMethod = "GET",response = Result.class, notes = "必须的参数：{sim:sim卡号}")
     	@RequestMapping(value="web/device/sim", method=RequestMethod.GET)
     	@ResponseBody
     	public String checkSim(String sim){
     		if(StringUtils.isBlank(sim)){
     			return "";
     		}
     		HashMap<String, String> maps = new HashMap<String, String>();
     		maps.put("sim", sim);
     		maps.put("status", Constant.ADD_STATUS);
     		List<Device> list = deviceService.listBy(maps);
     		if (CollectionUtils.isNotEmpty(list)) {
     			return "该sim卡号已被注册！";
     		}
     		return "";
     	}
    	
    	
     	@LogAnnotation(description="添加设备" )
    	@ApiOperation(value = "添加设备", httpMethod = "POST", response = Result.class, notes = "必须的参数：{device:{设备信息对象}}")
    	@TranslationControl(value = "device/add")
     	@RequestMapping(value="app/obd/upload", method = RequestMethod.POST)
     	@ResponseBody
     	public Response addDeviceAndCar(
     			@ApiParam(required = true, name = "vehicnum", value = "车牌号")String vehicnum,
     			@ApiParam(required = true, name = "obdnum", value = "终端编号")String obdnum,
     			@ApiParam(required = true, name = "simnum", value = "SIM号")String simnum,
     			@ApiParam(required = true, name = "odometer", value = "里程表读数")String odometer,
     			@ApiParam(required = true, name = "connectway", value = "接线方式")String connectway,
     			@ApiParam(required = true, name = "remark", value = "备注")String remark,
     			@ApiParam(required = true, name = "org", value = "机构id")String org
     			) {
    		
    		Device dd= new Device();
     		if(StringUtils.isNotBlank(vehicnum)){
     			dd.setCar_no(vehicnum);
     		}
     		if(StringUtils.isNotBlank(obdnum)){
     			dd.setDevice(obdnum);
     		}
     		if(StringUtils.isNotBlank(simnum)){
     			dd.setSim(simnum);
     		}
     		if(StringUtils.isNotBlank(connectway)){
     			dd.setConnectway(connectway);
     		}
     		HashMap<String, String> maps = new HashMap<String, String>();
     		maps.put("device", obdnum);
     		maps.put("status", Constant.ADD_STATUS);
     		List<Device> list = deviceService.listBy(maps);
     		if (CollectionUtils.isNotEmpty(list)) {
     			return new FailedResponse("设备编号为"+obdnum+"已经被使用");
     		}
     		try {
     			
     			Car cc = new Car();
     			cc.setCar_no(vehicnum);
     			Car ca = carService.findBy(cc);
     			if(ca != null){
     				return new FailedResponse("该车辆已经存在");
     			}else{
     				
     				cc.setTexts(remark);
     				if(StringUtils.isBlank(odometer)){
     					long k = 0;
     					cc.setMileage(k);
     				}else{
     					cc.setMileage(Long.parseLong(odometer));
     				}
         			cc.setObd("1");
         			cc.setStatus("1");
         			long i = 1;
         			cc.setOrg(Long.parseLong(org));
         			cc.setCreatedby(i);
         			cc.setCreatetime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
         			
         			carService.add(cc);
         			
         			Car c2 =new Car();
         			c2.setCar_no(vehicnum);
         			Car car = carService.findBy(c2);
         			
         			Long car_id = car.getId();
         			
         			dd.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
         			dd.setCar_id(car_id);
         			dd.setStatus("1");
         			dd.setCreatedby(i);
         			deviceService.add(dd);
         			
         			return new SuccessResponse();
     				
     			}
     			
     		} catch (Exception e) {
     			e.printStackTrace();
     			return new FailedResponse("服务器错误");
     		}
     	}

}
