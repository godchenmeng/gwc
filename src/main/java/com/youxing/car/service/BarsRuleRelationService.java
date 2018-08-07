package com.youxing.car.service;
import java.util.List;

import com.youxing.car.entity.BarsRuleRelation;
import com.youxing.car.service.base.BaseService;
public interface BarsRuleRelationService extends BaseService<BarsRuleRelation>{
	
	public void insertBatch(List<BarsRuleRelation> list);
	
	public void removeByRuleid(Long ruleid);
	
	public List<BarsRuleRelation> listBrrCarDevice(Long ruleid);
}
