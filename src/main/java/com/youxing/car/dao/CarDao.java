package com.youxing.car.dao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Selects;
public interface CarDao extends BaseDao<Car>{
	
	List<Car> pageCar(Map<String,Object> map);
	
	List<HashMap<String,Object>> countCar(Map<String,Object> map);
	
	List<Car> listByName(Map<String,Object> map);
	
	/*
	 * web
	 */
	
    List<Selects> listByOrg(Map<String,Object> map);
    List<Selects> listOrg(Map<String,Object> map);
    List<Car> listCarAndDeviceBy(Map<String,Object> map);
    List<Map<String,Object>> listCarAndDriverByStatus(String status);
   
    List<Car> pageByWeb(Map<String,Object> map);
   
    int countByWeb(Map<String,Object> map);
   
    void insertBatch(List<Car> list); 
   
   
    List<Car> getCarListByOrg(Map<String,Object> map);
   
    List<Car> getInsuranceOut();

    List<Car> getInspectionOut();
   
    List<Car> getCarIds(Map<String,Object> map);
 
    List<Car> listByDriverNoOrVin(Map<String, Object> map);
    
    List<Object> getCDCode(Map<String, Object> map);
	   
    List<Map<String, Object>> findCarByDevice(Map<String, Object> map);
}
