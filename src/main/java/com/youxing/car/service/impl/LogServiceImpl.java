package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.LogDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.SysLog;
import com.youxing.car.service.LogService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

/**   
 * @author mars   
 * @date 2017年4月25日 上午9:10:04 
 */
@Service
public class LogServiceImpl extends BaseServiceImpl<SysLog> implements LogService  {

	@Resource
	private LogDao logDao;
	@Override
	public BaseDao<SysLog> getBaseDao() {
		return logDao;
	}

}


