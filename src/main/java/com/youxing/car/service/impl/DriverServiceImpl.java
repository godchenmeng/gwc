package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.DriverDao;
import com.youxing.car.dao.MemberDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Member;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class DriverServiceImpl extends BaseServiceImpl<Driver> implements DriverService{
	@Resource
	private DriverDao driverDao;	
	
	@Resource
	private MemberDao memberDao;
	
	public BaseDao<Driver> getBaseDao() {
		return this.driverDao;
	}
	@Override
	public List<Driver> pageDriver(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return driverDao.pageDriver(map);
	}
	
	
	
	@Override
	public void add(Driver driver, Member me) {
		if(me.getId()!=null){
			memberDao.modify(me);
		}else{
			memberDao.add(me);
			long id = me.getId();
			driver.setUid(id);
		}
		driverDao.add(driver);
	}
	@Override
	public void modify(Driver driver, Member me) {
		driverDao.modify(driver);
		memberDao.modify(me);
		
	}
	@Override
	public List<String> listDr() {
		return driverDao.listDr();
	}
	@Override
	public void updateTaskStatusService(Driver d) {
		driverDao.updateTaskStatus(d);		
	}
	@Override
	public List<Driver> pageByWebService(Map<String, Object> map) {
		return driverDao.pageByWeb(map);
	}
	@Override
	public void modifyWebService(Driver driver) {
		driverDao.modifyWeb(driver);
	}
	@Override
	public int countByWeb(Map<String, Object> map) {
		return driverDao.countByWeb(map);
	}
	
	
	
	@Override
	public void modifyDriver(Driver driver) {
		driverDao.modifyDriver(driver);
	}
	@Override
	public int pageByWebCount(Map<String, Object> map) {
		return driverDao.pageByWebCount(map);
	}
	@Override
	public void updateTaskStatus(Driver driver) {
		driverDao.updateTaskStatus(driver);		
	}
	
	@Override
	public void modify(Driver driver){
		//删除司机时修改用户司机状态
		if(driver.getStatus() != null){
			if(driver.getStatus().equals(Constant.REMOVE_STATUS)){
				Member member = new Member();
				member.setId(driver.getUid());
				member.setDriver(Constant.REMOVE_STATUS);
				memberDao.modify(member);
			}
		}
		driverDao.modify(driver);
	}
}
