package com.youxing.car.service;

import java.util.List;
import java.util.Map;

import com.youxing.car.entity.CarNotice;
import com.youxing.car.service.base.BaseService;

public interface CarNoticeService extends BaseService<CarNotice>{
	

	
	public CarNotice findOneByDeviceAsc(Map<String,Object> map);
	
	public CarNotice findOneByDeviceDesc(Map<String,Object> map);
	
	
	//gy
	public List<CarNotice> findListForHisDockInfo(Map<String,Object> map);
	
	List<CarNotice> findListDayInfoService(Map<String,Object> map);
	
	
	//违规停运
	List<CarNotice> illegalStopCarByorgService(Map<String,Object> map);
	//车辆为入库
	List<CarNotice> carNoStorage(Map<String,Object> map);
	//查询车辆最新一条点火数据
	CarNotice findBydevice(Map<String,Object> map);
	
}
