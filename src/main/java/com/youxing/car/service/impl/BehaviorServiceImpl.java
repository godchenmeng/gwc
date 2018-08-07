package com.youxing.car.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.BehaviorDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Behavior;
import com.youxing.car.entity.CountEntity;
import com.youxing.car.entity.MileageAndFuel;
import com.youxing.car.entity.OrgCount;
import com.youxing.car.service.BehaviorService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class BehaviorServiceImpl extends BaseServiceImpl<Behavior> implements BehaviorService{
	@Resource
	private BehaviorDao behaviorDao;

	@Override
	public BaseDao<Behavior> getBaseDao() {
		return this.behaviorDao;
	}

	@Override
	public List<Behavior> findListByDeviceDay(Map<String, Object> map) {
		return behaviorDao.findListByDeviceDay(map);
	}

	@Override
	public List<MileageAndFuel> findListForCarMilAndFuel(Map<String, Object> map) {
		return behaviorDao.findListForCarMilAndFuel(map);
	}

	@Override
	public List<CountEntity> findListForReduceDay(Map<String, Object> map) {
		return behaviorDao.findListForReduceDay(map);
	}

	@Override
	public List<OrgCount> findListForReduceMonth(Map<String, Object> map) {
		return behaviorDao.findListForReduceMonth(map);
	}

	@Override
	public List<OrgCount> findListForReduceDevice(Map<String, Object> map) {
		return behaviorDao.findListForReduceDevice(map);
	}
	
	
	/**
	 * web
	 */
	
	@Override
	public List<Behavior> findListByDeviceDayWeb(Map<String, Object> map) {
		return behaviorDao.findListByDeviceDayWeb(map);
	}

	@Override
	public List<CountEntity> findListForCarMilAndFuelWeb(Map<String, Object> map) {
		return behaviorDao.findListForCarMilAndFuelWeb(map);
	}

	@Override
	public List<CountEntity> findListForReduceDayWeb(Map<String, Object> map) {
		return behaviorDao.findListForReduceDayWeb(map);
	}

	@Override
	public List<CountEntity> findListForReduceMonthWeb(Map<String, Object> map) {
		return behaviorDao.findListForReduceMonthWeb(map);
	}

	@Override
	public List<CountEntity> findListForReduceDeviceWeb(Map<String, Object> map) {
		return behaviorDao.findListForReduceDeviceWeb(map);
	}

	@Override
	public List<Behavior> lineRate(Map<String, Object> map) {
		return behaviorDao.lineRate(map);
	}

	@Override
	public int lineRateCount(Map<String, Object> map) {
		return behaviorDao.lineRateCount(map);
	}
	
	@Override
	public List<HashMap<Object,String>> findTableInfo(Map<String, Object> map) {
		return behaviorDao.findTableInfo(map);
	}

	@Override
	public int findTableInfoNum(Map<String, Object> map) {
		return behaviorDao.findTableInfoNum(map);
	}

}
