package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Control;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Evaluate;
import com.youxing.car.entity.Task;
import com.youxing.car.service.base.BaseService;
public interface ControlService extends BaseService<Control>{

	void modify(Control control, Task task,Evaluate eva);
	List<Control> pageByundo(Map<String, Object> map);

	List<String> listByControl(Map<String,Object> map);

	List<Control> listApply(Map<String,Object> map);
	
	List<Control> pageAllControl(Map<String,Object> map);
	void modify(Control ct, Driver driver);
	
	//gy
	List<String> listByDriver(Map<String,Object> map);
	List<Control> listByWebService(Map<String,Object> map);
	//gy
	List<Map<String,Object>> getDriverByDevice(Map<String,Object> map);
	
	void removeApply(Map<String,Object> map);
	
	Control findOneByMap(Map<?, ?> map);// 8.根据条件查询最新一条任务数据
}
