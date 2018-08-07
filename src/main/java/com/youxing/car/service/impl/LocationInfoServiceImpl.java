package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.entity.CarNotice;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.LocationInfoDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.LocationInfo;
import com.youxing.car.service.LocationInfoService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class LocationInfoServiceImpl extends BaseServiceImpl<LocationInfo> implements LocationInfoService{
	@Resource
	private LocationInfoDao locationInfoDao;

	@Override
	public List<LocationInfo> pageByLocationInfo(Map<String, Object> map) {
		return locationInfoDao.pageByLocationInfo(map);
	}

	@Override
	public List<CarNotice> pageByLocateInfo(Map<String, Object> map) {
		return locationInfoDao.pageByLocateInfo(map);
	}

	@Override
	public int countByLocationInfo(Map<String, Object> map) {
		return locationInfoDao.countByLocationInfo(map);
	}

	@Override
	public int countByLocateInfo(Map<String, Object> map) {
		return locationInfoDao.countByLocateInfo(map);
	}

	@Override
	public BaseDao<LocationInfo> getBaseDao() {
		return this.locationInfoDao;
	}

	@Override
	public List<CarNotice> getNoticeExcel(Map<String, Object> map) {
		return locationInfoDao.getNoticeExcel(map);
	}
	

}
