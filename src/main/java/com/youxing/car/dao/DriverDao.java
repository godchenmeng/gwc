package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Driver;
public interface DriverDao extends BaseDao<Driver>{

	List<Driver> pageDriver(Map<String,Object> map);
	
	List<String> listDr();
	
	void updateTaskStatus(Driver d);
	
	List<Driver> pageByWeb(Map<String,Object> map);
	
	int countByWeb(Map<String,Object> map);
	
	void modifyWeb(Driver driver);
	
	
	int getDriverNum(Map<String,Object> map);
	
	
	
	void modifyDriver(Driver driver);
	int pageByWebCount(Map<String,Object> map);
}
