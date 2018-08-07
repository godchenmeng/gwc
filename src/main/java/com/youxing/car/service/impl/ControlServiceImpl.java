package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.ControlDao;
import com.youxing.car.dao.DriverDao;
import com.youxing.car.dao.EvaluateDao;
import com.youxing.car.dao.TaskDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Control;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Evaluate;
import com.youxing.car.entity.Task;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class ControlServiceImpl extends BaseServiceImpl<Control> implements ControlService{
	@Resource
	private ControlDao controlDao;
	@Resource
	private TaskDao taskDao;
	@Resource
	private DriverDao driverDao;
	@Resource
	private EvaluateDao evaluateDao;
	public BaseDao<Control> getBaseDao() {
		return this.controlDao;
	}
	public void modify(Control control, Task task,Evaluate eva) {
		controlDao.modify(control);
		taskDao.modify(task);
		if(null!=eva){			
			evaluateDao.add(eva);
		}
	}
	@Override
	public List<Control> pageByundo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return controlDao.pageByundo(map);
	}
	@Override
	public List<String> listByControl(Map<String, Object> map) {
		return controlDao.listByControl(map);
	}
	@Override
	public List<Control> listApply(Map<String, Object> map) {
		return controlDao.listApply(map);
	}
	@Override
	public List<Control> pageAllControl(Map<String, Object> map) {
		return controlDao.pageAllControl(map);
	}
	@Override
	public void modify(Control ct, Driver driver) {
		controlDao.modify(ct);
		Driver dr = new Driver();
		dr.setId(driver.getId());
		Integer plan = driver.getPlan();
		if(plan!=null&&plan>0){
			dr.setPlan(plan-1);
		}else{
			dr.setPlan(0);
		}
		driverDao.modify(dr);
	}
	
	@Override
	public List<String> listByDriver(Map<String, Object> map) {
		return controlDao.listByDriver(map);
	}
	@Override
	public List<Control> listByWebService(Map<String, Object> map) {
		return controlDao.listByWeb(map);
	}

	@Override
	public List<Map<String,Object>> getDriverByDevice(Map<String,Object> map){
		return  controlDao.getDriverByDevice(map);
		}
	@Override
	public void removeApply(Map<String, Object> map) {
		controlDao.removeApply(map);
	}
	@Override
	public Control findOneByMap(Map<?, ?> map) {
		return controlDao.findOneByMap(map);
	}
}
