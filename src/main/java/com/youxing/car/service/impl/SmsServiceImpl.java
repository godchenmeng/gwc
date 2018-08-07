package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.SmsDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.SMS;
import com.youxing.car.service.SmsService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class SmsServiceImpl extends BaseServiceImpl<SMS> implements SmsService{
	
	@Resource
	private SmsDao smsDao;

	@Override
	public BaseDao<SMS> getBaseDao() {
		return this.smsDao;
	}

}
