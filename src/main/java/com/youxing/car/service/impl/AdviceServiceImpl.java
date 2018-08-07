package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.AdviceDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Advice;
import com.youxing.car.service.AdviceService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

/**   
 * @author mars   
 * @date 2017年5月18日 下午2:03:53 
 */
@Service
public class AdviceServiceImpl extends BaseServiceImpl<Advice> implements AdviceService {

	@Resource
	private AdviceDao adviceDao;
	
	@Override
	public BaseDao<Advice> getBaseDao() {
		return adviceDao;
	}

}


