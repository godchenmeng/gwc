package com.youxing.car.controller;

import com.youxing.car.entity.*;
import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.generators.StringUtil;
import com.youxing.car.service.BehaviorService;
import com.youxing.car.service.CarNoticeService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.TroubleCodeService;
import com.youxing.car.utils.other.QueryUtilsWeb;

@Api(value = "monitor", description = "车辆监控")
@Controller
public class MonitorController {
	@Resource
	private CarService carService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private ControlService controlService;
	@Resource
	private DriverService driverService;
	@Resource
	private GpsService gpsService;
	@Resource
	private MemberService memberService;
	@Resource
	private TroubleCodeService troubleCodeService;
	@Resource
	private BehaviorService behaviorService;
	@Resource
	private CarNoticeService carNoticeService;

	private static Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

	/**
	 * 
	 * @author mars @date 2017年5月15日 下午12:40:40 @Description: TODO(车辆监控) @param @param
	 *         car @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="车辆监控" )
	@ApiOperation(value = "车辆监控", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = CarNotice.class) })
	@RequestMapping(value = "app/car/gps", method = RequestMethod.POST)
	@ResponseBody
	public Result monitor(@ApiParam(value = "车辆id") Long car) {
		try {
			CarMessage gps = new CarMessage();
			String name = "";
			String dname = "";
			if (car == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			Car cars = carService.findById(car);
			if (cars == null) {
				return Result.instance(ResponseCode.data_not_exist.getCode(), ResponseCode.data_not_exist.getMsg());
			}
			Long org = cars.getOrg();
			if (org != null) {
				Organization organ = organizationService.findById(org);
				if (organ != null) {
					name = organ.getName();
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("car_id", car);
			map.put("status", Constant.ADD_STATUS);
			Device device = deviceService.findByMap(map);
			if (device == null) {
				return Result.instance(ResponseCode.no_device.getCode(), ResponseCode.no_device.getMsg());
			}
			map.put("state", new String[] { "2", "3", "5" });
			Control ct = controlService.findOneByMap(map);
			if (ct != null) {
				Long did = ct.getDriver_id();
				Driver dr = driverService.findById(did);
				if (dr != null) {
					Member mb = memberService.findById(dr.getUid());
					if (mb != null) {
						dname = mb.getName();
					}
				}
			}
			String de = device.getDevice();
			if (StringUtils.isBlank(de)) {
				return Result.instance(ResponseCode.no_obd.getCode(), ResponseCode.no_obd.getMsg());
			}
			map.clear();
			map.put("device", de);
			int count = troubleCodeService.countBy(map);
			List<CarMessage> list = gpsService.findGpsCurrentByDevice(map);
			if (CollectionUtils.isNotEmpty(list)) {
				gps = list.get(0);
				String acc = gps.getAcc();
				if ("0".equals(acc)) {
					gps.setAcc(acc);
				} else {
					if (gps.getCar_speed() == 0) {
						gps.setAcc("2");
					} else {
						gps.setAcc("1");
					}
				}
				gps.setCode(count);
				gps.setOrgName(name);
				gps.setDriverName(dname);
				gps.setCar_speed(gps.getCar_speed());
				double[] gPS = GPSToBaiduGPS.wgs2bd(gps.getLatitude(), gps.getLongitude());
				if (gPS.length>=2) {
					gps.setLatitude(gPS[0]);
					gps.setLongitude(gPS[1]);
					gps.setSend(DateUtils.getLong2Date(gps.getSenddate().getTime()));
					gps.setLoaction(LatUtils.getAddress(gPS[0] + "," + gPS[1]));
				}
				Long car_mileage = gps.getCar_mileage();
				if(car_mileage == null || car_mileage < 0 ){
					long i =0;
					car_mileage = i;
				}
				Long fuel = gps.getFuel();
				Double aa = 0.00;
				if (car_mileage > 0 && fuel > 0) {
					Double avgs = new BigDecimal(fuel / ((float)car_mileage)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					if(avgs == null || avgs < 0 ){
						gps.setAvg(aa+ "L");
					}else{
						gps.setAvg(avgs + "L");
					}
				}else{
					gps.setAvg(aa + "L");
				}
				gps.setCar_mileage(car_mileage);
				gps.setCar_no(cars.getCar_no());
				return Result.success(gps);
			}
			return Result.error("暂无车辆GPS数据！");
		} catch (Exception e) {
			LOGGER.error("app/car/gps", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars @date 2017年5月15日 下午12:57:34 @Description: TODO(按时间查询驾驶行为数据) @param @param
	 *         device @param @param date @param @return 设定文件 @return Result 返回类型 @throws
	 */
	@LogAnnotation(description="驾驶行为数据" )
	@ApiOperation(value = "驾驶行为数据", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = DriverLine.class) })
	@RequestMapping(value = "app/car/driverbehavior", method = RequestMethod.POST)
	@ResponseBody
	public Result driverLine(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每一页大小") Integer size, @ApiParam(value = "设备device") String device,
			@ApiParam(value = "日期,格式yyyy-MM-dd") String date) {
		try {
			if (page == null || size == null || StringUtils.isBlank(date) || StringUtils.isBlank(device)) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			List<DriverLine> line = new ArrayList<DriverLine>();
			List<Behavior> list = new ArrayList<Behavior>();
			List<CarNotice> listInfo = new ArrayList<CarNotice>();
			String startDate = date + " " + "00:00:01";
			String endDate = date + " " + "23:59:59";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("limit", size);
			map.put("startIdx", page * size);
			map.put("device", device);
			map.put("startTime", startDate);
			map.put("endTime", endDate);
			list = behaviorService.findListByDeviceDay(map);
			//点火熄火数据
			listInfo = carNoticeService.findListDayInfoService(map);
			if (CollectionUtils.isEmpty(list)) {
				return Result.success(line);
			}
				for (int i = 0; i < list.size(); i++) {
					Behavior dh = list.get(i);
					DriverLine dl = new DriverLine();
					List<Integer> zIndex = new ArrayList();
					boolean isBreakOut = false;
					for(int j= 0;j<listInfo.size();j++){
						CarNotice cc =  listInfo.get(j);
						if("0001".equals(cc.getAlm_id())){
							if(isBreakOut) break;
							if(null == dl.getStart() || dl.getStart().toString().equals("")){
								//dl.setStart(DateUtilsWeb.getFormatDate(cc.getSenddate()));
								if(zIndex.size() == 0){
									dl.setStart_name(cc.getAddress());
								}
							}
							zIndex.add(j);
						}
						if("0002".equals(cc.getAlm_id()) && j > 0){
							zIndex.add(j);
							isBreakOut = true;
						//	dl.setEnd(DateUtilsWeb.getFormatDate(cc.getSenddate()));
							dl.setEnd_name(cc.getAddress());
						}
					}
					for(int j = zIndex.size() - 1;j >= 0;j--){
						listInfo.remove(listInfo.get(zIndex.get(j)));
					}
					
						Long minite = DateUtils.getHour( dh.getStart(),dh.getEnd());
						dl.setRuntime(minite + "");
						float valueMileage = (float) (dh.getTrip_distance() / 1000);
						BigDecimal bMileage = new BigDecimal(valueMileage);
						float mileage=bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
						dl.setStart(DateUtilsWeb.getFormatDate(dh.getStart()));
						dl.setEnd(DateUtilsWeb.getFormatDate(dh.getEnd()));
						
						if(mileage == 0 || StringUtil.isEmpty(dl.getStart_name()) || StringUtil.isEmpty(dl.getEnd_name()) ){
							List<Gps> trackInfos = new ArrayList<Gps>();
							map.put("device", device);
							map.put("startTime", dl.getStart());
							map.put("endTime", dl.getEnd());
							trackInfos = gpsService.findListForTrack(map);
							if(mileage == 0) {
								double tmpDistance = 0D;
								for (int k = 0; k < trackInfos.size(); k++) {
									if (trackInfos.size() > 1 && (k + 1) < trackInfos.size()) {
										Gps tmpNotice = trackInfos.get(k);
										Gps tmpNotice2 = trackInfos.get(k + 1);
										double[] gps = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
										double[] gps2 = GPSToBaiduGPS.wgs2bd(tmpNotice2.getLatitude(), tmpNotice2.getLongitude());
										tmpDistance += this.getShortDistance(gps[1], gps[0], gps2[1], gps2[0]);
									}

								}
								mileage = (float)(tmpDistance / 1000);
							}
						}
						Float fuel = Float.parseFloat(dh.getTrip_fuel() / 1000.0 + "");
						
						dl.setMileage(mileage);
						dl.setFuel(fuel);
						dl.setDevice(device);
						line.add(dl);
			}
			return Result.success(line);
		} catch (Exception e) {
			LOGGER.error("app/car/driverbehavior", e);
			return Result.error();
		}
	}

	/**
	 * @author llh @date 2017年5月16日 下午4:58:42 @Description: TODO(驾驶轨迹) @param @param
	 *         device @param @param start @param @param end @param @return 设定文件 @return
	 *         Result 返回类型 @throws
	 */
	@LogAnnotation(description="驾驶轨迹" )
	@ApiOperation(value = "驾驶轨迹", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = GpsMsg.class) })
	@RequestMapping(value = "app/car/drivertrail", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject driverTrail(@ApiParam(value = "设备device") String device, @ApiParam(value = "开始时间,格式yyyy-MM-dd HH:mm:ss ") String start, @ApiParam(value = "结束时间,格式yyyy-MM-dd HH:mm:ss") String end) {
		JSONObject jsonObj = new JSONObject();
		List<Gps> trackInfos = new ArrayList<Gps>();
		List<TrackInfo> returnInfos = new ArrayList<TrackInfo>();
		TrackInfo startTrack = new TrackInfo();
		TrackInfo endTrack = new TrackInfo();
		/*Long startTime = Long.parseLong(start_time) * 1000;
		Long endTime = Long.parseLong(end_time) * 1000;*/
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("device", device);
		map.put("startTime", start);
		map.put("endTime",end);
		trackInfos = gpsService.findListForTrack(map);
		if(trackInfos.size() > 1){
			for(int i=0;i<trackInfos.size();i++){
				TrackInfo tmpInfo = new TrackInfo();
				Gps tmpNotice = trackInfos.get(i);
				double[] gps = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
				tmpInfo.setLatitude(gps[0]);
				tmpInfo.setLongitude(gps[1]);
				tmpInfo.setDirection(tmpNotice.getDirection());
				Double dMileage = (double) tmpNotice.getMileage();
				if (dMileage.intValue() > 0) {
					float fMileage = (float) (dMileage / 1000);
					BigDecimal bMileage = new BigDecimal(fMileage);
					tmpInfo.setMileage(bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				}else {
					tmpInfo.setMileage(0);
				}
				Double dFuel = (double) tmpNotice.getFuel();
				if (dFuel.intValue() > 0) {
					float fFuel = (float) (dFuel / 1000);
					BigDecimal bFuel = new BigDecimal(fFuel);
					tmpInfo.setFuel(bFuel.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				}else {
					tmpInfo.setFuel(0);
				}
				tmpInfo.setSpeed(tmpNotice.getCar_speed());
				tmpInfo.setCreate_time(DateUtils.formatDateTime(tmpNotice.getSenddate()));
				tmpInfo.setLoc_time(tmpNotice.getSenddate().getTime() / 1000);
				returnInfos.add(tmpInfo);
			}
			double[] startGPS = GPSToBaiduGPS.wgs2bd(trackInfos.get(0).getLatitude(), trackInfos.get(0).getLongitude());
			startTrack.setLatitude(startGPS[0]);
			startTrack.setLongitude(startGPS[1]);
			startTrack.setLoc_time(trackInfos.get(0).getSenddate().getTime() / 1000);
			double[] endGPS = GPSToBaiduGPS.wgs2bd(trackInfos.get(trackInfos.size() - 1).getLatitude(), trackInfos.get(trackInfos.size() - 1).getLongitude());
			endTrack.setLatitude(endGPS[0]);
			endTrack.setLongitude(endGPS[1]);
			endTrack.setLoc_time(trackInfos.get(trackInfos.size() - 1).getSenddate().getTime() / 1000);
			jsonObj.put("start_point", startTrack);
			jsonObj.put("end_point", endTrack);
			jsonObj.put("total", trackInfos.size());
			jsonObj.put("size", trackInfos.size());
			Long lMileage = trackInfos.get(0).getMileage() - trackInfos.get(trackInfos.size() - 1).getMileage();
			Double dMileage = (double) lMileage;
			if (dMileage.intValue() > 0) {
				float fMileage = (float) (dMileage / 1000);
				BigDecimal bMileage = new BigDecimal(fMileage);
				jsonObj.put("distance", bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
				jsonObj.put("toll_distance", bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
			}
			else{
				jsonObj.put("distance", 0);
				jsonObj.put("toll_distance", 0);
			}
			jsonObj.put("points", returnInfos);
			jsonObj.put("code", "10000");
			jsonObj.put("status", 0);
		}
		else{
			jsonObj.put("message", "无数据");
			jsonObj.put("status", 1);
		}
		return jsonObj;
	}
	@LogAnnotation(description="油耗统计 按月份" )
	@ApiOperation(value = "油耗统计 按月份", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = MileageAndFuel.class) })
	@RequestMapping(value = "app/car/count/oil", method = RequestMethod.GET)
	@ResponseBody
	public Result mileafeAndFuelMonth(@ApiParam(value = "设备device") String device, @ApiParam(value = "月份") Integer month, @ApiParam(value = "年份") Integer year) {
		try {
			List<MileageAndFuel> list = new ArrayList<MileageAndFuel>();
			if (StringUtils.isBlank(device) || month == null || year == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			// 返回的字段
			String[] time = DateUtils.getDayOfMonth(year, month);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("device", device);
			map.put("start", time[0]);
			map.put("end", time[1]);
			list = behaviorService.findListForCarMilAndFuel(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/car/count/oil", e);
			return Result.error();
		}
	}
	
	
	/**
	 * ====================================web
	 */
	
	@Resource
	private QueryUtilsWeb queryUtilsweb;

	/**
	 * 
	* @author mars   
	* @date 2017年4月12日 下午1:21:49 
	* @Description: TODO(实时监控页面) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@RequestMapping("monitor/gps")
	public String gps(){
		return "monitor/gps";
	}
	/**
	 * 
	* @author mars   
	* @date 2017年4月17日 上午9:12:16 
	* @Description: TODO(轨迹分析页面) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@RequestMapping("monitor/line")
	public String line(){
		return "monitor/line";
	}
	/**
	 * 
	* @author mars   
	* @date 2017年4月17日 上午9:12:44 
	* @Description: TODO(定位信息页面) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@RequestMapping("monitor/location")
	public String location(){
		return "monitor/location";
	}
	/**
	 * 
	* @author mars   
	* @date 2017年4月12日 下午1:28:16 
	* @Description: TODO(实时监控查询) 
	* @param @param request
	* @param @return    设定文件 
	* @return List<Car>    返回类型 
	* @throws
	 */
	@LogAnnotation(description="机构下车辆查询" )
 	@ApiOperation(value = "机构下车辆查询", httpMethod = "GET", response = Result.class, notes = "必须的参数：{car_no:查询的车牌号}")
	@RequestMapping(value="web/org/car/query", method = RequestMethod.GET)
	@ResponseBody
	public List<Car> getByOrgCar(HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("status", Constant.ADD_STATUS);
		map.put("obd", Constant.BIND_OBD);
		String car_no = request.getParameter("car_no");
		if(car_no != null && !"".equals(car_no)){
			map.put("car_no_like", car_no);
		}
		String device_no = request.getParameter("device_no");
		if(device_no != null && !"".equals(device_no)){
			map.put("device_no", device_no);
		}
		queryUtilsweb.queryByOrg(request, map);
		if (map.get("id") != null) {
			String org = map.get("id").toString();
			map.remove("id");
			map.put("org", org);
		}
        List<Car> list = carService.listBy(map);
		return list;
	}
	/**
	 * 
	* @author mars   
	* @date 2017年4月20日 下午3:25:27 
	* @Description: TODO(电子栅栏) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	@RequestMapping("bars/list")
	public String bars(){
		return "monitor/bars";
	}
	

	private double getShortDistance(double lng1, double lat1, double lng2,double lat2){
		double DEF_PI = 3.14159265359; // PI
		double DEF_2PI= 6.28318530712; // 2*PI
		double DEF_PI180= 0.01745329252; // PI/180.0
		double DEF_R =6370693.5; // radius of earth

		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		 double distance;
		// 角度转换为弧度
		ew1 = lng1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lng2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI)
		dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
		dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

}
