package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Permission;
public interface PermissionDao extends BaseDao<Permission>{
	
	List<Permission> getTranslations(Map<String,Object> map);
}
