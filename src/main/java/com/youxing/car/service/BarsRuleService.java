package com.youxing.car.service;
import com.youxing.car.entity.BarsRule;
import com.youxing.car.service.base.BaseService;
public interface BarsRuleService extends BaseService<BarsRule>{
	
	void removeBybarId(Long barid);
	
	
}
