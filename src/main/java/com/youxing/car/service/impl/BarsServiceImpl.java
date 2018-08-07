package com.youxing.car.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.BarsDao;
import com.youxing.car.dao.BarsRuleDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Bars;
import com.youxing.car.service.BarsService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class BarsServiceImpl extends BaseServiceImpl<Bars> implements BarsService{
	@Resource
	private BarsDao barsDao;
	@Resource
	private BarsRuleDao barsRuleDao;
	
	public BaseDao<Bars> getBaseDao() {
		return this.barsDao;
	}

	@Override
	public List<Bars> pageBarsAndLatsBy(Map<String, Object> map) {
		List<Bars> list = barsDao.pageBarsAndLatsBy(map); 
		List<Bars> subList = new ArrayList<Bars>();
		if(!CollectionUtils.isEmpty(list)){
			int startIndex = (int)map.get("startIdx");
			int endIndex = startIndex + (int)map.get("limit");
			int length = list.size() > endIndex ? endIndex : list.size();
			for(int i = startIndex; i < length; i++){
				subList.add(list.get(i));
			}			
		}
		return subList;
	}

	@Override
	public void updateStatusByIds(Map<String,Object> map) {
		barsDao.updateStatusByIds(map);
	}

	@Override
	public List<Bars> findNameService() {
		return barsDao.findName();
	}

	@Override
	public int countBarsAndLatsBy(Map<String, Object> map) {
		return barsDao.countBarsAndLatsBy(map);
	}
}
