package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.BarsLat;
import com.youxing.car.service.base.BaseService;
public interface BarsLatService extends BaseService<BarsLat>{
	
	//gy
	public void insertBatch(List<BarsLat> list);
	
	public void removeByBarid(Long barid);
	
	List<BarsLat> listBarid(Map<String, Object> map);

}
