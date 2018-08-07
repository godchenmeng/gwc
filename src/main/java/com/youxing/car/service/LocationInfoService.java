package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.LocationInfo;
import com.youxing.car.service.base.BaseService;
public interface LocationInfoService extends BaseService<LocationInfo>{
	//gy
	public List<LocationInfo> pageByLocationInfo(Map<String, Object> map);

	public List<CarNotice> pageByLocateInfo(Map<String, Object> map);
	
	public List<CarNotice> getNoticeExcel(Map<String, Object> map);
	
	//gy
	public int countByLocationInfo(Map<String, Object> map);

	public int countByLocateInfo(Map<String, Object> map);

}
