package com.youxing.car.dao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsRule;
public interface BarsRuleDao extends BaseDao<BarsRule>{
	
	BarsRule findBarsRuleAndCars(Long barid);
	
	void removeBybarId(Long barid);
}
