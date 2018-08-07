package com.youxing.car.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.DriverLine;
import com.youxing.car.entity.SpeedingInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author CM
 * @date 2017年5月26日
 *
 */
public class BaiduMapUtils {
	
	private static final Logger logger = Logger.getLogger(HttpUtils.class);
	
//	private static String BAIDUMAP_BASIC_PARAM = "ak=0v3mDWnry0Vjx2NChnaVWnbG&service_id=141770";
	private static String BAIDUMAP_BASIC_PARAM = "ak=pxBudaGK5Aez1hbfs5FQs1D5Ng0c19nH&service_id=148410";
	
	private static final String CCF_URL = "http://yingyan.baidu.com/api/v3/fence/createcirclefence";
	
	private static final String CPF_URL = "http://yingyan.baidu.com/api/v3/fence/createpolygonfence";
	
	private static final String DEL_URL = "http://yingyan.baidu.com/api/v3/fence/delete";
	/**
	 * @author CM
	 * @date 2017年5月26日
	 * @Description 获取停留点
	 * @param device 硬件编号
	 * @param startTime 记录起始时间
	 * @param endTime 记录结束时间
	 * @return List<DriverLine>
	 * @throws 
	 */
	public static List<DriverLine> GetStayPoints(String device,long startTime,long endTime) {
		List<DriverLine> stopTimes = new ArrayList<DriverLine>();
		String getStayPointURL = "http://yingyan.baidu.com/api/v3/analysis/staypoint";
		
		//详细参数详见http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/analysis#staypoint
		String getStayPointParam = "&entity_name=" + device + "&start_time=" + startTime + "&end_time=" + endTime + "&process_option=need_mapmatch=1";
		
		//String getStayPointResult = "{status:0,message:\"成功\",staypoint_num:3,stay_points:[{start_time:1464164829,end_time:1464166832,duration:2003,stay_point:{longitude:113.22167932597,latitude:23.411219209009,coord_type:3}},{start_time:1464170138,end_time:1464171664,duration:1526,stay_point:{longitude:113.20923398543,latitude:23.411707425027,coord_type:3}},{start_time:1464172067,end_time:1464172952,duration:885,stay_point:{longitude:113.207836,latitude:23.40664,coord_type:3}}]}";

		String getStayPointResult = HttpUtils.methodGet(getStayPointURL, BAIDUMAP_BASIC_PARAM + getStayPointParam);
		JSONObject returnObject = JSONObject.fromObject(getStayPointResult);
		int returnStatus = returnObject.getInt("status");
		
		if(returnStatus == 0){
			int staySize = returnObject.getInt("staypoint_num");
			if(staySize > 0){
				JSONArray stayPoints = returnObject.getJSONArray("stay_points");
				Iterator<JSONObject> tmpIterator = stayPoints.iterator();
				while (tmpIterator.hasNext()) {
					JSONObject stayPoint = tmpIterator.next();
					DriverLine stopTime = new DriverLine();
					stopTime.setStart(stayPoint.getString("start_time"));
					stopTime.setEnd(stayPoint.getString("end_time"));
					stopTimes.add(stopTime);
				}
			}
		}
		return stopTimes;
	}
	
