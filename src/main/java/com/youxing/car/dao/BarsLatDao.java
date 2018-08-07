package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsLat;
public interface BarsLatDao extends BaseDao<BarsLat>{
	
	//App
	List<BarsLat> listBarid(Map<String, Object> map);
	
	//WEB   gy
	void insertBatch(List<BarsLat> list);
	
	void removeByBarid(Long barid);
}
