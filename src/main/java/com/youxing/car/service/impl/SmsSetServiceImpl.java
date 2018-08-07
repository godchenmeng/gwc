package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.SmsSetDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.SMSSet;
import com.youxing.car.service.SmsSetService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class SmsSetServiceImpl extends BaseServiceImpl<SMSSet> implements SmsSetService{
	
	@Resource
	private SmsSetDao setDao;

	@Override
	public BaseDao<SMSSet> getBaseDao() {
		return this.setDao;
	}

}
