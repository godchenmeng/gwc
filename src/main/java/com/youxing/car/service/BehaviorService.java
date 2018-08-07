package com.youxing.car.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Behavior;
import com.youxing.car.entity.CountEntity;
import com.youxing.car.entity.MileageAndFuel;
import com.youxing.car.entity.OrgCount;
import com.youxing.car.service.base.BaseService;
public interface BehaviorService extends BaseService<Behavior>{
	//gy
	public List<Behavior> findListByDeviceDay(Map<String,Object> map);
	//gy
	public List<MileageAndFuel> findListForCarMilAndFuel(Map<String,Object> map);
	
	public List<CountEntity> findListForReduceDay(Map<String,Object> map);
	//gy
	public List<OrgCount> findListForReduceMonth(Map<String,Object> map);
	//gy
	public List<OrgCount> findListForReduceDevice(Map<String,Object> map);
	
	/**
	 * ==========================web
	 */
	
   public List<Behavior> findListByDeviceDayWeb(Map<String,Object> map);
	
   
   //gy
	public List<CountEntity> findListForCarMilAndFuelWeb(Map<String,Object> map);
	
	public List<CountEntity> findListForReduceDayWeb(Map<String,Object> map);
	
	public List<CountEntity> findListForReduceMonthWeb(Map<String,Object> map);
	
	public List<CountEntity> findListForReduceDeviceWeb(Map<String,Object> map);
	
	List<Behavior> lineRate(Map<String,Object> map);
	
	int lineRateCount(Map<String,Object> map);
	
	List<HashMap<Object,String>> findTableInfo(Map<String,Object> map);
	
	int findTableInfoNum(Map<String,Object> map);
	
	
}
