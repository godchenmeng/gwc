package com.youxing.car.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.RegionDao;
import com.youxing.car.dao.RegionRelationDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Region;
import com.youxing.car.entity.RegionRelation;
import com.youxing.car.service.RegionRelationService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class RegionRelationServiceImpl extends BaseServiceImpl<RegionRelation> implements RegionRelationService {

	@Resource
	private RegionRelationDao rrDao;
	@Resource
	private RegionDao regionDao;

	@Override
	public BaseDao<RegionRelation> getBaseDao() {
		return this.rrDao;
	}

	@Override
	public void add(Region region, List<RegionRelation> list) {
		regionDao.add(region);
		for (RegionRelation regionRelation : list) {
			regionRelation.setRegion_id(region.getId());
		}
		
		if(list.size() > 0) {
			rrDao.insertBatch(list);
		}
	}

	@Override
	public void modify(Region region,List<RegionRelation> list) {
		rrDao.removeById(region.getId());
		regionDao.modify(region);
		for (RegionRelation regionRelation : list) {
			regionRelation.setRegion_id(region.getId());
		}
		rrDao.insertBatch(list);
	}

	@Override
	public void modify(String[] ids, String status) {
		for(String id:ids) {
			Region region = regionDao.findById(Long.valueOf(id));
			if(region!=null) {
				region.setStatus(status);
				regionDao.modify(region);
			}
		}
		
	}

}
