package com.youxing.car.service;

import java.util.Map;

import com.youxing.car.entity.Read;
import com.youxing.car.service.base.BaseService;

public interface ReadService extends BaseService<Read>{
	
	public void updateIsReadService(Map<?, ?> map);
	
	public Object getAllCountService(Map<?, ?> map); 

}
