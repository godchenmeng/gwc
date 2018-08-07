package com.youxing.car.service;
import java.util.List;

import com.youxing.car.entity.Permission;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.service.base.BaseService;
public interface RolePermissionService extends BaseService<RolePermission>{

	List<Long> getPermission(Long id);

	List<Permission> getRolePermission(Long id);
	List<String> getRolePermissionUrl(Long id);
	
	List<Object> getRolePermissionUrlList(Long id);
}
