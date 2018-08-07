package com.youxing.car.service;
import java.util.List;

import com.youxing.car.entity.Role;
import com.youxing.car.service.base.BaseService;
public interface RoleService extends BaseService<Role>{

	void add(Role role, String[] pid);

	void modify(Role role, String[] pid,Long id);
	
	List<Role> findRoleNames();


}
