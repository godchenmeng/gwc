package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Member;
import com.youxing.car.service.base.BaseService;
public interface DriverService extends BaseService<Driver>{

	List<Driver> pageDriver(Map<String,Object> map);
	
	void add(Driver driver, Member me);

	void modify(Driver driver, Member me);
	
	void modifyWebService(Driver driver);

	List<String> listDr();
	
	void updateTaskStatusService(Driver d);
	
	List<Driver> pageByWebService(Map<String,Object> map);
	
	int countByWeb(Map<String,Object> map);
	
	void modifyDriver(Driver driver);
	
	int pageByWebCount(Map<String,Object> map);
	
	public void updateTaskStatus(Driver driver);
}
