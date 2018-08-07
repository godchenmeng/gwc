package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.OvertimeDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.OverTime;
import com.youxing.car.service.OvertimeService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

import java.util.List;


@Service
public class OverTimeServiceImpl extends BaseServiceImpl<OverTime> implements OvertimeService{

	
	@Resource
	private OvertimeDao overtimeDao;
	@Override
	public BaseDao<OverTime> getBaseDao() {
		return this.overtimeDao;
	}
	@Override
	public void setRead(List<Long> ids){ overtimeDao.setRead(ids);}
}
