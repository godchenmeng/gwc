package com.youxing.car.dao;
import java.util.List;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Permission;
import com.youxing.car.entity.RolePermission;
public interface RolePermissionDao extends BaseDao<RolePermission>{
	
	List<Long> getPermission(Long id);
	
	List<Permission> getRolePermission(Long id);
	List<String> getRolePermissionUrl(Long id);
	List<Object> getRolePermissionUrlList(Long id);
}
