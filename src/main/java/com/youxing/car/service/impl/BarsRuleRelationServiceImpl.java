package com.youxing.car.service.impl;
import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.youxing.car.dao.BarsRuleRelationDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsRuleRelation;
import com.youxing.car.service.BarsRuleRelationService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class BarsRuleRelationServiceImpl extends BaseServiceImpl<BarsRuleRelation> implements BarsRuleRelationService{
	@Resource
	private BarsRuleRelationDao barsRuleRelation;	
	public BaseDao<BarsRuleRelation> getBaseDao() {
		return this.barsRuleRelation;
	}
	@Override
	public void insertBatch(List<BarsRuleRelation> list) {
		barsRuleRelation.insertBatch(list);
	}
	@Override
	public void removeByRuleid(Long ruleid) {
		barsRuleRelation.removeByRuleid(ruleid);
	}
	@Override
	public List<BarsRuleRelation> listBrrCarDevice(Long ruleid) {
		return barsRuleRelation.listBrrCarDevice(ruleid);
	}
}
