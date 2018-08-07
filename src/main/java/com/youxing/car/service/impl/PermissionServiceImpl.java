package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.PermissionDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Permission;
import com.youxing.car.service.PermissionService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission> implements PermissionService{
	@Resource
	private PermissionDao permissionDao;	
	public BaseDao<Permission> getBaseDao() {
		return this.permissionDao;
	}
	@Override
	public List<Permission> getTranslations(Map<String,Object> map) {
		return permissionDao.getTranslations(map);
	}
}
