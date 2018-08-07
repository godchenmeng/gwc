package com.youxing.car.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.CarDao;
import com.youxing.car.dao.DeviceDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.CarService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class CarServiceImpl extends BaseServiceImpl<Car> implements CarService{
	@Resource
	private CarDao carDao;	
	@Resource
	private DeviceDao deviceDao;	
	public BaseDao<Car> getBaseDao() {
		return this.carDao;
	}
	@Override
	public List<Car> pageCar(Map<String, Object> map) {
		return carDao.pageCar(map);
	}
	@Override
	public HashMap<String, Object> countCarService(Map<String,Object> map) {
/*		HashMap<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = carDao.countCar(map);
		Integer offline_car = 0;
		Integer online_car = 0;
		for (HashMap<String, Object> hashMap : list) {
			String acc = hashMap.get("acc").toString();
			if (acc.equals("0")) {
				offline_car = Integer.parseInt(hashMap.get("number").toString());
			} else if (acc.equals("1") || acc.equals("2")) {
				online_car += Integer.parseInt(hashMap.get("number").toString());
			}
		}
		result.put("allCount", online_car + offline_car);
		result.put("useCount", list.get(0).get("useCount"));*/
		List<HashMap<String, Object>> list = carDao.countCar(map);
		HashMap<String, Object> result = list.get(0);
		return result;
	}
	@Override
	public List<Car> listByNameService(Map<String, Object> map) {
		return carDao.listByName(map);
	}
	
	
	/*
	 * web
	 */
	
	public List<Selects> listByOrg(Map<String,Object> map) {
		return carDao.listByOrg(map);
	}
	
	@Override
	public List<Car> listByDriverNoOrVin(Map<String, Object> map) {
		return carDao.listByDriverNoOrVin(map);
	}
	
	@Override
	public List<Car> listCarAndDeviceBy(Map<String,Object> map) {
		return carDao.listCarAndDeviceBy(map);
	}
	@Override
	public JSONArray listCarAndDriverByStatus(String status) {
		List<Map<String,Object>> mapList = carDao.listCarAndDriverByStatus(status);
		JSONArray jsonList = JSONArray.fromObject(mapList);
		return jsonList;
	}
	@Override
	public List<Selects> listOrg(Map<String, Object> map) {
		return carDao.listOrg(map);
	}
	@Override
	public List<Car> pageByWebService(Map<String, Object> map) {
		return carDao.pageByWeb(map);
	}
	@Override
	public int countByWeb(Map<String, Object> map) {
		return carDao.countByWeb(map);
	}
	
	
	
	@Override
	public List<Car> getInsuranceOut() {
		return carDao.getInsuranceOut();
	}

	@Override
	public List<Car> getInspectionOut() {
		return carDao.getInspectionOut();
	}
	@Override
	public List<Car> getCarIds(Map<String, Object> map) {
		return carDao.getCarIds(map);
	}
	@Override
	public List<Car> getCarListByOrg(Map<String, Object> map) {
		return carDao.getCarListByOrg(map);
	}
	@Override
	public List<Object> getCDCode(Map<String, Object> map) {
		return carDao.getCDCode(map);
	}
	
	@Override
	public void modifyCarAndDevice(Car car, Long id) {
		carDao.modify(car);
		Device device = new Device();
		device.setCar_id(id);
		device = deviceDao.findBy(device);
		if(device != null){
			device.setStatus(Constant.REMOVE_STATUS);
			deviceDao.modify(device);
		}
	}

	@Override
	public List<Map<String, Object>> findCarByDevice(Map<String, Object> map){
		return carDao.findCarByDevice(map);
	}
}
