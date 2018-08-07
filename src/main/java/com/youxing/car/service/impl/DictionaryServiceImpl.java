package com.youxing.car.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.DictionaryDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Dictionary;
import com.youxing.car.service.DictionaryService;
import com.youxing.car.service.base.impl.BaseServiceImpl;


@Service
public class DictionaryServiceImpl extends BaseServiceImpl<Dictionary> implements DictionaryService {
	
	@Resource
	private DictionaryDao dicDao;

	@Override
	public BaseDao<Dictionary> getBaseDao() {
		return this.dicDao;
	}
	
	@Override
	public List<Dictionary> findtextAndValue(Map<String, Object> map) {
		return dicDao.findtextAndValue(map);
	}

}
