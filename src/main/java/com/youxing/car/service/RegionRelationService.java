package com.youxing.car.service;

import java.util.List;

import com.youxing.car.entity.Region;
import com.youxing.car.entity.RegionRelation;
import com.youxing.car.service.base.BaseService;

public interface RegionRelationService extends BaseService<RegionRelation>{

	void add(Region region, List<RegionRelation> list);

	void modify(Region region, List<RegionRelation> list);

	void modify(String[] ids, String status);

}
