package com.youxing.car.service;

import com.youxing.car.entity.ROCRelation;
import com.youxing.car.service.base.BaseService;

public interface ROCRelationService extends BaseService<ROCRelation>{
	
	void removeByRegionId(String region);

}
