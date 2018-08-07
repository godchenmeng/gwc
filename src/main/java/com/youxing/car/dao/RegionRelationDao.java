package com.youxing.car.dao;

import java.util.List;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.RegionRelation;

public interface RegionRelationDao  extends BaseDao<RegionRelation>{
	void insertBatch(List<RegionRelation> list);
}
