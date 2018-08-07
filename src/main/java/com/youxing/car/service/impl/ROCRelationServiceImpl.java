package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.ROCRelationDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.ROCRelation;
import com.youxing.car.service.ROCRelationService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class ROCRelationServiceImpl extends BaseServiceImpl<ROCRelation> implements ROCRelationService{
	
	@Resource
	private ROCRelationDao rocRelationDao;

	@Override
	public BaseDao<ROCRelation> getBaseDao() {
		return this.rocRelationDao;
	}

	@Override
	public void removeByRegionId(String region) {
		rocRelationDao.removeByRegionId(region);
	}

}
