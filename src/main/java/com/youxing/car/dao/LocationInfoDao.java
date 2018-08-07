package com.youxing.car.dao;

import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.LocationInfo;

public interface LocationInfoDao extends BaseDao<LocationInfo>{
	
	List<LocationInfo> pageByLocationInfo(Map<String, Object> map);

	List<CarNotice> pageByLocateInfo(Map<String, Object> map);
	
	List<CarNotice> getNoticeExcel(Map<String, Object> map);
	
	public int countByLocationInfo(Map<String, Object> map);

	public int countByLocateInfo(Map<String, Object> map);
}
