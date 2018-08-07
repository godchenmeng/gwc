package com.youxing.car.controller;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.*;
import com.youxing.car.service.*;
import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "gps", description = "gps")
@Controller
public class GpsController {
	@Resource
	private DeviceService deviceService;
	@Resource
	private GpsService gpsService;
	@Resource
	private CarNoticeService carNoticeService;
	@Resource
	private BehaviorService behaviorService;
	@Resource
	private InfoCenterService infoCenterService;


	/**
	 * 
	 * @author mars   =====================================gy
	 * @date 2017年4月24日 上午9:13:35
	 * @Description: TODO(实时监控获取车辆最新的gps信息)
	 * @param @param car
	 * @param @return 设定文件
	 * @return Gps 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取gps信息" )
 	@ApiOperation(value = "获取gps信息", httpMethod = "POST", response = Result.class, notes = "必须的参数： {device:deviceArray[车辆设备号数组]}")
	@RequestMapping(value = "web/get/car/gpscurrent", method = RequestMethod.POST)
	@ResponseBody
	public List<Gps> getCarGpsCurrent(HttpServletRequest request) {
		List<Gps> result  = new ArrayList<Gps>();
		
		try {
		String[] devices = request.getParameterValues("device[]");
		Map<String,Object> map = new HashMap<String,Object>();
		if(devices == null){
			return result;
		}else{
			map.put("devices",devices);
		}
		List<Gps> list = gpsService.findListByDevice(map);
		for(int i=0;i<list.size();i++){
			Gps tmpNotice = list.get(i);
			double[] tmpGPS = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
			tmpNotice.setLatitude(tmpGPS[0]);
			tmpNotice.setLongitude(tmpGPS[1]);
			result.add(tmpNotice);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * @throws ParseException 
	 * 
	 * @author mars
	 * @date 2017年4月24日 上午9:45:08
	 * @Description: TODO(得到某段时间某个汽车的行驶gps数据)
	 * @param @param car
	 * @param @param start
	 * @param @param end
	 * @param @return 设定文件
	 * @return Gps 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取车辆行驶轨迹" )
	@RequestMapping(value = "web/get/car/line", method = RequestMethod.POST)
	@ResponseBody
	public String getCarLine(String device) throws ParseException {
		List<TrackInfo> gps = new ArrayList<TrackInfo>();
		JSONObject jsonObj = new JSONObject();
		List<CarNotice> listInfo = new ArrayList<CarNotice>();
		List<CarNotice> sanJiInfo = new ArrayList<CarNotice>();
		List<InfoCenter> alertInfo = new ArrayList<InfoCenter>();
		if (StringUtils.isBlank(device)) {
			jsonObj.put("status", 0);
			jsonObj.put("msg", "未知车辆查询");
			return jsonObj.toString();
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("device", device);
		Device d = new Device();
		d.setDevice(device);
		d.setStatus(Constant.ADD_STATUS);
		d =  deviceService.findBy(d);
		if(d != null){
		List<Gps> list = gpsService.findListForCarCurrentLine(map);
		for(int i=0;i<list.size();i++){
            Gps tmpNotice = list.get(i);
            TrackInfo tmpInfo = new TrackInfo();
			double[] tmpGPS = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
            tmpInfo.setLatitude(tmpGPS[0]);
            tmpInfo.setLongitude(tmpGPS[1]);
            tmpInfo.setDirection(tmpNotice.getDirection());
			tmpInfo.setMileage(tmpNotice.getMileage());
            tmpInfo.setLoc_time(tmpNotice.getSenddate().getTime() / 1000);
            tmpInfo.setSpeed(tmpNotice.getCar_speed());
			gps.add(tmpInfo);
		}
		jsonObj.put("points", gps);
		if(gps.size() > 0){
			map.put("startTime", list.get(0).getSenddate());
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
		map.put("car_id",d.getCar_id());
		alertInfo = infoCenterService.getInfoByCar(map);
		jsonObj.put("alertInfo", alertInfo);//三急数据
		jsonObj.put("status", 1);
		return jsonObj.toString();
		}else{
			jsonObj.put("status", 0);
			jsonObj.put("msg", "无数据");
			return jsonObj.toString();
		}
	}
	
	/**
	 * ===========================================================gy
	 * @Function: GpsController::getCarMilAndFuel
	 * @Description: 获取汽车，当前月的里程油耗信息；
	 * @param car_id 汽车id
	 * @return 油耗信息列表
	 * @throws ParseException
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月27日 上午8:46:36 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月27日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="获取车辆当月的里程油耗信息" )
 	@ApiOperation(value = "获取车辆当月的里程油耗信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：{car_id:车辆id}")
	@RequestMapping(value = "web/get/car/milfuel", method = RequestMethod.POST)
	@ResponseBody
	public List<CountEntity> getCarMilAndFuel(String car_id) throws ParseException{
		List<CountEntity> ceList = new ArrayList<CountEntity>();
		if (StringUtils.isBlank(car_id)) {
			return ceList;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("car_id", car_id);
		Device device = deviceService.findByMap(map);
		if (device == null) {
			return ceList;
		}
		String device_no = device.getDevice();
		if (StringUtils.isBlank(device_no)) {
			return ceList;
		}
		Long start = DateUtilsWeb.getfirstDayofMonthToDate().getTime();
		Long end = DateUtilsWeb.getlastDayofMonthToDate().getTime();
		map.clear();
		map.put("device", device_no);
		map.put("start", DateUtilsWeb.getLong2Date(start));
		map.put("end", DateUtilsWeb.getLong2Date(end));
		ceList = behaviorService.findListForCarMilAndFuelWeb(map);
		if(ceList != null && ceList.size() > 0){
			for (CountEntity ce : ceList) {
				Float mileages = ce.getMil();
				Float fuels = ce.getFuel();
				if(mileages == 0){
					List<Gps> trackInfos = new ArrayList<Gps>();
					Map<String, Object> trackMap = new HashMap<String,Object>();
					map.put("device", device_no);
					map.put("startTime", ce.getDay()+" 00:00:00");
					map.put("endTime", ce.getDay()+" 23:59:59");
					trackInfos = gpsService.findListForTrack(map);
					double tmpDistance = 0D;
					for(int k=0;k<trackInfos.size();k++){
						if(trackInfos.size() > 1 && (k + 1) < trackInfos.size()){
							Gps tmpNotice = trackInfos.get(k);
							Gps tmpNotice2 = trackInfos.get(k+1);
							double[] gps = GPSToBaiduGPS.wgs2bd(tmpNotice.getLatitude(), tmpNotice.getLongitude());
							double[] gps2 = GPSToBaiduGPS.wgs2bd(tmpNotice2.getLatitude(), tmpNotice2.getLongitude());
							tmpDistance += this.getShortDistance(gps[1],gps[0],gps2[1],gps2[0]);
						}
					}
					mileages = (float)(tmpDistance / 1000);
				}
				if (mileages > 0) {
					BigDecimal b = new BigDecimal(mileages / 1000.0);
					mileages = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setMil(mileages);
				}
				if (fuels > 0) {
					BigDecimal b = new BigDecimal(fuels / 1000.0);
					fuels = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setFuel(fuels);
				}
			}			
		}		
		return ceList;
	}
	
	
	/**
	 * =======================================gy
	 * @Function: GpsController::getHisDockInfoByCarId
	 * @Description: 获取汽车历史停靠信息列表
	 * @param car_id 汽车id
	 * @return  历史停靠信息列表  或 []
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月27日 上午8:52:20 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月27日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="获取车辆当天的历史停靠信息" )
 	@ApiOperation(value = "获取车辆当天的历史停靠信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：   car_id:车辆id,dateStr:查询时间，如2017-07-07")
	@RequestMapping(value = "web/get/car/hisdock", method = RequestMethod.POST)
	@ResponseBody
	public List<CarNotice> getHisDockInfoByCarId(String car_id,String dateStr){
		List<CarNotice> cnList = new ArrayList<CarNotice>();
		if (StringUtils.isBlank(car_id)) {
			return cnList;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("car_id", car_id);
		Device device = deviceService.findByMap(map);
		if (device == null) {
			return cnList;
		}
		String device_no = device.getDevice();
		if (StringUtils.isBlank(device_no)) {
			return cnList;
		}	
		Date hisDockDate = DateUtilsWeb.parseDate(dateStr);
		List<String> alm_ids = new ArrayList<String>();
		alm_ids.add(Constant.IGNITION);
		alm_ids.add(Constant.FLAMEOUT);
		Long startTime = DateUtilsWeb.getDateStart(hisDockDate).getTime();
		Long endTime = DateUtilsWeb.getDateEnd(hisDockDate).getTime();
		map.clear();
		map.put("device", device_no);
		map.put("alm_ids", alm_ids);
		map.put("startTime", DateUtilsWeb.getLong2Date(startTime));
		map.put("endTime", DateUtilsWeb.getLong2Date(endTime));
		cnList = carNoticeService.findListForHisDockInfo(map);
		
		List<CarNotice> list = new ArrayList<CarNotice>();
		if (CollectionUtils.isNotEmpty(cnList)) {
			for(int i = 0 ; i<cnList.size() ; i++){
				CarNotice cc =  cnList.get(i);
				if("0001".equals(cc.getAlm_id())){
					if(list.size() != 0){
						if(!cc.getAlm_id().equals(list.get(list.size()-1).getAlm_id())){
							double[] glist = GPSToBaiduGPS.wgs2bd(cnList.get(i).getLatitude(),cnList.get(i).getLongitude());
							if(glist != null){
								cnList.get(i).setLatitude(glist[0]);
								cnList.get(i).setLongitude(glist[1]);
								cnList.get(i).setSend(cnList.get(i).getSenddate());
								list.add(cnList.get(i));
					}
						}
						
					}else{
						double[] glist = GPSToBaiduGPS.wgs2bd(cnList.get(i).getLatitude(),cnList.get(i).getLongitude());
						if(glist != null){
							cnList.get(i).setLatitude(glist[0]);
							cnList.get(i).setLongitude(glist[1]);
							cnList.get(i).setSend(cnList.get(i).getSenddate());
							list.add(cnList.get(i));
				}
						
					}
						
			}
				if("0002".equals(cc.getAlm_id())){
					if(list.size() != 0){
						if(cc.getAlm_id().equals(list.get(list.size()-1).getAlm_id())){
							double[] glist = GPSToBaiduGPS.wgs2bd(cnList.get(i).getLatitude(),cnList.get(i).getLongitude());
	 						if(glist != null){
								cnList.get(i).setLatitude(glist[0]);
								cnList.get(i).setLongitude(glist[1]);
								cnList.get(i).setSend(cnList.get(i).getSenddate());
								//如果于上一条数据相同，替换上一条
								list.set(i-1, cnList.get(i));
					}
						}else{
							double[] glist = GPSToBaiduGPS.wgs2bd(cnList.get(i).getLatitude(),cnList.get(i).getLongitude());
	 						if(glist != null){
								cnList.get(i).setLatitude(glist[0]);
								cnList.get(i).setLongitude(glist[1]);
								cnList.get(i).setSend(cnList.get(i).getSenddate());
								list.add(cnList.get(i));
					}
							
						}
						
					}else{
						double[] glist = GPSToBaiduGPS.wgs2bd(cnList.get(i).getLatitude(),cnList.get(i).getLongitude());
						if(glist != null){
							cnList.get(i).setLatitude(glist[0]);
							cnList.get(i).setLongitude(glist[1]);
							cnList.get(i).setSend(cnList.get(i).getSenddate());
							list.add(cnList.get(i));
				}
						
					}
				}
			}

			}
		return list;
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
