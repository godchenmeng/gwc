package com.youxing.car.dao;

import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarNotice;

public interface CarNoticeDao extends BaseDao<CarNotice>{

	
	CarNotice findOneByDeviceAsc(Map<String,Object> map);
	
	CarNotice findOneByDeviceDesc(Map<String,Object> map);
	
	List<CarNotice> findListForHisDockInfo(Map<String,Object> map);
	
	List<CarNotice> findListDayInfo(Map<String,Object> map);
	
	

	//违规停运
	List<CarNotice> illegalStopCarByorg(Map<String,Object> map);
	
	//车辆为入库
	List<CarNotice> carNoStorage(Map<String,Object> map);
	
	//查询车辆最新一条点火数据
	CarNotice findBydevice(Map<String,Object> map);
	
	
}
