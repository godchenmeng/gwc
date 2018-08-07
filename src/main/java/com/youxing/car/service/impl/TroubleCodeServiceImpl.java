package com.youxing.car.service.impl;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.TroubleCodeDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.TroubleCode;
import com.youxing.car.service.TroubleCodeService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class TroubleCodeServiceImpl extends BaseServiceImpl<TroubleCode> implements TroubleCodeService{
	@Resource
	private TroubleCodeDao troublecodeDao;	
	public BaseDao<TroubleCode> getBaseDao() {
		return this.troublecodeDao;
	}
	@Override
	public Integer countByType(Map<String, Object> map) {
		return troublecodeDao.countByType(map);
	}
}
