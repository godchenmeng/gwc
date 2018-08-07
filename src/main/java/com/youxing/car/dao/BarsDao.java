package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Bars;
public interface BarsDao extends BaseDao<Bars>{

	List<Bars> pageBarsAndLatsBy(Map<String, Object> map);
	
	void updateStatusByIds(Map<String,Object> map);
	
	List<Bars> findName();
	
	public int countBarsAndLatsBy(Map<String,Object> map);
}
