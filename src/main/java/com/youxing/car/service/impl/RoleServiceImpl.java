package com.youxing.car.service.impl;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.RoleDao;
import com.youxing.car.dao.RolePermissionDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Role;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.service.RoleService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService{
	@Resource
	private RoleDao roleDao;
	@Resource
	private RolePermissionDao rolePermissionDao;
	public BaseDao<Role> getBaseDao() {
		return this.roleDao;
	}
	@Override
	public void add(Role role, String[] pid) {
		roleDao.add(role);
		Long id = role.getId();
		Date date = role.getCreatedate();
		for(String p:pid){
			if(!p.equals("-1")){				
				RolePermission pe = new RolePermission();
				pe.setCreatedate(date);
				pe.setCreatedby(role.getCreateby());
				pe.setRid(id);
				pe.setPid(Long.parseLong(p));
				rolePermissionDao.add(pe);
			}
		}
		
	}
	@Override
	public void modify(Role role, String[] pid,Long id) {
		roleDao.modify(role);
		Date date  = new Date();
		Long rid = role.getId();
		List<Long> list = rolePermissionDao.getPermission(rid);
		//添加新加的权限
		for(String p:pid){
			if(!p.equals("-1")){
				Long pd = Long.valueOf(p);
				if(!list.contains(pd)){
					RolePermission pe = new RolePermission();
					pe.setCreatedate(date);
					pe.setCreatedby(id);
					pe.setRid(rid);
					pe.setPid(pd);
					rolePermissionDao.add(pe);
				}else{
					list.remove(pd);
				}
			}
		}
		//删除多余的权限
		for(Long lo:list){
			RolePermission entity = new RolePermission();
			entity.setRid(rid);
			entity.setPid(lo);
			RolePermission rp = rolePermissionDao.findBy(entity);
			if(rp!=null){
				rolePermissionDao.removeById(rp.getId());
			}
		}
	}
	@Override
	public List<Role> findRoleNames() {
		return roleDao.findRoleNames();
	}
}
