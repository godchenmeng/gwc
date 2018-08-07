package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Member;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.User;
import com.youxing.car.service.base.BaseService;
public interface UserService extends BaseService<User>{
	
   void add(User user, Member member);
	
	List<String> listMid(Map<String,Object> map);
	
	 String findStatusByNameService(String name);
	 
	 List<User> findUserNamesService();
	 
	 String findUserRoleService(String name);
	 
	 void updateRoleService(String name);
	 
	 List<User> pageByWebService(Map<String,Object> map);
	 
	 List<User> countIdsService(Map<String,Object> map);
	 
	 //获取表的字段名称
	 List<Selects>  getColumn(String table_name);

	
}
