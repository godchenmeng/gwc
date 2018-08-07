package com.youxing.car.dao;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.TroubleCode;
public interface TroubleCodeDao extends BaseDao<TroubleCode>{
	Integer countByType(Map<String,Object> map);
}
