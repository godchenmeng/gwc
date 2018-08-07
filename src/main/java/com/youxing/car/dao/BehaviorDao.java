package com.youxing.car.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Behavior;
import com.youxing.car.entity.CountEntity;
import com.youxing.car.entity.MileageAndFuel;
import com.youxing.car.entity.OrgCount;

public interface BehaviorDao extends BaseDao<Behavior>{
	
	List<Behavior> findListByDeviceDay(Map<String,Object> map);

	List<MileageAndFuel> findListForCarMilAndFuel(Map<String,Object> map);
	
	List<CountEntity> findListForReduceDay(Map<String,Object> map);
//	List<CountEntity> findListForReduceMonth1(Map<String,Object> map);
	
	List<OrgCount> findListForReduceMonth(Map<String,Object> map);
	
	List<OrgCount> findListForReduceDevice(Map<String,Object> map);
//	List<CountEntity> findListForReduceDevice1(Map<String,Object> map);
	
	/**
	 * ==================web
	 */
	
	List<Behavior> findListByDeviceDayWeb(Map<String,Object> map);

	List<CountEntity> findListForCarMilAndFuelWeb(Map<String,Object> map);
	
	List<CountEntity> findListForReduceDayWeb(Map<String,Object> map);
	
	List<CountEntity> findListForReduceMonthWeb(Map<String,Object> map);
	
	//gy
	List<CountEntity> findListForReduceDeviceWeb(Map<String,Object> map);
	
	List<Behavior> lineRate(Map<String,Object> map);
	
	int lineRateCount(Map<String,Object> map);
	
	List<HashMap<Object,String>> findTableInfo(Map<String,Object> map);
	
	int findTableInfoNum(Map<String,Object> map);
	
}
