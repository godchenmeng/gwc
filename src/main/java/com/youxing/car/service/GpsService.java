package com.youxing.car.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Gps;
import com.youxing.car.service.base.BaseService;

public interface GpsService extends BaseService<Gps>{
	public List<Gps> findListByDevice(Map<String,Object> map);
	public List<Gps> findListForCarCurrentLine(Map<String,Object> map);
	//gy
	List<CarMessage> findGpsCurrentByDevice(Map<String,Object> map);
	
	
	/**
	 * web
	 * @param map
	 * @return
	 */
	public List<CarMessage> findListForCarMessage(Map<String,Object> map);
	
	public List<Gps> findListForCarLineWeb(Map<String,Object> map);
	
	List<Gps> getCarStatusService(Map<String,Object> map);
	
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
