package com.youxing.car.service;

import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Dictionary;
import com.youxing.car.service.base.BaseService;

public interface DictionaryService extends BaseService<Dictionary> {
	
	List<Dictionary> findtextAndValue(Map<String,Object> map);

}
