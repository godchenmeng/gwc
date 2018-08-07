package com.youxing.car.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.ReadDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Read;
import com.youxing.car.service.ReadService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class ReadSeviceImpl extends BaseServiceImpl<Read> implements ReadService {
	
	@Resource
	private ReadDao readDao;

	@Override
	public BaseDao<Read> getBaseDao() {
		return this.readDao;
	}

	@Override
	public void updateIsReadService(Map<?, ?> map) {
		readDao.updateIsRead(map);
	}

	@Override
	public Object getAllCountService(Map<?, ?> map) {
		return readDao.getAllCount(map);
	}
	
	
	

}