	/**
	 * @author CM
	 * @date 2017年5月26日
	 * @Description 获取轨迹记录点
	 * @param device 硬件编号
	 * @param startTime 记录起始时间
	 * @param endTime 记录结束时间
	 * @return List<CarNotice> 返回轨迹点记录
	 * @throws 
	 */
	public static List<CarNotice> GetTrackPoints(String device,long startTime,long endTime) {
		List<CarNotice> trackPoints = new ArrayList<CarNotice>();
//		DriverLine trackInfo = new DriverLine();
//		HashMap<DriverLine,List<CarNotice>> trackUnionInfo = new HashMap<DriverLine,List<CarNotice>>();
//		List<HashMap<DriverLine,List<CarNotice>>> backInfo = new ArrayList<HashMap<DriverLine,List<CarNotice>>>();
		String getTrackInfoURL = "http://yingyan.baidu.com/api/v3/track/gettrack";
		
		//详细参数详见http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/trackprocess#gettrack
		String getTrackInfoParamFirst = "&entity_name=" + device + "&start_time=" + startTime + "&end_time=" + endTime 
										+ "&is_processed=1&supplement_mode=driving&page_index=1&page_size=5000&process_option=need_mapmatch=1";
		String getTrackInfoParamSecond = "&entity_name=" + device + "&start_time=" + startTime + "&end_time=" + endTime 
											+ "&is_processed=1&supplement_mode=driving&page_index=2&page_size=5000&process_option=need_mapmatch=1";
		
		//String getTrackInfoResult = "{\"status\":0,\"message\":\"成功\",\"total\":10156,\"size\":100,\"distance\":70101.769271664,\"toll_distance\":0,\"start_point\":{\"longitude\":121.47756835641,\"latitude\":31.228864478309,\"loc_time\":1487210008},\"end_point\":{\"longitude\":121.66034736775,\"latitude\":31.145494901873,\"loc_time\":1487260798},\"points\":[{\"loc_time\":1487210008,\"latitude\":31.228864478309,\"longitude\":121.47756835641,\"create_time\":\"2017-02-16 09:53:56\",\"direction\":160,\"height\":5,\"key1\":\"value1\",\"radius\":30,\"speed\":6.8},{\"loc_time\":1487210013,\"latitude\":31.228900948179,\"longitude\":121.47727614458,\"create_time\":\"2017-02-16 09:53:56\",\"direction\":0,\"height\":36,\"key1\":\"value1\",\"radius\":10,\"speed\":0},{\"loc_time\":1487210503,\"latitude\":31.232230380323,\"longitude\":121.47848441484,\"create_time\":\"2017-02-16 10:02:09\",\"direction\":228,\"height\":6,\"key1\":\"value1\",\"radius\":30,\"speed\":1.87}]}";

		String getTrackInfoResult = HttpUtils.methodGet(getTrackInfoURL, BAIDUMAP_BASIC_PARAM + getTrackInfoParamFirst);
		int isOK = getTrackInfoResult.indexOf("\"status\":0");
		if(isOK < 0) return null;
		JSONObject returnObject = JSONObject.fromObject(getTrackInfoResult);
		int returnStatus = returnObject.getInt("status");
		if(returnStatus == 0){
			int returnSize = returnObject.getInt("size");
			if(returnSize > 0){
	//			trackInfo.setStart(returnObject.getJSONObject("start_point").getString("loc_time"));
	//			trackInfo.setEnd(returnObject.getJSONObject("end_point").getString("loc_time"));
				JSONArray tracks = returnObject.getJSONArray("points");
				Iterator<JSONObject> tmpIterator = tracks.iterator();
				while (tmpIterator.hasNext()) {
					JSONObject track = tmpIterator.next();
					CarNotice trackPoint = new CarNotice();
					trackPoint.setLatitude(track.getDouble("latitude"));
					trackPoint.setLongitude(track.getDouble("longitude"));
					trackPoint.setCar_speed(track.getInt("speed"));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
				    Date date = null;
					try {
						date = sdf.parse(DateUtils.getLong2Date(track.getLong("loc_time")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					trackPoint.setSenddate(sdf.format(date));
					trackPoint.setDirection(track.getDouble("direction"));
					if(track.containsKey("fuel")) {
						trackPoint.setFuel(track.getLong("fuel"));
					}
					if(track.containsKey("mileage")) {
						trackPoint.setMileage(track.getLong("mileage"));
					}
					trackPoints.add(trackPoint);
				}
	//			trackUnionInfo.put(trackInfo, trackPoints);
	//			backInfo.add(trackUnionInfo);
			}
		}
		
//		trackPoints = new ArrayList<CarNotice>();
//		trackInfo = new DriverLine();
		//String getTrackInfoResultSecond = "{status:400,size:0}";

		String getTrackInfoResultSecond = HttpUtils.methodGet(getTrackInfoURL, BAIDUMAP_BASIC_PARAM + getTrackInfoParamSecond);
		isOK = getTrackInfoResultSecond.indexOf("\"status\":0");
		if(isOK < 0) return null;
		JSONObject returnObjectSecond = JSONObject.fromObject(getTrackInfoResultSecond);
		int returnStatusSecond = returnObjectSecond.getInt("status");
		if(returnStatusSecond == 0){
			int returnSize = returnObject.getInt("size");
			if(returnSize > 0){
	//			trackInfo.setStart(returnObjectSecond.getJSONObject("start_point").getString("loc_time"));
	//			trackInfo.setEnd(returnObjectSecond.getJSONObject("end_point").getString("loc_time"));
				JSONArray tracks = returnObjectSecond.getJSONArray("points");
				Iterator<JSONObject> tmpIterator = tracks.iterator();
				while (tmpIterator.hasNext()) {
					JSONObject track = tmpIterator.next();
					CarNotice trackPoint = new CarNotice();
					trackPoint.setLatitude(track.getDouble("latitude"));
					trackPoint.setLongitude(track.getDouble("longitude"));
					trackPoint.setCar_speed(track.getInt("speed"));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
				    Date date = null;
					try {
						date = sdf.parse(DateUtils.getLong2Date(track.getLong("loc_time")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					trackPoint.setSenddate(sdf.format(date));
					trackPoint.setDirection(track.getDouble("direction"));
					if(track.containsKey("fuel")) {
						trackPoint.setFuel(track.getLong("fuel"));
					}
					if(track.containsKey("mileage")) {
						trackPoint.setMileage(track.getLong("mileage"));
					}
					trackPoints.add(trackPoint);
				}
	//			trackUnionInfo.put(trackInfo, trackPoints);
	//			backInfo.add(trackUnionInfo);
			}
		}
		return trackPoints;
	}
	
	/**
	 * @author CM
	 * @date 2017年5月26日
	 * @Description 获取驾驶行为数据
	 * @param device 硬件编号
	 * @param startTime 记录起始时间
	 * @param endTime 记录结束时间
	 * @return List<HashMap<Long,List<CarNotice>>>
	 * @throws 
	 */
	public static List<SpeedingInfo> GetDriverBehavior(String device,long startTime,long endTime) {
		List<SpeedingInfo> driverBehaviors = new ArrayList<SpeedingInfo>();
		String getDriverBehaviorURL = "http://yingyan.baidu.com/api/v3/analysis/drivingbehavior";
		
		//详细参数详见http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/analysis#drivingbehavior
		String getDriverBehaviorParam = "&entity_name=" + device + "&start_time=" + startTime + "&end_time=" + endTime + "&process_option=need_mapmatch=1";
		
		//String getDriverBehaviorResult = "{status:0,message:\"成功\",distance:61744.609006319,duration:23568,average_speed:9.4314575875233,max_speed:54.9,start_point:{longitude:113.24471412903,latitude:23.405204706192,coord_type:3,loc_time:1464149553,address:\"马鞍山公园(地铁站)附近25米\"},end_point:{longitude:113.20774073303,latitude:23.406671233485,coord_type:3,loc_time:1464173121,address:\"新凤凰酒店东112米\"},speeding_num:2,harsh_acceleration_num:1,harsh_breaking_num:1,harsh_steering_num:3,speeding:[{speeding_distance:400,speeding_points:[{longitude:113.24100205065,latitude:23.412706344614,coord_type:3,loc_time:1464149867,actual_speed:21.6,limit_speed:15},{longitude:113.24198745703,latitude:23.412683720093,coord_type:3,loc_time:1464150340,actual_speed:18.9,limit_speed:15}]},{speeding_distance:600,speeding_points:[{longitude:113.2379268869,latitude:23.419198232806,coord_type:3,loc_time:1464150995,actual_speed:15.3,limit_speed:15},{longitude:113.23551855949,latitude:23.420734738893,coord_type:3,loc_time:1464151486,actual_speed:16.2,limit_speed:15}]}],harsh_acceleration:[{longitude:113.23505718408,latitude:23.415670302452,coord_type:3,loc_time:1464151621,acceleration:3.9347222222222,initial_speed:5.4,end_speed:33.73}],harsh_breaking:[{longitude:113.21355843102,latitude:23.410533082252,coord_type:3,loc_time:1464167499,acceleration:-3.75,initial_speed:13.5,end_speed:0}],harsh_steering:[{longitude:113.23536366944,latitude:23.415997495116,coord_type:3,loc_time:1464158361,centripetal_acceleration:42.081624991152,turn_type:\"left\",speed:19.8},{longitude:113.24036159664,latitude:23.415169028634,coord_type:3,loc_time:1464158512,centripetal_acceleration:19.256219588206,turn_type:\"left\",speed:19.8},{longitude:113.22883765361,latitude:23.409799328344,coord_type:3,loc_time:1464159414,centripetal_acceleration:5.2597602426213,turn_type:\"left\",speed:25.2}]}";

		String getDriverBehaviorResult = HttpUtils.methodGet(getDriverBehaviorURL, BAIDUMAP_BASIC_PARAM + getDriverBehaviorParam);
		JSONObject returnObject = JSONObject.fromObject(getDriverBehaviorResult);
		int returnStatus = returnObject.getInt("status");
		if(returnStatus == 0){
			int speedingSize = returnObject.getInt("speeding_num");
			if(speedingSize > 0){
				JSONArray speedingTimes = returnObject.getJSONArray("speeding");
				Iterator<JSONObject> tmpIterator = speedingTimes.iterator();
				while (tmpIterator.hasNext()) {
					SpeedingInfo driverBehavior = new SpeedingInfo();
					HashMap<Long,List<CarNotice>> tempUnionInfo = new HashMap<Long,List<CarNotice>>();
					List<CarNotice> speedPoints = new ArrayList<CarNotice>();
					JSONObject speedingTime = tmpIterator.next();
					driverBehavior.setSpeeding_distance(speedingTime.getDouble("speeding_distance"));
					JSONArray speedingPoints = speedingTime.getJSONArray("speeding_points");
					Iterator<JSONObject> tmpIteratorSecond = speedingPoints.iterator();
					int k = 0;
					while (tmpIteratorSecond.hasNext()) {
						JSONObject speedingPoint = tmpIteratorSecond.next();
						if(k == 0)
						{
							driverBehavior.setStart_time(speedingPoint.getLong("loc_time"));
							driverBehavior.setStart_latitude(speedingPoint.getDouble("latitude"));
							driverBehavior.setStart_longitude(speedingPoint.getDouble("longitude"));
							driverBehavior.setCar_speed(speedingPoint.getInt("actual_speed"));
							driverBehavior.setLimit_speed(speedingPoint.getInt("limit_speed"));
						}
						else
						{
							driverBehavior.setEnd_time(speedingPoint.getLong("loc_time"));
							driverBehavior.setEnd_latitude(speedingPoint.getDouble("latitude"));
							driverBehavior.setEnd_longitude(speedingPoint.getDouble("longitude"));
							driverBehavior.setCar_speed(speedingPoint.getInt("actual_speed"));
							driverBehavior.setLimit_speed(speedingPoint.getInt("limit_speed"));
						}
						k++;
					}
					driverBehaviors.add(driverBehavior);
				}
			}
		}
		return driverBehaviors;
	}
	
	public static String GetCarStatus(String device){
		try {
			String getCarStatusURL = "http://yingyan.baidu.com/api/v3/entity/list";
			
			//设置检查前两分钟还有上传数据的车辆信息
			long activeTime = DateUtils.getTime(DateUtils.getDateTime())/1000 - 60*2;
			
			//详细参数详见http://lbsyun.baidu.com/index.php?title=yingyan/api/v3/analysis#drivingbehavior
			String getCarStatusParam = "&filter=entity_names:" + device + "|active_time:" + activeTime;
			String getCarStatusResult = HttpUtils.methodGet(getCarStatusURL, BAIDUMAP_BASIC_PARAM + getCarStatusParam);
			int isOK = getCarStatusResult.indexOf("\"status\":0");
			if(isOK > 0) return "1";
//			JSONObject returnObject = JSONObject.fromObject(getCarStatusResult);
//			int status = returnObject.getInt("status");
//			if(status == 0){
//				int total = returnObject.getInt("total");
//				if(total > 0)return "1";
//			}
			return "0";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0";
	}
	
	/**
	 * @author CM
	 * @date 2017年5月27日
	 * @Description 根据坐标获取地址
	 * @param latiude 纬度
	 * @param longitude 经度
	 * @return String
	 * @throws 
	 */
	public static String GetAddressByCoordinate(Double latiude,Double longitude,String ak){
		String getAddressURL = "http://api.map.baidu.com/geocoder/v2/";
		
		//详细参数详见http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
		String getAddressParam = "?ak=" + ak + "&output=json&location=" + latiude + "," + longitude;

		String returnResult = HttpUtils.methodGet(getAddressURL, getAddressParam);
		JSONObject returnObject = JSONObject.fromObject(returnResult);
		int returnStatus = returnObject.getInt("status");
		if(returnStatus == 0){
			JSONObject resultObject = returnObject.getJSONObject("result");
			return resultObject.getString("formatted_address");
		}
		return "";
	}
	
		/**
	 * 
	 * @Function: HttpUtils::createCircleFence
	 * @Description: 创建百度圆形电子围栏
	 * @param fence_name  围栏名称
	 * @param device_no   监控对象，设备编码
	 * @param longitude   圆心坐标经度
	 * @param latitude    圆心坐标纬度
	 * @param radius      圆半径
	 * @return 创建成功返回围栏id 否则返回null
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 下午9:23:56 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	public static Long createCircleFence(String ak ,String service_id ,String fence_name ,String device_no ,String longitude, String latitude, Double radius){
		
		NameValuePair[] meta_list = new NameValuePair[8];
		meta_list[0] = new NameValuePair("ak=", ak);
		meta_list[1] = new NameValuePair("service_id", service_id);
		meta_list[2] = new NameValuePair("fence_name", fence_name);
		meta_list[3] = new NameValuePair("monitored_person",device_no);
		meta_list[4] = new NameValuePair("longitude", longitude);
		meta_list[5] = new NameValuePair("latitude", latitude);
		meta_list[6] = new NameValuePair("radius", String.valueOf(radius));
		meta_list[7] = new NameValuePair("coord_type", "bd09ll");
		
		/*String param = "ak=" + ak 
		             + "/&service_id=" + service_id
		             + "/&fence_name=" + fence_name
		             + "/&monitored_person=" + device_no
		             + "/&longitude=" + longitude
		             + "/&latitude=" + latitude
		             + "/&radius=" + radius
		             + "/&coord_type=bd09ll";*/
	    String ccfResult = HttpUtils.methodPost(CCF_URL, meta_list);
	    logger.info("百度圆形围栏生成请求反馈信息："+ccfResult);
	    JSONObject jsonObject = JSONObject.fromObject(ccfResult);
	    if(jsonObject.containsKey("status") && "0".equals(jsonObject.getString("status"))){
	    	return jsonObject.getLong("fence_id");
	    }
		return null;
	}
	
	
	/**
	 * 
	 * @Function: HttpUtils::createPolygonFence
	 * @Description: 创建百度多边形电子围栏
	 * @param fence_name 围栏名称
	 * @param device_no  监控对象，设备编号
	 * @param vertexes 多边形围栏形状点     形式如“double,double; double,double; ...;double,double” 经纬度顺序为：纬度,经度；
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 下午9:33:57 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	public static Long createPolygonFence(String ak ,String service_id ,String fence_name ,String device_no ,String vertexes){
		/*String param = "ak=" + ak 
		             + "/&service_id=" + service_id
		             + "/&fence_name=" + fence_name
		             + "/&monitored_person=" + device_no
		             + "/&vertexes=" + vertexes
		             + "/&coord_type=bd09ll";*/
		
		NameValuePair[] meta_list = new NameValuePair[6];
		meta_list[0] = new NameValuePair("ak", ak);
		meta_list[1] = new NameValuePair("service_id", service_id);
		meta_list[2] = new NameValuePair("fence_name", fence_name);
		meta_list[3] = new NameValuePair("monitored_person",device_no);
		meta_list[4] = new NameValuePair("vertexes", vertexes);
		meta_list[5] = new NameValuePair("coord_type", "bd09ll");
	    String ccfResult = HttpUtils.methodPost(CPF_URL, meta_list);
	    logger.info("百度多边形围栏生成请求反馈信息："+ccfResult);
	    JSONObject jsonObject = JSONObject.fromObject(ccfResult);
	    if(jsonObject.containsKey("status") && "0".equals(jsonObject.getString("status"))){
	    	return jsonObject.getLong("fence_id");
	    }
		return null;
	}
	
	/**
	 * 
	 * @Function: BaiduMapUtils::deleteFence
	 * @Description: 百度地图电子栅栏删除
	 * @param ak 百度ak
	 * @param service_id  百度 service_id
	 * @param device_no 监控对象，设备编号
	 * @param fence_id 百度栅栏id 这里只能是一个id  不能多个
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年6月1日 下午3:40:46 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年6月1日     Banji Uoocent          v1.1.0               修改原因
	 */
	public static String deleteFence(String ak ,String service_id ,String device_no, Long fence_id){
    	String fence_ids = null;
		/*String param = "ak=" + ak 
		             + "/&service_id=" + service_id
		             + "/&monitored_person=" + device_no
		             + "/&fence_ids=" + fence_id;*/

		NameValuePair[] meta_list = new NameValuePair[4];
		meta_list[0] = new NameValuePair("ak", ak);
		meta_list[1] = new NameValuePair("service_id", service_id);
		meta_list[2] = new NameValuePair("monitored_person", device_no);
		meta_list[3] = new NameValuePair("fence_ids", String.valueOf(fence_id));
	    String ccfResult = HttpUtils.methodPost(DEL_URL,meta_list);
	    logger.info("百度围栏删除请求反馈信息："+ccfResult);
	    JSONObject jsonObject = JSONObject.fromObject(ccfResult);
	    if(jsonObject.containsKey("status") && "0".equals(jsonObject.getString("status"))){
	    	if(jsonObject.containsKey("fence_ids")){
	    		fence_ids = jsonObject.getString("fence_ids");
	    	}
	    }
		return fence_ids;
	}
}
