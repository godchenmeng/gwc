package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.User;
public interface UserDao extends BaseDao<User>{
	
	List<String> listMid(Map<String,Object> map);
	
	 String findStatusByName(String name);
	 
	 List<User> findUserNames();
	 
	 String findUserRole(String name);
	 
	 void updateRole(String name);
	 
	 List<User> pageByWeb(Map<String,Object> map);
	 
	 List<User> countIds(Map<String,Object> map);
	 
	 //获取表的字段名称
	 List<Selects> getColumn(String table_name);
	
}
