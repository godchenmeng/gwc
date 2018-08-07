package com.youxing.car.dao;
import java.util.List;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Role;
public interface RoleDao extends BaseDao<Role>{
	
	List<Role> findRoleNames();
}
