package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.CarDao;
import com.youxing.car.dao.DeviceDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Device;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class DeviceServiceImpl extends BaseServiceImpl<Device> implements DeviceService{
	@Resource
	private DeviceDao deviceDao;	
	public BaseDao<Device> getBaseDao() {
		return this.deviceDao;
	}
	
	@Resource
	private CarDao carDao;
	@Override
	public List<String> listOrgDevice(Map<String, Object> map) {
		return deviceDao.listOrgDevice(map);
	}
	
	
	/**
	 * web
	 */
	

	@Override
	public void modify(Device mbs, Long car_id) {
		deviceDao.modify(mbs);
		Car car = carDao.findById(car_id);
		if (car != null) {
			Car cr = new Car();
			cr.setId(car_id);
			cr.setObd(Constant.UNBIND_OBD);
			cr.setStatus(Constant.REMOVE_STATUS);
			carDao.modify(cr);
		}

	}

	
	public void modifyCar(Device entity) {
		Long l = entity.getCar_id();
		if (l != null) {
			Car car = carDao.findById(l);
			if (car != null) {
				Car cr = new Car();
				cr.setId(l);
				cr.setObd(Constant.UNBIND_OBD);
				carDao.modify(cr);
			}

		}
		super.modify(entity);
	}

	@Override
	public void add(Device device, Long ci) {
		deviceDao.add(device);
		Car car = carDao.findById(ci);
		if (car != null) {
			Car cr = new Car();
			cr.setId(ci);
			cr.setObd(Constant.BIND_OBD);
			carDao.modify(cr);
		}

	}

	@Override
	public List<String> listDevice(Map<String, Object> map) {
		return deviceDao.listDevice(map);
	}


	@Override
	public CarMessage listOrgAndCar(Map<String, Object> map) {
		return deviceDao.listOrgAndCar(map);
	}

	@Override
	public List<String> listDeviceByCar(Map<String, Object> map) {
		return deviceDao.listDeviceByCar(map);
	}


	@Override
	public List<Device> pageByWebService(Map<String, Object> map) {
		return deviceDao.pageByWeb(map);
	}


	@Override
	public int countByWeb(Map<String, Object> map) {
		return deviceDao.countByWeb(map);
	}


	@Override
	public void updateDriverByDeviceService(Map<String,Object> map) {
		deviceDao.updateDriverByDevice(map);
	}


	@Override
	public List<Device> listDevicesByOrgService(Map<String,Object> map) {
		return deviceDao.listDevicesByOrg(map);
	}


	@Override
	public List<Device> findListByMap(Map<String, Object> map) {
		return deviceDao.findListByMap(map);
	}

	@Override
	public List<Device> sysDeviceInfo(){ return deviceDao.sysDeviceInfo(); }

	@Override
	public List<Device> unknowDeviceInfo(){ return deviceDao.unknowDeviceInfo(); }
	@Override
	public void removeByIdAndCarId(Long id, Long carId) {
		deviceDao.removeById(id);
		Car car = carDao.findById(carId);
		if (car != null) {
			carDao.removeById(carId);
		}
	}


	@Override
	public void removeById(Long id, Long carId) {
		deviceDao.removeById(id);
		Car car = carDao.findById(carId);
		if (car != null) {
			Car cr = new Car();
			cr.setId(carId);
			cr.setObd(Constant.UNBIND_OBD);
			carDao.modify(cr);
		}
	}
}
