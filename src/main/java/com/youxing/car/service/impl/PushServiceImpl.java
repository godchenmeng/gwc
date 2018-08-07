package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.PushDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CountPushType;
import com.youxing.car.entity.Push;
import com.youxing.car.service.PushService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class PushServiceImpl extends BaseServiceImpl<Push> implements PushService{
	@Resource
	private PushDao pushDao;	
	public BaseDao<Push> getBaseDao() {
		return pushDao;
	}
	@Override
	public List<CountPushType> groupByType(Map<String, Object> map) {
		return pushDao.groupByType(map);
	}
	@Override
	public void addRejectInfoService(Map<String, Object> map) {
		pushDao.addRejectInfo(map);
	}
}
