package com.youxing.car.service.impl;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.BarsRuleDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsRule;
import com.youxing.car.service.BarsRuleService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class BarsRuleServiceImpl extends BaseServiceImpl<BarsRule> implements BarsRuleService{
	@Resource
	private BarsRuleDao barsRuleDao;	
	public BaseDao<BarsRule> getBaseDao() {
		return this.barsRuleDao;
	}
	@Override
	public void removeBybarId(Long barid) {
		barsRuleDao.removeBybarId(barid);
	}
}
