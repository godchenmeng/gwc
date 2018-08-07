package com.youxing.car.controller;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.config.Config;
import com.youxing.car.entity.*;
import com.youxing.car.page.Page;
import com.youxing.car.generators.StringUtil;
import com.youxing.car.service.*;
import com.youxing.car.util.*;

import com.youxing.car.utils.redis.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.util.DateUtilsWeb;
import com.youxing.car.util.ExcelUtils;
import com.youxing.car.util.FindDatesUtils;
import com.youxing.car.util.GPSToBaiduGPS;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.util.Result;

import net.sf.json.JSONObject;

@Api(value = "behaivor", description = "behaivor")
@Controller
public class BehaviorController {
	@Resource
	private DeviceService deviceService;
	@Resource
	private OrganizationService orgService;
	@Resource
	private CarService carService;
	@Resource
	private UserService userService;
	@Resource
	private BehaviorService behaviorService;
	@Resource
	private CarNoticeService carNoticeService;
	@Resource
	private GpsService gpsService;
	@Resource
	private InfoCenterService infoCenterService;
	@Resource
	private ControlService controlService;
	@Resource
	private Config config;
	@Resource
	private RedisUtils redisUtils;
    
	@LogAnnotation(description="" )
	@RequestMapping(value = "web/get/car/track", method = RequestMethod.POST)
	@ResponseBody
	public String getTrack(String entity_name,String start_time,String end_time) throws ParseException {
		JSONObject jsonObj = new JSONObject();
		List<Gps> trackInfos = new ArrayList<Gps>();
		List<TrackInfo> returnInfos = new ArrayList<TrackInfo>();
		TrackInfo startTrack = new TrackInfo();
		TrackInfo endTrack = new TrackInfo();
		Long startTime = Long.parseLong(start_time) * 1000;
		Long endTime = Long.parseLong(end_time) * 1000;
		Map<String, Object> map = new HashMap<String,Object>();
		Car carInfo = null;

		if(entity_name.split(",").length > 1){
			map.put("devices", entity_name.split(","));
		}else{
			map.put("device", entity_name);
			Device device = new Device();
			device.setDevice(entity_name);
			device.setStatus(Constant.ADD_STATUS);
			device =  deviceService.findBy(device);
			carInfo = carService.findById(device.getCar_id());
		}
		map.put("startTime", DateUtilsWeb.getLong2Date(startTime));
		map.put("endTime", DateUtilsWeb.getLong2Date(endTime));
		String redisKey = entity_name + "TIME" + DateUtils.getLong2Date(startTime).substring(0,10).replace("-","");
		List<Object> objs = redisUtils.lGet(redisKey,0, redisUtils.lGetListSize(redisKey));
		if(null == objs ||  objs.size() == 0){
			trackInfos = gpsService.findListForTrack(map);
		}else{
			for(int i = 0; i < objs.size(); i++){
				String gpsVal = objs.get(i).toString();
				String carParam[] = gpsVal.substring(gpsVal.indexOf("[") + 1, gpsVal.length() - 1).split(",", -1);
				Gps gps = DataUtils.getGps(carParam);
				trackInfos.add(gps);
			}
		}
		if(trackInfos.size() > 1){
			jsonObj.put("driverInfo", controlService.getDriverByDevice(map));
			for(int i=0;i<trackInfos.size();i++){
				TrackInfo tmpInfo = new TrackInfo();
				Gps tmpNotice = trackInfos.get(i);
				double[] gps = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
				tmpInfo.setDevice(tmpNotice.getDevice());
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
				if(null != carInfo){
					tmpInfo.setCar_type(carInfo.getType());
				}
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
			jsonObj.put("message", "成功");
			jsonObj.put("status", 0);
		}
		else{
			jsonObj.put("message", "无数据");
			jsonObj.put("status", 1);
		}
		return jsonObj.toString();
	}
	/**
	 * @throws ParseException
	 *
	 * @author mars
	 * @date 2017年4月24日 上午9:43:40
	 * @Description: TODO(得到汽车的驾驶行为数据)
	 * @param @param car
	 * @param @param start
	 * @param @param end
	 * @param @return 设定文件
	 * @return List<DriverLine> 返回类型
	 * @throws
	 */
	@RequestMapping(value = "web/get/car/dr", method = RequestMethod.POST)
	@ResponseBody
	public String getBehavior(String car,String car_no, String time, HttpServletRequest request,HttpServletResponse response) throws ParseException {


		List<DriverLine> line = new ArrayList<DriverLine>();
		List<Behavior> list = new ArrayList<Behavior>();
		List<CarNotice> listInfo = new ArrayList<CarNotice>();
		List<CarNotice> sanJiInfo = new ArrayList<CarNotice>();
		DriverLine todayLine = new DriverLine();
		List<InfoCenter> alertInfo = new ArrayList<InfoCenter>();

		//本日的里程和油耗
		Float todayMileage = 0F;
		Float todayFuel = 0F;
		Float tm = 0F;
		Float tf = 0F;

		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = new JSONObject();

		if(car.equals("")){
			HashMap<String, String> maps = new HashMap<String, String>();
			maps.put("car_no_like", car_no);
			maps.put("status", Constant.ADD_STATUS);
			maps.put("car_status", Constant.ADD_STATUS);
			maps.put("obd", Constant.BIND_OBD);
			List<Car> carList = carService.listBy(maps);
			if(carList.size() > 0){
				car = carList.get(0).getId().toString();
				car_no = carList.get(0).getCar_no();
			}
			else{
				jsonObj.put("status", "404");//未找到车辆
				return jsonObj.toString();
			}
		}
		jsonObj.put("carNo", car_no);
		jsonObj.put("carID", car);
		map.put("car_id", car);
		Device device = deviceService.findByMap(map);
		if (device == null) {
			jsonObj.put("status", "400");//未找到设备
			return jsonObj.toString();
		}
		String de = device.getDevice();
		if (StringUtils.isBlank(de)) {
			jsonObj.put("status", "400");//未找到设备号
			return jsonObj.toString();
		}
		jsonObj.put("isNewSearch", setCookie(car,car_no,de,request,response));
		map.clear();
		jsonObj.put("device", de);
		map.put("device", de);
		map.put("startTime", time + " 00:00:00");
		map.put("endTime", time + " 23:59:59");

		list = behaviorService.findListByDeviceDay(map);
		if (list.size() == 0) {
			jsonObj.put("status", "401");//此车辆无当日轨迹信息
			return jsonObj.toString();
		}
		//点火熄火数据
		listInfo = carNoticeService.findListDayInfoService(map);
		for(int i = listInfo.size() - 1; i >= 0; i--){
			CarNotice tmpNotice = listInfo.get(i);
			if(tmpNotice.getAlm_id().equals("0111") || tmpNotice.getAlm_id().equals("0112") || tmpNotice.getAlm_id().equals("0113")){
				sanJiInfo.add(tmpNotice);
				listInfo.remove(tmpNotice);
			}
		}
		jsonObj.put("sanJi", sanJiInfo);//三急数据

		//报警事件
		map.put("car_id",car);
		alertInfo = infoCenterService.getInfoByCar(map);
		jsonObj.put("alertInfo", alertInfo);//三急数据

		Float mileages = 0F;
		Float fuels = 0F;
		for(Behavior bh:list){
			mileages += bh.getTrip_distance();
			fuels += bh.getTrip_fuel();
		}

		if (mileages > 0) {
			BigDecimal b = new BigDecimal(mileages / 1000.0);
			mileages = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			tm = mileages;
		}
		if (fuels > 0) {
			BigDecimal b = new BigDecimal(fuels / 1000.0);
			fuels = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			tf = fuels;
		}
		//针对A9设备，无油耗，里程数据，不做判断
		/*if (CollectionUtils.isEmpty(list) || (tm == 0 && tf ==0) ) {
			jsonObj.put("status", "401");//无驾驶记录
			return jsonObj.toString();
		}*/

		for (int i = 0; i < list.size(); i++) {
			Behavior dh = list.get(i);
			DriverLine dl = new DriverLine();
			List<Integer> zIndex = new ArrayList();
			boolean isBreakOut = false;
			for(int j= 0;j<listInfo.size();j++){
				CarNotice cc =  listInfo.get(j);
				if("0001".equals(cc.getAlm_id())){
					if(isBreakOut) break;
					zIndex.add(j);
					if(null == dl.getStart() || dl.getStart().toString().equals("")){
						//dl.setStart(DateUtilsWeb.getFormatDate(cc.getSenddate()));
						dl.setStartAddr(cc.getAddress());
					}
				}
				if("0002".equals(cc.getAlm_id()) && j > 0){
					zIndex.add(j);
					isBreakOut = true;
					//dl.setEnd(DateUtilsWeb.getFormatDate(cc.getSenddate()));
					dl.setEndAddr(cc.getAddress());
				}
			}
			for(int j = zIndex.size() - 1;j >= 0;j--){
				listInfo.remove(listInfo.get(zIndex.get(j)));
			}

			Double daMileage = (double) dh.getTrip_distance();
			if (daMileage.intValue() >= 0) {
				float valueMileage = (float) (daMileage / 1000);
				BigDecimal bMileage = new BigDecimal(valueMileage);
				float b1=bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				Double daFuel = (double) dh.getTrip_fuel();
				float valueFuel = (float) (daFuel / 1000);
				BigDecimal bFuel = new BigDecimal(valueFuel);
				float d1= bFuel.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				dl.setStart(DateUtilsWeb.getFormatDate(dh.getStart()));
				dl.setEnd(DateUtilsWeb.getFormatDate(dh.getEnd()));
				if(b1 == 0 || StringUtil.isEmpty(dl.getStartAddr()) || StringUtil.isEmpty(dl.getEndAddr()) ){
					List<Gps> trackInfos = new ArrayList<Gps>();
					map.put("device", de);
					map.put("startTime", dl.getStart());
					map.put("endTime", dl.getEnd());
					trackInfos = gpsService.findListForTrack(map);
					if(b1 == 0) {
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
						b1 = (float)(tmpDistance / 1000);
					}
					if(StringUtil.isEmpty(dl.getStartAddr()) || StringUtil.isEmpty(dl.getEndAddr())){
						if(trackInfos.size() > 0) {
							if (StringUtil.isEmpty((dl.getStartAddr()))) {
								double[] gps = GPSToBaiduGPS.wgs2bd(trackInfos.get(0).getLatitude(), trackInfos.get(0).getLongitude());
								dl.setStartAddr(BaiduMapUtils.GetAddressByCoordinate(gps[0],gps[1],config.getAk()));
							}
							if (StringUtil.isEmpty((dl.getEndAddr()))){
								double[] gps = GPSToBaiduGPS.wgs2bd(trackInfos.get(trackInfos.size() - 1).getLatitude(), trackInfos.get(trackInfos.size() - 1).getLongitude());
								dl.setEndAddr(BaiduMapUtils.GetAddressByCoordinate(gps[0],gps[1],config.getAk()));
							}
						}
					}
				}
				todayMileage += b1;
				todayFuel += d1;
				double tmpMil = bMileage.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

				dl.setMileage(b1);
				dl.setFuel(d1);
				todayLine.setDevice(de);
				dl.setDevice(de);
				Long lastTime = dh.getStart().getTime();
				if(null != dh.getLastdate()) lastTime = dh.getLastdate().getTime();
                dl.setLastdate(dh.getStart().getTime() - lastTime);
				if(tmpMil >= 0.0D){
					line.add(dl);
				}
			}
		}

		if(line.size() > 0){
			todayLine.setStart(line.get(0).getStart());
			todayLine.setStartAddr(line.get(0).getStartAddr());
			todayLine.setEnd(line.get(line.size() -1).getEnd());
			todayLine.setEndAddr(line.get(line.size() -1).getEndAddr());
			todayLine.setMileage(todayMileage);
			todayLine.setFuel(todayFuel);
		}
//		if(todayMileage < 0){
//			jsonObj.put("allDriverLine", new ArrayList<DriverLine>());
//			jsonObj.put("todayDriverLine", new DriverLine());
//		}
//		else{
			jsonObj.put("allDriverLine", line);
			jsonObj.put("todayDriverLine", todayLine);
//		}
		return jsonObj.toString();
	}

	private boolean setCookie(String car,String car_no,String device,HttpServletRequest request,HttpServletResponse response){
		boolean cookiesIsNew = true;
		try{
			Cookie[] cookies = request.getCookies();
			boolean cookiesIsSave = false;
			int oldHistoryId = 0;
			if(null != cookies){
				for(Cookie cookie : cookies){
					if(cookie.getName().equals("searchDeviceHistory1")){
						cookiesIsSave = true;
					}
				}
				if(cookiesIsSave){
					Map<String,Cookie> cookieMap = HttpUtils.ReadCookieMap(request);
					for(int i=1;i<7;i++){
						if(cookieMap.get("searchCarNoHistory" + i).getValue().equals(URLEncoder.encode(car_no, "utf-8")) && cookieMap.get("searchCarIdHistory" + i).getValue().equals(car)){
							cookiesIsNew = false;
							oldHistoryId = i;
						}
					}
					if(cookiesIsNew)
					{
						Cookie cookie = new Cookie("searchDeviceHistory6", cookieMap.get("searchDeviceHistory5").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarNoHistory6", cookieMap.get("searchCarNoHistory5").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarIdHistory6", cookieMap.get("searchCarIdHistory5").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchDeviceHistory5", cookieMap.get("searchDeviceHistory4").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarNoHistory5", cookieMap.get("searchCarNoHistory4").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarIdHistory5", cookieMap.get("searchCarIdHistory4").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchDeviceHistory4", cookieMap.get("searchDeviceHistory3").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarNoHistory4", cookieMap.get("searchCarNoHistory3").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarIdHistory4", cookieMap.get("searchCarIdHistory3").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchDeviceHistory3", cookieMap.get("searchDeviceHistory2").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarNoHistory3", cookieMap.get("searchCarNoHistory2").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarIdHistory3", cookieMap.get("searchCarIdHistory2").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchDeviceHistory2", cookieMap.get("searchCarNoHistory1").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarNoHistory2", cookieMap.get("searchCarNoHistory1").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
						cookie = new Cookie("searchCarIdHistory2", cookieMap.get("searchCarIdHistory1").getValue());
						cookie.setMaxAge(60 * 60 * 24 * 365);
						cookie.setPath("/");
						response.addCookie(cookie);
					}else{
						for(int i = oldHistoryId; i > 1; i--){
							Cookie cookie = new Cookie("searchDeviceHistory" + i, cookieMap.get("searchDeviceHistory" + (i-1)).getValue());
							cookie.setMaxAge(60 * 60 * 24 * 365);
							cookie.setPath("/");
							response.addCookie(cookie);
							cookie = new Cookie("searchCarNoHistory" + i, cookieMap.get("searchCarNoHistory" + (i-1)).getValue());
							cookie.setMaxAge(60 * 60 * 24 * 365);
							cookie.setPath("/");
							response.addCookie(cookie);
							cookie = new Cookie("searchCarIdHistory" + i, cookieMap.get("searchCarIdHistory" + (i-1)).getValue());
							cookie.setMaxAge(60 * 60 * 24 * 365);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					Cookie cookie = new Cookie("searchDeviceHistory1", device);
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory1", URLEncoder.encode(car_no, "utf-8"));
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory1", car);
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
				}else{
					Cookie cookie = new Cookie("searchDeviceHistory1", device);
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory1", URLEncoder.encode(car_no, "utf-8"));
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory1", car);
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchDeviceHistory2", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory2", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory2", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchDeviceHistory3", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory3", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory3", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchDeviceHistory4", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory4", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory4", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchDeviceHistory5", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory5", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory5", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchDeviceHistory6", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarNoHistory6", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
					cookie = new Cookie("searchCarIdHistory6", "");
					cookie.setMaxAge(60 * 60 * 24 * 365);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return cookiesIsNew;
		}
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
	
	
	
	
	
	
	@LogAnnotation(description="用车上线率统计" )
	@ApiOperation(value = "用车上线率统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/online/page", method = RequestMethod.POST)
	@ResponseBody
	public Page onLine(
			Integer limit,
			Integer pageIndex, 
			String start_time, 
			String end_time,HttpServletRequest request) throws ParseException {

		
	    String[] org_ids = request.getParameterValues("org_ids[]");
	     
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<Behavior> list = new ArrayList<Behavior>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Object> listreturn = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();
		
		Double line = 0.0;
		Double avg = 0.0;

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg  != null){
					map.put("list", listOrg);
				}
			}
			list = behaviorService.lineRate(map);
			
			if(list.size() > 0){
				for (int i = 0; i < list.size(); i++) {
					Behavior bh = list.get(i);
					String orgName = bh.getOrg_name();
					String date = bh.getDate();
					String id = bh.getOrg();
					line = bh.getOnline();
					if (listName.size() == 0) {
						 avg += line;
						maps.put("org_name", orgName);
						maps.put(date, line);
						maps.put("org_id", id);
						listName.add(orgName);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						avg += line;
						maps.put(date, line);
						if(!listDates.contains(date)){
							listDates.add(date);
						}
					} else {
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}
						avg = new BigDecimal(avg/7).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
						maps.put("average_value", avg);
						lists.add(maps);
						
						listDates.clear();
						avg =0.0;
						
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, line);
						maps.put("org_id", id);
						if(!listDates.contains(date)){
							listDates.add(date);
						}
						if(!listName.contains(orgName)){
							listName.add(orgName);
						}
					}

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j);
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}
				avg = new BigDecimal(avg/7).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
				maps.put("average_value", avg);
				lists.add(maps);
				
			}else{
				if(org_ids != null){
					map.put("list", org_ids);
					orgNames = orgService.listBy(map);
				}else{
					List<Long> listOrg = getOrg(request);
					//去除出重复元素
					if(listOrg  != null){
						for  ( int  i  =   0 ; i  <  listOrg.size()  -   1 ; i ++ )   { 
						    for  ( int  j  =  listOrg.size()  -   1 ; j  >  i; j -- )   { 
						      if  (listOrg.get(j).equals(listOrg.get(i)))   { 
						    	  listOrg.remove(j); 
						      } 
						    } 
						  } 
						map.put("list", listOrg);
					}
					orgNames =  orgService.findOrgNameService(map);
				}
				for(Organization o:orgNames){
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
							maps.put(day, 0);
							maps.put("average_value", 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();
					
				}
			}
			
			for(int mm = pageIndex * limit;mm  < ((pageIndex+1) * limit);mm++){
				if(mm < lists.size()){
					lists.get(mm);
					listreturn.add(lists.get(mm));
				}
			}
			
		//int count = behaviorService.lineRateCount(map);
		return new Page<Object>(listreturn, lists.size(), limit);

	}
	
	
	
	
	@LogAnnotation(description="用车上线率统计导出" )
	@ApiOperation(value = "用车上线率统计导出", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/online/export", method = RequestMethod.GET)
	@ResponseBody
	public Object onLineExport(
			Integer limit,
			Integer pageIndex, 
			String start_time, 
			String end_time,HttpServletRequest request,
			HttpServletResponse response) throws ParseException {

		
		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<Behavior> list = new ArrayList<Behavior>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();
		
		Double line = 0.0;
		Double avg = 0.0;

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time); 
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time); 
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "上线率数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;

		String[][] values = null;
		
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg !=null){
					map.put("list", listOrg);
				}
			}
			list = behaviorService.lineRate(map);
			
			if(list.size() > 0){
				for (int i = 0; i < list.size(); i++) {
					Behavior bh = list.get(i);
					String orgName = bh.getOrg_name();
					String date = bh.getDate();
					String id = bh.getOrg();
					line = bh.getOnline();
					if (listName.size() == 0) {
						 avg += line;
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(line)+"%");
						maps.put("org_id", id);
						listName.add(orgName);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						avg += line;
						maps.put(date, String.valueOf(line)+"%");
						if(!listDates.contains(date)){
							listDates.add(date);
						}
					} else {
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0"+"%");
							}
						}
						avg = new BigDecimal(avg/7).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
						maps.put("average_value", avg+"%");
						lists.add(maps);
						
						listDates.clear();
						avg =0.0;
						
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(line)+"%");
						maps.put("org_id", id);
						if(!listDates.contains(date)){
							listDates.add(date);
						}
						if(!listName.contains(orgName)){
							listName.add(orgName);
						}
					}

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j);
					if (!listDates.contains(day)) {
						maps.put(day, "0"+"%");
					}
				}
				avg = new BigDecimal(avg/7).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();;
				maps.put("average_value", avg+"%");
				lists.add(maps);
				
			}else{
				if(org_ids != null){
					map.put("list", org_ids);
					orgNames = orgService.listBy(map);
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
					orgNames =  orgService.findOrgNameService(map);
				}
				for(Organization o:orgNames){
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
							maps.put(day, "0"+"%");
							maps.put("average_value", "0"+"%");
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
	
	
	// 自定义下载文件
	    @LogAnnotation(description="自定义下载文件" )
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
	    
	    
	    public List<Long> getOrg(HttpServletRequest request){
			User u = new User();
			String id1 = request.getParameter("keyid");
			u.setId(Long.parseLong(id1));
			User user = userService.findBy(u);

			Long org = user.getOrg();
			Organization userOrg = orgService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = orgService.getOrganizationUser(
						userOrg, org);
				if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					return org_list;
				}
				
			}
			return null;

		}

}
