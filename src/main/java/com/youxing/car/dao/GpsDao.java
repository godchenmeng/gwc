package com.youxing.car.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Gps;

public interface GpsDao extends BaseDao<Gps>{
	
	//gy
	List<Gps> findListByDevice(Map<String,Object> map);
//gy
	List<Gps> findListForCarCurrentLine(Map<String,Object> map);
	List<CarMessage> findGpsCurrentByDevice(Map<String,Object> map);
	
	/**
	 * web
	 * 
	 */
	List<CarMessage> findListForCarMessage(Map<String,Object> map);
	
	List<Gps> findListForCarLineWeb(Map<String,Object> map);
	
	List<Gps> getCarStatus(Map<String,Object> map);
	
	//3日，7日，一直无数据查询，导出
	List<HashMap<String,Object>> getDeviceByThree(Map<String,Object> map);
	int getDeviceByThreeNum(Map<String,Object> map);
	List<HashMap<String,Object>> getDeviceByThreeEx(Map<String,Object> map);
	
	List<HashMap<String,Object>> getDeviceBySeven(Map<String,Object> map);
	int getDeviceBySevenNum(Map<String,Object> map);
	List<HashMap<String,Object>> getDeviceBySevenEx(Map<String,Object> map);
	
	List<HashMap<String,Object>> getDeviceByNever(Map<String,Object> map);
	int getDeviceByNeverNum(Map<String,Object> map);
	List<HashMap<String,Object>> getDeviceByNeverEx(Map<String,Object> map);
	List<Gps> findListForTrack(Map<String,Object> map);

	Gps findNewGPSInfo(String device);
}
