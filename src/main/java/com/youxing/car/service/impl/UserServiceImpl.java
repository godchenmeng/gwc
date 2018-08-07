package com.youxing.car.service.impl;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.MemberDao;
import com.youxing.car.dao.UserDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.User;
import com.youxing.car.service.UserService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService{
	@Resource
	private UserDao userDao;
	
	@Resource
	private MemberDao memberDao;
	
	public BaseDao<User> getBaseDao() {
		return this.userDao;
	}
	
	
	@Override
	public void add(User user, Member member) {
		memberDao.add(member);
		Long id = member.getId();
		user.setMid(id+"");
		userDao.add(user);		
	}
	@Override
	public List<String> listMid(Map<String, Object> map) {
		return userDao.listMid(map);
	}
	@Override
	public String findStatusByNameService(String name) {
		return userDao.findStatusByName(name);
	}
	@Override
	public List<User> findUserNamesService() {
		return userDao.findUserNames();
	}
	@Override
	public String findUserRoleService(String name) {
		return userDao.findUserRole(name);
	}
	@Override
	public void updateRoleService(String name) {
		userDao.updateRole(name);
	}


	@Override
	public List<User> pageByWebService(Map<String, Object> map) {
		return userDao.pageByWeb(map);
	}


	@Override
	public List<User> countIdsService(Map<String, Object> map) {
		return userDao.countIds(map);
	}
	
	@Override
	public List<Selects> getColumn(String table_name) {
		return userDao.getColumn(table_name);
	}
	
}
