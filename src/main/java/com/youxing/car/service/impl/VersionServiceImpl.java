package com.youxing.car.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.VersionDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Version;
import com.youxing.car.service.VersionService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

/**   
 * @author mars   
 * @date 2017年5月18日 下午1:19:26 
 */
@Service
public class VersionServiceImpl extends BaseServiceImpl<Version> implements VersionService{

	@Resource
	private VersionDao versionDao;
	
	@Override
	public BaseDao<Version> getBaseDao() {
		return versionDao;
	}

}


