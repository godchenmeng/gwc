package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Bars;
import com.youxing.car.service.base.BaseService;
public interface BarsService extends BaseService<Bars>{
	
	public List<Bars> pageBarsAndLatsBy(Map<String,Object> map);
	
	//gy
	public void updateStatusByIds(Map<String,Object> map);
	
	List<Bars> findNameService();
	
	public int countBarsAndLatsBy(Map<String,Object> map);
}
