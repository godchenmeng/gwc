package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CountPushType;
import com.youxing.car.entity.Push;
public interface PushDao extends BaseDao<Push>{
	
	List<CountPushType> groupByType(Map<String,Object> map);
	
	void addRejectInfo(Map<String,Object> map);
}
