package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Device;
public interface DeviceDao extends BaseDao<Device>{
	
	List<String> listOrgDevice(Map<String,Object> map);
	
	
	/**
	 * web
	 * @param map
	 * @return
	 */
    List<String> listDevice(Map<String,Object> map);
	
	
	CarMessage listOrgAndCar(Map<String,Object> map);
	
	List<String>listDeviceByCar(Map<String,Object> map);
	
	List<Device> pageByWeb(Map<String,Object> map);
	
	int countByWeb(Map<String,Object> map);
	
	void updateDriverByDevice(Map<String,Object> map);
	
	List<Device> listDevicesByOrg(Map<String,Object> map);
	
	List<Device> findListByMap(Map<String,Object> map);

	List<Device> sysDeviceInfo();

	List<Device> unknowDeviceInfo();
}
