package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Control;
public interface ControlDao extends BaseDao<Control>{
	List<Control> pageByundo(Map<String, Object> map);
	
	List<String> listByControl(Map<String,Object> map);
	
	List<Control> listApply(Map<String,Object> map);
	
	List<Control> pageAllControl(Map<String,Object> map);
	
	//web
	List<String> listByDriver(Map<String,Object> map);
	
	List<Control> listByWeb(Map<String,Object> map);

	List<Map<String,Object>> getDriverByDevice(Map<String,Object> map);
	
	void removeApply(Map<String,Object> map);
	
	Control findOneByMap(Map<?, ?> map);// 8.根据条件查询最新一条任务数据
}
