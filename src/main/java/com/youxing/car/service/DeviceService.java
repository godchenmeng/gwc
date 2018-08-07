package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Device;
import com.youxing.car.service.base.BaseService;
public interface DeviceService extends BaseService<Device>{
	
	//gy
	List<String> listOrgDevice(Map<String,Object> map);
	
	/**
	 * web
	 * @param mbs
	 * @param car_id
	 */
	//gy
	void modify(Device mbs, Long car_id);

	void add(Device device, Long ci);

	void modifyCar(Device entity);
	
	//gy
	List<String> listDevice(Map<String,Object> map);
	
	CarMessage listOrgAndCar(Map<String,Object> map);
	
	List<String>listDeviceByCar(Map<String,Object> map);
	
	//gy
	List<Device> pageByWebService(Map<String,Object> map);
	//gy
	int countByWeb(Map<String,Object> map);
	
	void updateDriverByDeviceService(Map<String,Object> map);
	
	List<Device> listDevicesByOrgService(Map<String,Object> map);
	
	List<Device> findListByMap(Map<String,Object> map);

	List<Device> sysDeviceInfo();

	List<Device> unknowDeviceInfo();
	void removeByIdAndCarId(Long id,Long carId);
	
	void removeById(Long id,Long carId);
}
