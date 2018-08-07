package com.youxing.car.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.LocationInfo;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.generators.StringUtil;
import com.youxing.car.page.Page;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.Response;
import com.youxing.car.service.CarNoticeService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.service.LocationInfoService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.DateUtilsWeb;
import com.youxing.car.util.ExcelUtils;
import com.youxing.car.util.FindDatesUtils;
import com.youxing.car.util.LatUtils;
import com.youxing.car.util.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "notice", description = "notice")
@Controller
public class CarNoticeController {
	@Resource
	private DeviceService deviceService;
	@Resource
	private UserService userService;
	@Resource
	private CarService carService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private LocationInfoService locationInfoService;
	@Resource
	private InfoCenterService infoService;
	@Resource
	private CarNoticeService carNoticeService;
	

	@LogAnnotation(description="分页获取当天定位信息" )
 	@ApiOperation(value = "分页获取当天定位信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：car:carsArray[车辆id数组],acc:accsArray[驾驶行为状态，只包含点火熄火]")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="web/car/notice/page",method=RequestMethod.POST)
	@ResponseBody
	public Page pageCarLoaction(int limit, int pageIndex, @RequestParam(value = "car", required = false) String[] car, @RequestParam(value = "acc", required = false) String[] acc,
			HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<String> device = new ArrayList<String>();
		List<LocationInfo> list = new ArrayList<LocationInfo>();
		if (car != null && car.length > 0) {
			map.put("list", car);
			device = deviceService.listDevice(map);
		}
		if (CollectionUtils.isEmpty(device)) {
			return new Page<LocationInfo>(list, 0, limit);
		} else {
			map.clear();
			Long startdate = DateUtilsWeb.getDateStart(new Date()).getTime();
			Long enddate = System.currentTimeMillis();
			map.put("device", device);
			map.put("startdate",DateUtilsWeb.getLong2Date(startdate));
			map.put("enddate",DateUtilsWeb.getLong2Date(enddate));
			map.put("acc",acc);
			int recodes = locationInfoService.countByLocationInfo(map);
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
			list = locationInfoService.pageByLocationInfo(map);
			
			
			for (LocationInfo cn : list) {
				map.put("device", cn.getDevice());
				Device dev = deviceService.findByMap(map);
				if (dev != null) {
					Long cid = dev.getCar_id();
					if (cid != null) {
						Car cars = carService.findById(cid);
						if (cars != null) {
							String brand = cars.getBrand();
							String car_no = cars.getCar_no();
							cn.setType(cars.getType());
							Long org = cars.getOrg();
							if (StringUtils.isNotBlank(brand)) {
								cn.setBrand(brand);
							}
							if (StringUtils.isNotBlank(car_no)) {
								cn.setCar(car_no);
							}
							if (org != null) {
								Organization organ = organizationService.findById(org);
								if (organ != null) {
									cn.setOrg(organ.getName());
								}
							}
						}
					}
				}
				String time = DateUtilsWeb.getFormatDate(cn.getSenddate());
				if (StringUtils.isBlank(cn.getAddress()) || "null".equals(cn.getAddress())) {
					cn.setAddress(LatUtils.getAddress(cn.getLatitude() + "," + cn.getLongitude()));
				}
				cn.setSend(time);
			}
			return new Page<LocationInfo>(list, recodes, limit);
		}
	}
	@LogAnnotation(description="分页获取当天定位信息" )
	@ApiOperation(value = "分页获取当天定位信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：devices:devicesArray[设备ID数组],acc:accsArray[驾驶行为状态，只包含运动静止离线]")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="web/get/car/notice/page",method=RequestMethod.POST)
	@ResponseBody
	public Page pageCarLoacte(int limit, int pageIndex, @RequestParam(value = "devices[]", required = false) String[] devices, @RequestParam(value = "acc[]", required = false) String[] acc,
								HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<CarNotice> list = new ArrayList<CarNotice>();
		if (devices == null || devices.length == 0 || acc == null || acc.length == 0) {
			return new Page<CarNotice>(list, 0, limit);
		} else {
			map.clear();
			Long startdate = DateUtilsWeb.getDateStart(new Date()).getTime();
			Long enddate = System.currentTimeMillis();
			map.put("device", devices);
			map.put("startdate",DateUtilsWeb.getLong2Date(startdate));
			map.put("enddate",DateUtilsWeb.getLong2Date(enddate));
			int accMap = 0;
			if(Arrays.asList(acc).contains("0") && Arrays.asList(acc).contains("1") && Arrays.asList(acc).contains("2")){ //运动、静止、离线
				accMap = 1;
			}else if(Arrays.asList(acc).contains("0") && Arrays.asList(acc).contains("1") && acc.length == 2){ //运动、离线
				accMap = 2;
			}
			else if(Arrays.asList(acc).contains("0") && Arrays.asList(acc).contains("2") && acc.length == 2){ //离线、静止
				accMap = 3;
			}
			else if(Arrays.asList(acc).contains("1") && Arrays.asList(acc).contains("2") && acc.length == 2){ //运动、静止
				accMap = 4;
			}else if(Arrays.asList(acc).contains("0") && acc.length == 1){ //离线
				accMap = 5;
			}else if(Arrays.asList(acc).contains("1") && acc.length == 1){ //运动
				accMap = 6;
			}else if(Arrays.asList(acc).contains("2") && acc.length == 1){ //静止
				accMap = 7;
			}
			map.put("acc",accMap);
			int recodes = locationInfoService.countByLocateInfo(map);
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
			list = locationInfoService.pageByLocateInfo(map);


			return new Page<CarNotice>(list, recodes, limit);
		}
	}
	
	@LogAnnotation(description="分页获取当天定位信息" )
	@ApiOperation(value = "分页获取当天定位信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：devices:devicesArray[设备ID数组],acc:accsArray[驾驶行为状态，只包含运动静止离线]")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="web/get/car/notice/excel",method=RequestMethod.POST)
	@ResponseBody
	public Object getNoticeExel(@RequestParam(value = "devices", required = false) String devices,@RequestParam(value = "acc", required = false) String acc,
			HttpServletRequest request,HttpServletResponse response) {
		
		Map<String,Object> map=new HashMap<String,Object>();
		List<CarNotice> list = new ArrayList<CarNotice>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "notice数据文件" + sdf.format(System.currentTimeMillis())+ ".xls"; 
		// sheet名
		String sheetName = "数据明细";
		// 列名
		String[] title = new String[] { "部门", "车牌", "类型", "位置", "Acc状态","通讯时间" };

		if (StringUtil.isEmpty(devices) || StringUtil.isEmpty(acc)) {
			return null;
		} else {
			map.clear();
			Long startdate = DateUtilsWeb.getDateStart(new Date()).getTime();
			Long enddate = System.currentTimeMillis();
			map.put("device", devices.split(","));
			map.put("startdate",DateUtilsWeb.getLong2Date(startdate));
			map.put("enddate",DateUtilsWeb.getLong2Date(enddate));

			String [] accs = acc.split(",");
			int accMap = 0;
			if(Arrays.asList(accs).contains("0") && Arrays.asList(accs).contains("1") && Arrays.asList(accs).contains("2")){ //运动、静止、离线
				accMap = 1;
			}else if(Arrays.asList(accs).contains("0") && Arrays.asList(accs).contains("1") && accs.length == 2){ //运动、离线
				accMap = 2;
			}
			else if(Arrays.asList(accs).contains("0") && Arrays.asList(accs).contains("2") && accs.length == 2){ //离线、静止
				accMap = 3;
			}
			else if(Arrays.asList(accs).contains("1") && Arrays.asList(accs).contains("2") && accs.length == 2){ //运动、静止
				accMap = 4;
			}else if(Arrays.asList(accs).contains("0") && accs.length == 1){ //离线
				accMap = 5;
			}else if(Arrays.asList(accs).contains("1") && accs.length == 1){ //运动
				accMap = 6;
			}else if(Arrays.asList(accs).contains("2") && accs.length == 1){ //静止
				accMap = 7;
			}
			map.put("acc",accMap);
			
			list = locationInfoService.getNoticeExcel(map);
			// 内容list
			String[][] values = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				values[i] = new String[title.length];
				// 将对象内容转换成string
				CarNotice cn = list.get(i);
				values[i][0] = cn.getOrg();
				values[i][1] = cn.getCar_no();
				values[i][2] = cn.getType();
				values[i][3] = cn.getAddress();
				String alm = cn.getAlm_id();
				if("0001".equals(alm)){
					values[i][4] = "点火";
				}if("0002".equals(alm)){
					values[i][4] = "熄火";
				}
				values[i][5] = cn.getSenddate();
			}
			
			HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,null);

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
			return ("ok");
		}
		
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
			response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@LogAnnotation(description="违规停运车辆数据统计" )
	@ApiOperation(value = "违规停运车辆数据统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value ="web/statistics/foul_park/page", method = RequestMethod.POST)
	@ResponseBody
	public Page illegalStopCar(
			String show_type,
			Integer limit,Integer pageIndex,
			String start_time,
			String end_time,HttpServletRequest request) throws ParseException {
		
		    String[] org_ids = request.getParameterValues("org_ids[]");
		    String[] car_ids = request.getParameterValues("car_ids[]");
		    
		Map<String, String> orgMap = new HashMap<String, String>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lists = new ArrayList<Object>();
		
		List<Object> listreturn = new ArrayList<Object>();
		List<CarNotice> listc = new ArrayList<CarNotice>();


		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}
		
		if("org".equals(show_type)){
		
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}
		Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
		List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
		
		for(CarNotice carStat : list) {
			List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
			
			if(!orgMap.containsKey(carStat.getOrg())) {
				orgMap.put(carStat.getOrg(), carStat.getOrgName());
			}
			
			if(carNoList == null) {
				carNoList = new ArrayList<CarNotice>();
				carNoMap.put(carStat.getCar_no(), carNoList);
			}
			
			carNoList.add(carStat);
		}
		
		Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
		for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
			List<CarNotice> carNoList = entry.getValue();
			for(CarNotice carNoStat : carNoList) {
				String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
				List<CarNotice> carStatList = carStatMap.get(key);
				if(carStatList == null) {
					carStatList = new ArrayList<CarNotice>();
					carStatMap.put(key, carStatList);
				}
				
				carStatList.add(carNoStat);
			}
		}
		
		Map<String, Integer> statMap = new HashMap<String, Integer>();
		for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
			List<CarNotice> carStatList = entry.getValue();
			boolean startFlag = false;
			for(int i = 0; i < carStatList.size();) {
				CarNotice carStat = carStatList.get(i);
				if(!startFlag && "0001".equals(carStat.getAlm_id())) {
					i++;
					continue;
				} else {
					startFlag = true;
				}
				
				
				String key = carStat.getOrg() + "_" + carStat.getDate();
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Long startDate = format.parse(carStat.getSenddate()).getTime();
				Integer count = statMap.get(key);
				if(count == null) {
					count = 0;
				}
				
				String endDate = null;
				for(int j = i + 1; j < carStatList.size(); j++) {
					CarNotice nextCarStat = carStatList.get(j);
					if("0002".equals(nextCarStat.getAlm_id())) {
						if(endDate == null) {
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							} else {
								continue;
							}
						} else {
							i = j;
							break;
						}
					} else if("0001".equals(nextCarStat.getAlm_id())) {
						endDate = nextCarStat.getSenddate();
						if(j == carStatList.size() - 1) {
							i = j + 1;
							break;
						}
					} else {
						i = j;
						break;
					}
				}
				
				if(endDate == null) {
					i++;
				}
				
				if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (10*1000)) {
					count++;
					statMap.put(key, count);
				}
			}
		}
		
		
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		List<String> listDate = new ArrayList<String>();
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
		
		for(Entry<String, Integer> entry : statMap.entrySet()){
			String key = entry.getKey();
			String orgId = key.split("_")[0];
			String date = key.split("_")[1];
			String orgName = orgMap.get(orgId);
			Integer count = entry.getValue();
			
			Map<String, Object> statCountMap = new HashMap<String, Object>();
			if(orgStatMap.containsKey(orgId)) {
				statCountMap = orgStatMap.get(orgId);
			} else {
				statCountMap.put("org_id", orgId);
				statCountMap.put("org_name", orgName);
				orgStatMap.put(orgId, statCountMap);
			}
			
			statCountMap.put(date, count);
		}
		for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
			Map<String, Object> statCountMap = entry.getValue();
			if(statCountMap.size() < listDate.size() + 2) {
				for(String date : listDate) {
					if(!statCountMap.containsKey(date)) {
						statCountMap.put(date, 0);
					}
				}
			}
			
			lists.add(statCountMap);
		}
		
		}
		
		if("car".equals(show_type)){
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.carNoStorage(map);
			
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			List<CarNotice>  statList = new ArrayList<CarNotice>();
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (10*1000)) {
						Long time = format.parse(endDate).getTime() - startDate;
						carStat.setDuration(time);
						carStat.setTrigger_type("违规停运");
						carStat.setTrigger_site(carStat.getAddress());
						carStat.setTrigger_time(carStat.getSenddate());
						statList.add(carStat);
						
					}
				}
			}
			
			for(int mm = pageIndex * limit;mm  < ((pageIndex+1) * limit);mm++){
				if(mm < statList.size()){
					listc.add((CarNotice) statList.get(mm));
				}
			}
			return new Page<CarNotice>(listc, statList.size(), limit);
		}
			
		for(int mm = pageIndex * limit;mm  < ((pageIndex+1) * limit);mm++){
			if(mm < lists.size()){
				listreturn.add(lists.get(mm));
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);
	}
	
	
	
	
	@LogAnnotation(description="违规停运车辆 按周 按月 统计" )
	@ApiOperation(value = "违规停运车辆 按周 按月 统计", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value ="web/statistics/foul_park", method = RequestMethod.GET)
	@ResponseBody
	public Response illegalStopCarMonthAndWeek(
			String start_time,
			String end_time,HttpServletRequest request) throws ParseException {
			
		    String[] org_ids = request.getParameterValues("org_ids[]");

		Map<String, String> orgMap = new HashMap<String, String>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lists = new ArrayList<Object>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}
		
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}

		
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}
		Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
		List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
		
		for(CarNotice carStat : list) {
			List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
			
			if(!orgMap.containsKey(carStat.getOrg())) {
				orgMap.put(carStat.getOrg(), carStat.getOrgName());
			}
			
			if(carNoList == null) {
				carNoList = new ArrayList<CarNotice>();
				carNoMap.put(carStat.getCar_no(), carNoList);
			}
			
			carNoList.add(carStat);
		}
		
		Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
		for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
			List<CarNotice> carNoList = entry.getValue();
			for(CarNotice carNoStat : carNoList) {
				String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
				List<CarNotice> carStatList = carStatMap.get(key);
				if(carStatList == null) {
					carStatList = new ArrayList<CarNotice>();
					carStatMap.put(key, carStatList);
				}
				
				carStatList.add(carNoStat);
			}
		}
		
		Map<String, Integer> statMap = new HashMap<String, Integer>();
		for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
			List<CarNotice> carStatList = entry.getValue();
			boolean startFlag = false;
			for(int i = 0; i < carStatList.size();) {
				CarNotice carStat = carStatList.get(i);
				if(!startFlag && "0001".equals(carStat.getAlm_id())) {
					i++;
					continue;
				} else {
					startFlag = true;
				}
				
				
				String key = carStat.getOrg() + "_" + carStat.getDate();
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Long startDate = format.parse(carStat.getSenddate()).getTime();
				Integer count = statMap.get(key);
				if(count == null) {
					count = 0;
				}
				
				String endDate = null;
				for(int j = i + 1; j < carStatList.size(); j++) {
					CarNotice nextCarStat = carStatList.get(j);
					if("0002".equals(nextCarStat.getAlm_id())) {
						if(endDate == null) {
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							} else {
								continue;
							}
						} else {
							i = j;
							break;
						}
					} else if("0001".equals(nextCarStat.getAlm_id())) {
						endDate = nextCarStat.getSenddate();
						if(j == carStatList.size() - 1) {
							i = j + 1;
							break;
						}
					} else {
						i = j;
						break;
					}
				}
				
				if(endDate == null) {
					i++;
				}
				
				if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (10*1000)) {
					count++;
					statMap.put(key, count);
				}
			}
		}
		
		
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		List<String> listDate = new ArrayList<String>();
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
		
		for(Entry<String, Integer> entry : statMap.entrySet()){
			String key = entry.getKey();
			String orgId = key.split("_")[0];
			String date = key.split("_")[1];
			String orgName = orgMap.get(orgId);
			Integer count = entry.getValue();
			
			Map<String, Object> statCountMap = new HashMap<String, Object>();
			if(orgStatMap.containsKey(orgId)) {
				statCountMap = orgStatMap.get(orgId);
			} else {
				statCountMap.put("org_id", orgId);
				statCountMap.put("org_name", orgName);
				orgStatMap.put(orgId, statCountMap);
			}
			
			statCountMap.put(date, count);
		}
		
		for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
			Map<String, Object> statCountMap = entry.getValue();
			if(statCountMap.size() < listDate.size() + 2) {
				for(String date : listDate) {
					if(!statCountMap.containsKey(date)) {
						statCountMap.put(date, 0);
					}
				}
			}
			
			lists.add(statCountMap);
		}
		return new ListResponse<Object>(lists);
	}
	
	
	
	@LogAnnotation(description="违规停运数据导出" )
	@ApiOperation(value = "违规停运数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/foul_park/export", method = RequestMethod.GET)
	@ResponseBody
	public Object illegalStopEx(String show_type,
			Integer pageIndex, String start_time, String end_time,
			HttpServletResponse response,HttpServletRequest request) throws ParseException {

		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}
		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<CarNotice>  statList = new ArrayList<CarNotice>();
		List<Object> lists = new ArrayList<Object>();

		if (StringUtils.isNotBlank(start_time) && StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "违规停运数据" + sdf.format(System.currentTimeMillis())+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;
		String[][] values = null;
		
		if("org".equals(show_type)){

			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg !=null){
					map.put("list", listOrg);
				}
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
			
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			
			Map<String, Integer> statMap = new HashMap<String, Integer>();
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					String key = carStat.getOrg() + "_" + carStat.getDate();
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					Integer count = statMap.get(key);
					if(count == null) {
						count = 0;
					}
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (10*1000)) {
						count++;
						statMap.put(key, count);
					}
				}
			}
			
			
			Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
			Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
			List<String> listDate = new ArrayList<String>();
			listDate = FindDatesUtils.findDates(dBegin, dEnd);
			Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
			
			for(Entry<String, Integer> entry : statMap.entrySet()){
				String key = entry.getKey();
				String orgId = key.split("_")[0];
				String date = key.split("_")[1];
				String orgName = orgMap.get(orgId);
				Integer count = entry.getValue();
				
				Map<String, Object> statCountMap = new HashMap<String, Object>();
				if(orgStatMap.containsKey(orgId)) {
					statCountMap = orgStatMap.get(orgId);
				} else {
					statCountMap.put("org_id", orgId);
					statCountMap.put("org_name", orgName);
					orgStatMap.put(orgId, statCountMap);
				}
				
				statCountMap.put(date, String.valueOf(count));
			}
			
			for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
				Map<String, Object> statCountMap = entry.getValue();
				if(statCountMap.size() < listDate.size()) {
					for(String date : listDate) {
						if(!statCountMap.containsKey(date)) {
							statCountMap.put(date, "0");
						}
					}
				}
				
				lists.add(statCountMap);
			}
			
			if(lists.size() > 0){
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] { "部门名称",listDate.get(0), listDate.get(1), listDate.get(2), listDate.get(3), listDate.get(4),listDate.get(5),listDate.get(6) };
					Object obj = lists.get(i);
					Map<String,String> mapobj = (Map<String,String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string
					
					for(Entry<String, String> entry : mapobj.entrySet()) {
						String  key = entry.getKey();
						
						if("org_name".equals(key)){
							values[i][0] = entry.getValue();
						}else if(listDate.get(0).equals(key)){
							values[i][1] = entry.getValue();
						}else if(listDate.get(1).equals(key)){
							values[i][2] = entry.getValue();
						}else if(listDate.get(2).equals(key)){
							values[i][3] = entry.getValue();
						}else if(listDate.get(3).equals(key)){
							values[i][4] = entry.getValue();
						}else if(listDate.get(4).equals(key)){
							values[i][5] = entry.getValue();
						}else if(listDate.get(5).equals(key)){
							values[i][6] = entry.getValue();
						}else if(listDate.get(6).equals(key)){
							values[i][7] = entry.getValue();
						}
					}
				}
				}
			
			
		}
		
		if("car".equals(show_type)){
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				map.put("list", listOrg);
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  !=null){
					for(Car car:carList){
						long id  = car.getId();
						lists.add(id);
					}
					map.put("lists", li);
				}
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
			
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (10*1000)) {
						Long time = format.parse(endDate).getTime() - startDate;
						carStat.setDuration(time);
						carStat.setTrigger_type("违规停运");
						carStat.setTrigger_site(carStat.getAddress());
						carStat.setTrigger_time(carStat.getSenddate());
						statList.add(carStat);
					}
					
				}
			}
			values = new String[statList.size()][];
			for (int k = 0; k < statList.size(); k++) {
				title = new String[] { "部门名称", "车牌号", "触发时间", "持续时长", "类型", "触发地点" };

				CarNotice cn = statList.get(k);
				values[k] = new String[title.length];
				// 将对象内容转换成string
				values[k][0] = cn.getOrgName();
				values[k][1] = cn.getCar_no();
				values[k][2] = cn.getTrigger_time();
				Long time = cn.getDuration();
				values[k][3] = String.valueOf(time);
				values[k][4] = cn.getTrigger_type();
				values[k][5] = cn.getTrigger_site();

			}
			
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
		return ("ok");
		
	}
	
	
	@LogAnnotation(description="未入库车辆数据  按周 按月统计" )
	@ApiOperation(value = "未入库车辆数据  按周 按月统计", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value ="web/statistics/not_return", method = RequestMethod.GET)
	@ResponseBody
	public Response carNoStorageMonthAndWeek(String show_type,
			Integer limit,Integer pageIndex,
			String start_time,
			String end_time,HttpServletRequest request) throws ParseException {
		    String[] org_ids = request.getParameterValues("org_ids[]");
		     
		Map<String, String> orgMap = new HashMap<String, String>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lists = new ArrayList<Object>();
		
		Map<Object, Object> maps = new HashMap<Object, Object>();
		
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		List<String> listDate = new ArrayList<String>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}
		
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg  !=null){
				map.put("list", listOrg);
			}
		}
		Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
		List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
		
		if(list.size() > 0){
		
		for(CarNotice carStat : list) {
			List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
			
			if(!orgMap.containsKey(carStat.getOrg())) {
				orgMap.put(carStat.getOrg(), carStat.getOrgName());
			}
			
			if(carNoList == null) {
				carNoList = new ArrayList<CarNotice>();
				carNoMap.put(carStat.getCar_no(), carNoList);
			}
			
			carNoList.add(carStat);
		}
		
		Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
		for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
			List<CarNotice> carNoList = entry.getValue();
			for(CarNotice carNoStat : carNoList) {
				String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
				List<CarNotice> carStatList = carStatMap.get(key);
				if(carStatList == null) {
					carStatList = new ArrayList<CarNotice>();
					carStatMap.put(key, carStatList);
				}
				
				carStatList.add(carNoStat);
			}
		}
		
		Map<String, Integer> statMap = new HashMap<String, Integer>();
		for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
			List<CarNotice> carStatList = entry.getValue();
			boolean startFlag = false;
			for(int i = 0; i < carStatList.size();) {
				CarNotice carStat = carStatList.get(i);
				if(!startFlag && "0001".equals(carStat.getAlm_id())) {
					i++;
					continue;
				} else {
					startFlag = true;
				}
				
				
				String key = carStat.getOrg() + "_" + carStat.getDate();
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Long startDate = format.parse(carStat.getSenddate()).getTime();
				Integer count = statMap.get(key);
				if(count == null) {
					count = 0;
				}
				
				String endDate = null;
				for(int j = i + 1; j < carStatList.size(); j++) {
					CarNotice nextCarStat = carStatList.get(j);
					if("0002".equals(nextCarStat.getAlm_id())) {
						if(endDate == null) {
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							} else {
								continue;
							}
						} else {
							i = j;
							break;
						}
					} else if("0001".equals(nextCarStat.getAlm_id())) {
						endDate = nextCarStat.getSenddate();
						if(j == carStatList.size() - 1) {
							i = j + 1;
							break;
						}
					} else {
						i = j;
						break;
					}
				}
				
				if(endDate == null) {
					i++;
				}
				
				if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (1*1000*60*60)) {
					count++;
					statMap.put(key, count);
				}
			}
		}
		
		
		
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
		
		for(Entry<String, Integer> entry : statMap.entrySet()){
			String key = entry.getKey();
			String orgId = key.split("_")[0];
			String date = key.split("_")[1];
			String orgName = orgMap.get(orgId);
			Integer count = entry.getValue();
			
			Map<String, Object> statCountMap = new HashMap<String, Object>();
			if(orgStatMap.containsKey(orgId)) {
				statCountMap = orgStatMap.get(orgId);
			} else {
				statCountMap.put("org_id", orgId);
				statCountMap.put("org_name", orgName);
				orgStatMap.put(orgId, statCountMap);
			}
			
			statCountMap.put(date, count);
		}
		
		for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
			Map<String, Object> statCountMap = entry.getValue();
			if(statCountMap.size() < (listDate.size()+2)) {
				for(String date : listDate) {
					if(!statCountMap.containsKey(date)) {
						statCountMap.put(date, 0);
					}
				}
			}
			
			lists.add(statCountMap);
		}
		}else{
			map.put("list", org_ids);
			orgNames = organizationService.listBy(map);
			for(Organization o:orgNames){
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
						maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();
				
			}
			
		}
		
		return new ListResponse<Object>(lists);
	}
	
	
	
	@LogAnnotation(description="未入库车辆数据统计" )
	@ApiOperation(value = "未入库车辆数据统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value ="web/statistics/not_return/page", method = RequestMethod.POST)
	@ResponseBody
	public Page carNoStorage(
			String show_type,
			Integer limit,Integer pageIndex,
			String start_time,
			String end_time,HttpServletRequest request) throws ParseException {

		String[] org_ids = request.getParameterValues("org_ids[]");
		String[] car_ids = request.getParameterValues("car_ids[]");
		List<Object> listreturn = new ArrayList<Object>();
		List<CarNotice> listc = new ArrayList<CarNotice>();

		Map<String, String> orgMap = new HashMap<String, String>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> lists = new ArrayList<Object>();
		
		Map<Object, Object> maps = new HashMap<Object, Object>();
		
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		List<String> listDate = new ArrayList<String>();
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}
		
		if("org".equals(show_type)){
		
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg !=null){
				map.put("list", listOrg);
			}
		}
		Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
		List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
		
		if(list.size() > 0){
		for(CarNotice carStat : list) {
			List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
			
			if(!orgMap.containsKey(carStat.getOrg())) {
				orgMap.put(carStat.getOrg(), carStat.getOrgName());
			}
			
			if(carNoList == null) {
				carNoList = new ArrayList<CarNotice>();
				carNoMap.put(carStat.getCar_no(), carNoList);
			}
			
			carNoList.add(carStat);
		}
		
		Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
		for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
			List<CarNotice> carNoList = entry.getValue();
			for(CarNotice carNoStat : carNoList) {
				String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
				List<CarNotice> carStatList = carStatMap.get(key);
				if(carStatList == null) {
					carStatList = new ArrayList<CarNotice>();
					carStatMap.put(key, carStatList);
				}
				
				carStatList.add(carNoStat);
			}
		}
		
		Map<String, Integer> statMap = new HashMap<String, Integer>();
		for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
			List<CarNotice> carStatList = entry.getValue();
			boolean startFlag = false;
			for(int i = 0; i < carStatList.size();) {
				CarNotice carStat = carStatList.get(i);
				if(!startFlag && "0001".equals(carStat.getAlm_id())) {
					i++;
					continue;
				} else {
					startFlag = true;
				}
				
				
				String key = carStat.getOrg() + "_" + carStat.getDate();
				SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				Long startDate = format.parse(carStat.getSenddate()).getTime();
				Integer count = statMap.get(key);
				if(count == null) {
					count = 0;
				}
				
				String endDate = null;
				for(int j = i + 1; j < carStatList.size(); j++) {
					CarNotice nextCarStat = carStatList.get(j);
					if("0002".equals(nextCarStat.getAlm_id())) {
						if(endDate == null) {
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							} else {
								continue;
							}
						} else {
							i = j;
							break;
						}
					} else if("0001".equals(nextCarStat.getAlm_id())) {
						endDate = nextCarStat.getSenddate();
						if(j == carStatList.size() - 1) {
							i = j + 1;
							break;
						}
					} else {
						i = j;
						break;
					}
				}
				
				if(endDate == null) {
					i++;
				}
				
				if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (1*1000*60*60)) {
					count++;
					statMap.put(key, count);
				}
			}
		}
		
		
		
	
		Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
		
		for(Entry<String, Integer> entry : statMap.entrySet()){
			String key = entry.getKey();
			String orgId = key.split("_")[0];
			String date = key.split("_")[1];
			String orgName = orgMap.get(orgId);
			Integer count = entry.getValue();
			
			Map<String, Object> statCountMap = new HashMap<String, Object>();
			if(orgStatMap.containsKey(orgId)) {
				statCountMap = orgStatMap.get(orgId);
			} else {
				statCountMap.put("org_id", orgId);
				statCountMap.put("org_name", orgName);
				orgStatMap.put(orgId, statCountMap);
			}
			
			statCountMap.put(date, count);
		}
		
		for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
			Map<String, Object> statCountMap = entry.getValue();
			if(statCountMap.size() < 9) {
				for(String date : listDate) {
					if(!statCountMap.containsKey(date)) {
						statCountMap.put(date, 0);
					}
				}
			}
			
			lists.add(statCountMap);
		}
		}else{
			if(org_ids != null){
				map.put("list", org_ids);
				orgNames = organizationService.listBy(map);
			}else{
				List<Long> listOrg = getOrg(request);
				//去除出重复元素
				if(listOrg !=null){
					for  ( int  i  =   0 ; i  <  listOrg.size()  -   1 ; i ++ )   { 
					    for  ( int  j  =  listOrg.size()  -   1 ; j  >  i; j -- )   { 
					      if  (listOrg.get(j).equals(listOrg.get(i)))   { 
					    	  listOrg.remove(j); 
					      } 
					    } 
					  } 
					map.put("list", listOrg);
				}
				orgNames =  organizationService.findOrgNameService(map);
			}
			for(Organization o:orgNames){
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
						maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();
				
			}
		}
		
		}
		
		if("car".equals(show_type)){
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				map.put("list", listOrg);
				List<Car> carList = carService.getCarIds(map);
				List<Long> ids = new ArrayList<>();
				if(carList !=null){
					for(Car car:carList){
						long id  = car.getId();
						ids.add(id);
					}
					map.put("lists", ids);
				}
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.carNoStorage(map);
			
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			List<CarNotice>  statList = new ArrayList<CarNotice>();
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (1*1000*60*60)) {
						Long time = (format.parse(endDate).getTime() - startDate)/60000;
						carStat.setDuration(time);
						carStat.setTrigger_site(carStat.getAddress());
						carStat.setTrigger_time(carStat.getSenddate());
						statList.add(carStat);
						
					}
				}
			}

			for(int mm = pageIndex * limit;mm  < ((pageIndex+1) * limit);mm++){
				if(mm < statList.size()){
					listc.add(statList.get(mm));
				}
			}
			return new Page<CarNotice>(listc, statList.size(), limit);
		}

		for(int mm = pageIndex * limit;mm  < ((pageIndex+1) * limit);mm++){
			if(mm < lists.size()){
				listreturn.add(lists.get(mm));
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);
	}
	
	
	@LogAnnotation(description="车辆未入库数据导出" )
	@ApiOperation(value = "车辆未入库数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/not_return/export", method = RequestMethod.GET)
	@ResponseBody
	public Object carNoStorage(String show_type,
			Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletResponse response,HttpServletRequest request) throws ParseException {
		

		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}

		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<CarNotice>  statList = new ArrayList<CarNotice>();
		

		List<Object> lists = new ArrayList<Object>();
		
		Map<Object, Object> maps = new HashMap<Object, Object>();
		
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		List<String> listDate = new ArrayList<String>();
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		
		List<Organization> orgNames = new ArrayList<Organization>();


		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "车辆未入库数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;
		String[][] values = null;
		
		if("org".equals(show_type)){
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg  !=null){
					map.put("list", listOrg);
				}
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
			
			if(list.size() > 0){
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			
			Map<String, Integer> statMap = new HashMap<String, Integer>();
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					String key = carStat.getOrg() + "_" + carStat.getDate();
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					Integer count = statMap.get(key);
					if(count == null) {
						count = 0;
					}
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (1*1000*60*60)) {
						count++;
						statMap.put(key, count);
					}
				}
			}
			
			
			
			
			Map<String, Map<String, Object>> orgStatMap = new HashMap<String, Map<String, Object>>();
			
			for(Entry<String, Integer> entry : statMap.entrySet()){
				String key = entry.getKey();
				String orgId = key.split("_")[0];
				String date = key.split("_")[1];
				String orgName = orgMap.get(orgId);
				Integer count = entry.getValue();
				
				Map<String, Object> statCountMap = new HashMap<String, Object>();
				if(orgStatMap.containsKey(orgId)) {
					statCountMap = orgStatMap.get(orgId);
				} else {
					statCountMap.put("org_id", orgId);
					statCountMap.put("org_name", orgName);
					orgStatMap.put(orgId, statCountMap);
				}
				
				statCountMap.put(date, String.valueOf(count));
			}
			
			for(Entry<String, Map<String, Object>> entry : orgStatMap.entrySet()) {
				Map<String, Object> statCountMap = entry.getValue();
				if(statCountMap.size() < 9) {
					for(String date : listDate) {
						if(!statCountMap.containsKey(date)) {
							statCountMap.put(date, "0");
						}
					}
				}
				
				lists.add(statCountMap);
			}
			}else{
				if(org_ids != null){
					map.put("list", org_ids);
					orgNames = organizationService.listBy(map);
				}else{
					List<Long> listOrg = getOrg(request);
					//去除出重复元素
					if(listOrg  !=null){
						for  ( int  i  =   0 ; i  <  listOrg.size()  -   1 ; i ++ )   { 
						    for  ( int  j  =  listOrg.size()  -   1 ; j  >  i; j -- )   { 
						      if  (listOrg.get(j).equals(listOrg.get(i)))   { 
						    	  listOrg.remove(j); 
						      } 
						    } 
						  } 
						map.put("list", listOrg);
					}
					orgNames =  organizationService.findOrgNameService(map);
				}
				for(Organization o:orgNames){
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
							maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();
					
				}
			}
			
			if(lists.size() > 0){
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] { "部门名称",listDate.get(0), listDate.get(1), listDate.get(2), listDate.get(3), listDate.get(4),listDate.get(5),listDate.get(6) };
					Object obj = lists.get(i);
					Map<String,String> mapobj = (Map<String,String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string
					
					for(Entry<String, String> entry : mapobj.entrySet()) {
						String  key = entry.getKey();
						
						if("org_name".equals(key)){
							values[i][0] = entry.getValue();
						}else if(listDate.get(0).equals(key)){
							values[i][1] = entry.getValue();
						}else if(listDate.get(1).equals(key)){
							values[i][2] = entry.getValue();
						}else if(listDate.get(2).equals(key)){
							values[i][3] = entry.getValue();
						}else if(listDate.get(3).equals(key)){
							values[i][4] = entry.getValue();
						}else if(listDate.get(4).equals(key)){
							values[i][5] = entry.getValue();
						}else if(listDate.get(5).equals(key)){
							values[i][6] = entry.getValue();
						}else if(listDate.get(6).equals(key)){
							values[i][7] = entry.getValue();
						}
					}
				}
				}
			
		}
		
		
		if("car".equals(show_type)){
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				map.put("list", listOrg);
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  !=null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			Map<String, List<CarNotice>> carNoMap = new HashMap<String, List<CarNotice>>();
			List<CarNotice> list = carNoticeService.illegalStopCarByorgService(map);
			
			for(CarNotice carStat : list) {
				List<CarNotice> carNoList = carNoMap.get(carStat.getCar_no());
				
				if(!orgMap.containsKey(carStat.getOrg())) {
					orgMap.put(carStat.getOrg(), carStat.getOrgName());
				}
				
				if(carNoList == null) {
					carNoList = new ArrayList<CarNotice>();
					carNoMap.put(carStat.getCar_no(), carNoList);
				}
				
				carNoList.add(carStat);
			}
			
			Map<String, List<CarNotice>> carStatMap = new HashMap<String, List<CarNotice>>();
			for(Entry<String, List<CarNotice>> entry : carNoMap.entrySet()) {
				List<CarNotice> carNoList = entry.getValue();
				for(CarNotice carNoStat : carNoList) {
					String key = carNoStat.getCar_no() + "_" + carNoStat.getDate();
					List<CarNotice> carStatList = carStatMap.get(key);
					if(carStatList == null) {
						carStatList = new ArrayList<CarNotice>();
						carStatMap.put(key, carStatList);
					}
					
					carStatList.add(carNoStat);
				}
			}
			for(Entry<String, List<CarNotice>> entry : carStatMap.entrySet()) {
				List<CarNotice> carStatList = entry.getValue();
				boolean startFlag = false;
				for(int i = 0; i < carStatList.size();) {
					CarNotice carStat = carStatList.get(i);
					if(!startFlag && "0001".equals(carStat.getAlm_id())) {
						i++;
						continue;
					} else {
						startFlag = true;
					}
					
					
					SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Long startDate = format.parse(carStat.getSenddate()).getTime();
					
					String endDate = null;
					for(int j = i + 1; j < carStatList.size(); j++) {
						CarNotice nextCarStat = carStatList.get(j);
						if("0002".equals(nextCarStat.getAlm_id())) {
							if(endDate == null) {
								if(j == carStatList.size() - 1) {
									i = j + 1;
									break;
								} else {
									continue;
								}
							} else {
								i = j;
								break;
							}
						} else if("0001".equals(nextCarStat.getAlm_id())) {
							endDate = nextCarStat.getSenddate();
							if(j == carStatList.size() - 1) {
								i = j + 1;
								break;
							}
						} else {
							i = j;
							break;
						}
					}
					
					if(endDate == null) {
						i++;
					}
					if(endDate != null && (format.parse(endDate).getTime() - startDate)>= (1*1000*60*60)) {
						Long time = (format.parse(endDate).getTime() - startDate)/60000;
						carStat.setDuration(time);
						carStat.setTrigger_site(carStat.getAddress());
						carStat.setTrigger_time(carStat.getSenddate());
						statList.add(carStat);
					}
					
				}
			}
			
			values = new String[statList.size()][];
			for (int k = 0; k < statList.size(); k++) {
			title = new String[] { "部门名称", "车牌号", "触发时间", "持续时长",  "触发地点" };

			CarNotice cn = statList.get(k);
			values[k] = new String[title.length];
			// 将对象内容转换成string
			values[k][0] = cn.getOrgName();
			values[k][1] = cn.getCar_no();
			values[k][2] = cn.getTrigger_time();
			Long time = cn.getDuration();
			values[k][3] = String.valueOf(time);
			values[k][4] = cn.getTrigger_site();

		}
			
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
		return ("ok");
		
	}
	
	 public List<Long> getOrg(HttpServletRequest request){
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			Long org = user.getOrg();
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(
						userOrg, org);
				if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					return org_list;
				}
				
			}
			return null;

		}
	
	
}
		

