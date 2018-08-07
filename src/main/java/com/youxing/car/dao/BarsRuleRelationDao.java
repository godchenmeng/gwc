package com.youxing.car.dao;
import java.util.List;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsRuleRelation;
public interface BarsRuleRelationDao extends BaseDao<BarsRuleRelation>{
	void insertBatch(List<BarsRuleRelation> list);
	void removeByRuleid(Long ruleid);
	List<BarsRuleRelation> listBrrCarDevice(Long ruleid);
}
