package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.CountPushType;
import com.youxing.car.entity.Push;
import com.youxing.car.service.base.BaseService;
public interface PushService extends BaseService<Push>{

	List<CountPushType> groupByType(Map<String,Object> map);
	
	void addRejectInfoService(Map<String,Object> map);
}
