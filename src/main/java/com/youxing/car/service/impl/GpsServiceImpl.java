package com.youxing.car.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.GpsDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Gps;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class GpsServiceImpl extends BaseServiceImpl<Gps> implements GpsService {

	@Resource
	private GpsDao gpsDao;
	@Override
	public List<Gps> findListByDevice(Map<String, Object> map) {
		return gpsDao.findListByDevice(map);
	}

	@Override
	public BaseDao<Gps> getBaseDao() {
		return this.gpsDao;
	}

	@Override
	public List<Gps> findListForCarCurrentLine(Map<String, Object> map) {
		return gpsDao.findListForCarCurrentLine(map);
	}
	@Override
	public List<CarMessage> findGpsCurrentByDevice(Map<String, Object> map) {
		return gpsDao.findGpsCurrentByDevice(map);
	}
	
	
	/**
	 * =============web
	 */
	@Override
	public List<CarMessage> findListForCarMessage(Map<String, Object> map) {
		return gpsDao.findListForCarMessage(map);
	}
	
	@Override
	public List<Gps> findListForCarLineWeb(Map<String, Object> map) {
		return gpsDao.findListForCarLineWeb(map);
	}

	@Override
	public List<Gps> getCarStatusService(Map<String, Object> map) {
		return gpsDao.getCarStatus(map);
	}

	@Override
	public List<HashMap<String, Object>> getDeviceByThree(Map<String, Object> map) {
		return gpsDao.getDeviceByThree(map);
	}


	@Override
	public int getDeviceByThreeNum(Map<String, Object> map) {
		return gpsDao.getDeviceByThreeNum(map);
	}


	@Override
	public List<HashMap<String, Object>> getDeviceByThreeEx(Map<String, Object> map) {
		return gpsDao.getDeviceByThreeEx(map);
	}


	@Override
	public List<HashMap<String, Object>> getDeviceBySeven(Map<String, Object> map) {
		return gpsDao.getDeviceBySeven(map);
	}


	@Override
	public int getDeviceBySevenNum(Map<String, Object> map) {
		return gpsDao.getDeviceBySevenNum(map);
	}


	@Override
	public List<HashMap<String, Object>> getDeviceBySevenEx(Map<String, Object> map) {
		return gpsDao.getDeviceBySevenEx(map);
	}


	@Override
	public List<HashMap<String, Object>> getDeviceByNever(Map<String, Object> map) {
		return gpsDao.getDeviceByNever(map);
	}


	@Override
	public int getDeviceByNeverNum(Map<String, Object> map) {
		return gpsDao.getDeviceByNeverNum(map);
	}


	@Override
	public List<HashMap<String, Object>> getDeviceByNeverEx(Map<String, Object> map) {
		return gpsDao.getDeviceByNeverEx(map);
	}

	@Override
	public List<Gps> findListForTrack(Map<String, Object> map) {
		return gpsDao.findListForTrack(map);
	}

	@Override
	public Gps findNewGPSInfo(String device){
		return gpsDao.findNewGPSInfo(device);
	}
}
