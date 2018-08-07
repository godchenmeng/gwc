package com.youxing.car.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.RegionDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Region;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.RegionService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class RegionServiceImpl extends BaseServiceImpl<Region> implements RegionService{
	
	@Resource
	private RegionDao regionDao;

	@Override
	public BaseDao<Region> getBaseDao() {
		return this.regionDao;
	}

	@Override
	public Long findNew() {
		return regionDao.findNew();
	}

	@Override
	public void closeOrOpenOrdeleteRegion(Map<String, Object> map) {
		regionDao.closeOrOpenOrdeleteRegion(map);
	}

	@Override
	public List<Region> findAllRegion(Map<String, Object> map) {
		return regionDao.findAllRegion(map);
	}

	@Override
	public int countAllRegion(Map<String, Object> map) {
		return regionDao.countAllRegion(map);
	}

	@Override
	public List<Selects> listOrg(HashMap<String, Object> map) {
		// TODO Auto-generated method stub
		return regionDao.listOrg(map);
	}

	
	@Override
	public List<Region> findCountRegion(Map<String, Object> map) {
		return regionDao.findCountRegion(map);
	}

	@Override
	public List<Object> getSupplyCount(Map<String, Object> map) {
		return regionDao.getSupplyCount(map);
	}

	@Override
	public int getSupplyCountNum(Map<String, Object> map) {
		return regionDao.getSupplyCountNum(map);
	}

	@Override
	public List<Object> getSupplyByCarId(Map<String, Object> map) {
		return regionDao.getSupplyByCarId(map);
	}

	@Override
	public int getSupplyByCarIdNum(Map<String, Object> map) {
		return regionDao.getSupplyByCarIdNum(map);
	}

	@Override
	public List<Region> findRegionDatas(Map<String, Object> map) {
		return regionDao.findRegionDatas(map);
	}

}
