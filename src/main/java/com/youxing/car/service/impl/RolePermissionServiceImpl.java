package com.youxing.car.service.impl;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.RolePermissionDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Permission;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.service.RolePermissionService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission> implements RolePermissionService{
	@Resource
	private RolePermissionDao rolepermissionDao;	
	public BaseDao<RolePermission> getBaseDao() {
		return this.rolepermissionDao;
	}
	@Override
	public List<Long> getPermission(Long id) {
		return rolepermissionDao.getPermission(id);
	}
	
	@Override
	public List<String> getRolePermissionUrl(Long id) {
		return rolepermissionDao.getRolePermissionUrl(id);
	}
	@Override
	public List<Permission> getRolePermission(Long id) {
		return rolepermissionDao.getRolePermission(id);
	}
	@Override
	public List<Object> getRolePermissionUrlList(Long id) {
		return rolepermissionDao.getRolePermissionUrlList(id);
	}
}
