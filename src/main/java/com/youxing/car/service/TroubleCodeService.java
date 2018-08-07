package com.youxing.car.service;
import java.util.Map;

import com.youxing.car.entity.TroubleCode;
import com.youxing.car.service.base.BaseService;
public interface TroubleCodeService extends BaseService<TroubleCode>{

	Integer countByType(Map<String,Object> map);
}
