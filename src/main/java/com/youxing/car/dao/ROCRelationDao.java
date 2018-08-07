package com.youxing.car.dao;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.ROCRelation;

public interface ROCRelationDao extends BaseDao<ROCRelation>{
	
	void removeByRegionId(String region);

}
