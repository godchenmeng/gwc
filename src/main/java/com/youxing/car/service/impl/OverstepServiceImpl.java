package com.youxing.car.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.OverstepDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Overstep;
import com.youxing.car.service.OverstepService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

/**
* @author jianlu
* @version 创建时间：2018年1月23日 上午9:31:34
* @ClassName 类名称
* @Description 类描述
*/
@Service
public class OverstepServiceImpl extends BaseServiceImpl<Overstep> implements OverstepService{
	
	@Resource
	private OverstepDao overstepDao;

	@Override
	public List<Overstep> pageByWeb(Map<?, ?> map) {
		return overstepDao.pageByWeb(map);
	}

	@Override
	public Integer countByWeb(Map<?, ?> map) {
		return overstepDao.countByWeb(map);
	}

	@Override
	public List<Overstep> getInfoDetail(Map<?, ?> map) {
		return overstepDao.getInfoDetail(map);
	}

	@Override
	public BaseDao<Overstep> getBaseDao() {
		return overstepDao;
	}

	@Override
	public List<HashMap<String, Object>> pageByMonthL(Map<?, ?> map) {
		return overstepDao.pageByMonthL(map);
	}

	@Override
	public List<HashMap<String, Object>> pageByMonthC(Map<?, ?> map) {
		return overstepDao.pageByMonthC(map);
	}

	@Override
	public List<HashMap<String, Object>> pageByDayL(Map<?, ?> map) {
		return overstepDao.pageByDayL(map);
	}

	@Override
	public List<HashMap<String, Object>> pageByDayC(Map<?, ?> map) {
		return overstepDao.pageByDayC(map);
	}

	@Override
	public List<HashMap<String, Object>> getAllcountByOrgId(Map<?, ?> map) {
		return overstepDao.getAllcountByOrgId(map);
	}

	@Override
	public Integer getAllcount(Map<?, ?> map) {
		return overstepDao.getAllcount(map);
	}

	@Override
	public List<HashMap<String, Object>> getCountByOrgId(Map<?, ?> map) {
		return overstepDao.getCountByOrgId(map);
	}

	@Override
	public List<HashMap<String, Object>> overStepByCar(Map<?, ?> map) {
		return overstepDao.overStepByCar(map);
	}

	@Override
	public Integer overStepNum(Map<?, ?> map) {
		return overstepDao.overStepNum(map);
	}

	@Override
	public List<Map<String, Object>> overStepCountByOrg(Map<String, Object> map) {
		return overstepDao.overStepCountByOrg(map);
	}

	@Override
	public List<Map<String, Object>> overStepMonthAndWeek(Map<String, Object> map) {
		return overstepDao.overStepMonthAndWeek(map);
	}

	@Override
	public List<Map<String, Object>> overStepByCarEx(Map<String, Object> map) {
		return overstepDao.overStepByCarEx(map);
	}

}
