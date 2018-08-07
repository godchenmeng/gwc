package com.youxing.car.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.CarNoticeDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarNotice;
import com.youxing.car.service.CarNoticeService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class CarNoticeServiceImpl extends BaseServiceImpl<CarNotice> implements CarNoticeService{

	@Resource
	private CarNoticeDao carNoticeDao;
	


	@Override
	public BaseDao<CarNotice> getBaseDao() {
		return this.carNoticeDao;
	}

	@Override
	public CarNotice findOneByDeviceAsc(Map<String, Object> map) {
		return carNoticeDao.findOneByDeviceAsc(map);
	}

	@Override
	public CarNotice findOneByDeviceDesc(Map<String, Object> map) {
		return carNoticeDao.findOneByDeviceDesc(map);
	}

	@Override
	public List<CarNotice> findListForHisDockInfo(Map<String, Object> map) {
		return carNoticeDao.findListForHisDockInfo(map);
	}

	@Override
	public List<CarNotice> findListDayInfoService(Map<String, Object> map) {
		return carNoticeDao.findListDayInfo(map);
	}

	@Override
	public List<CarNotice> illegalStopCarByorgService(Map<String, Object> map) {
		return carNoticeDao.illegalStopCarByorg(map);
	}

	@Override
	public List<CarNotice> carNoStorage(Map<String, Object> map) {
		return carNoticeDao.carNoStorage(map);
	}

	@Override
	public CarNotice findBydevice(Map<String, Object> map) {
		return carNoticeDao.findBydevice(map);
	}



}
