package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.BarsLatDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.BarsLat;
import com.youxing.car.service.BarsLatService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class BarsLatServiceImpl extends BaseServiceImpl<BarsLat> implements BarsLatService{
	@Resource
	private BarsLatDao barslatDao;	
	public BaseDao<BarsLat> getBaseDao() {
		return this.barslatDao;
	}
	
	@Override
	public void insertBatch(List<BarsLat> list) {
		barslatDao.insertBatch(list);
	}

	@Override
	public void removeByBarid(Long barid) {
		barslatDao.removeByBarid(barid);
	}

	@Override
	public List<BarsLat> listBarid(Map<String, Object> map) {
		return barslatDao.listBarid(map);
	}
}
