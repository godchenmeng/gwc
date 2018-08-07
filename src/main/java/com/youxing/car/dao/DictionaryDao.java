package com.youxing.car.dao;

import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Dictionary;

public interface DictionaryDao extends BaseDao<Dictionary>{
	
	List<Dictionary> findtextAndValue(Map<String,Object> map);

}
