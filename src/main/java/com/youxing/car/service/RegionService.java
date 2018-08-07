package com.youxing.car.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Region;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.base.BaseService;

public interface RegionService extends BaseService<Region>{
	
	Long findNew();
	
	void closeOrOpenOrdeleteRegion(Map<String,Object> map);

	List<Region>findAllRegion(Map<String,Object> map);
	
	int countAllRegion(Map<String,Object> map);

	List<Selects> listOrg(HashMap<String, Object> map);


	
	List<Region> findCountRegion (Map<String,Object> map);
	
	List<Object> getSupplyCount(Map<String, Object> map);
	int getSupplyCountNum(Map<String,Object> map);
	
	//根据车牌号查询供给次数
	List<Object> getSupplyByCarId(Map<String, Object> map);
	int getSupplyByCarIdNum(Map<String,Object> map);
	
	List<Region>findRegionDatas(Map<String,Object> map);
	
	
}
