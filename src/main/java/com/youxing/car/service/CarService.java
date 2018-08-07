package com.youxing.car.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.youxing.car.entity.Car;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.base.BaseService;
public interface CarService extends BaseService<Car>{

		List<Car> pageCar(Map<String,Object> map);

		HashMap<String, Object> countCarService(Map<String,Object> map);

		//gy
		List<Car> listByNameService(Map<String,Object> map);


		/*
		* web
		*/

		List<Selects> listByOrg(Map<String,Object> map);

		List<Car> listByDriverNoOrVin(Map<String, Object> map);

		List<Selects> listOrg(Map<String,Object> map);

		List<Car> listCarAndDeviceBy(Map<String,Object> map);

		JSONArray listCarAndDriverByStatus(String status);

		List<Car> pageByWebService(Map<String,Object> map);

		int countByWeb(Map<String,Object> map);



		List<Car> getInsuranceOut();

		List<Car> getInspectionOut();

		List<Car> getCarIds(Map<String,Object> map);


		List<Car> getCarListByOrg(Map<String,Object> map);

		List<Object> getCDCode(Map<String, Object> map);

		void modifyCarAndDevice(Car car, Long id);

		List<Map<String, Object>> findCarByDevice(Map<String, Object> map);

}
