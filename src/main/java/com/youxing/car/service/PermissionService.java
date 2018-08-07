package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Permission;
import com.youxing.car.service.base.BaseService;
public interface PermissionService extends BaseService<Permission>{
	
	List<Permission> getTranslations(Map<String,Object> map);


}
