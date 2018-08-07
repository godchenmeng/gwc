package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.SmsRoleDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.SmsRole;
import com.youxing.car.service.SmsRoleService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class SmsRoleServiceImpl extends BaseServiceImpl<SmsRole> implements SmsRoleService{
	
	@Resource
	private SmsRoleDao smsRoleDao;

	@Override
	public BaseDao<SmsRole> getBaseDao() {
		return this.smsRoleDao;
	}

}
