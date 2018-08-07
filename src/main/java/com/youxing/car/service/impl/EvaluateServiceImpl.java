package com.youxing.car.service.impl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.youxing.car.dao.EvaluateDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Evaluate;
import com.youxing.car.service.EvaluateService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class EvaluateServiceImpl extends BaseServiceImpl<Evaluate> implements EvaluateService{
	@Resource
	private EvaluateDao evaluateDao;	
	public BaseDao<Evaluate> getBaseDao() {
		return this.evaluateDao;
	}
}
