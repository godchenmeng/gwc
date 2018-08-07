package com.youxing.car.dao;

import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Read;

public interface ReadDao extends BaseDao<Read>{
	
	
	public void updateIsRead(Map<?, ?> map);
	
	public Object getAllCount(Map<?, ?> map); 
	

}
