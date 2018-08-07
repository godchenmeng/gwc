package com.youxing.car.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.OrganizationDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
import org.springframework.cache.annotation.Cacheable;

@Service
public class OrganizationServiceImpl extends BaseServiceImpl<Organization> implements OrganizationService{
	@Resource
	private OrganizationDao organizationDao;	
	public BaseDao<Organization> getBaseDao() {
		return this.organizationDao;
	}

	public List<Long> getOrganization(List<Long> orgid, Long org) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parent", org);
		map.put("status", Constant.ADD_STATUS);
		List<Organization> list = organizationDao.listBy(map);
		if (list.size() > 0) {
			for (Organization orgz : list) {
				Long id = orgz.getId();
				if (!orgid.contains(id)) {
					orgid.add(id);
				}
				getOrganization(orgid, id);
			}
		}
		return orgid;
	}

	@Override
	@CacheEvict(value = "ORG", key = "'listAll'")
	public void add(Organization organization){
		organizationDao.add(organization);
	}

	@Override
	@CacheEvict(value = "ORG", key = "'listAll'")
	public void modify(Organization organization){
		organizationDao.modify(organization);
	}

	@Override
	@Cacheable(value = "ORG", key = "#root.methodName")
	public List<Organization> listAll(){
		return organizationDao.listAll();
	}

	@Override
	public List<Long> getOrganizationUser(Organization o ,Long org) {
		List<Long> list = new ArrayList<Long>();
		return getOrganizationByType(o,list, org);
	}

	public List<Long> getOrganizationByType(Organization o,List<Long> orgid, Long org) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parent", org);
		map.put("status", Constant.ADD_STATUS);
		List<Organization> list = organizationDao.listBy(map);
		if (list.size() > 0) {
			for (Organization orgz : list) {
				Long id = orgz.getId();
				if (!orgid.contains(id)) {
					orgid.add(id);
				}
				orgid.add(org);
				getOrganization(orgid, id);
			}
		}
		return orgid;
	}

	@Override
	public List<Long> getChildren(Long org) {
		List<Long> list = new ArrayList<Long>();
		getOrganization(list,org);
		return list;
	}
	@Override
	public Organization findByNameService(String name) {
		return organizationDao.findByName(name);
	}
	@Override
	public Long findIdByNameService(String name) {
		return organizationDao.findIdByName(name);
	}
	@Override
	public List<Organization> findIdsByIdService(long id) {
		return organizationDao.findIdsById(id);
	}
	
	@Override
	public void modify(Organization org, List<Long> child) {
		organizationDao.modify(org);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("list", child);
		map.put("emergency", org.getEmergency());
		organizationDao.modifyChild(map);
	}

	@Override
	public String findTypeByIdService(String id) {
		return organizationDao.findTypeById(id);
	}
	@Override
	public List<Organization> countChildService(long oid) {
		return organizationDao.countChild(oid);
	}
	@Override
	public List<Organization> findIdsBytypeService(Map<?, ?> map) {
		return organizationDao.findIdsBytype(map);
	}
	@Override
	public List<Organization> findOrgNameService(Map<?, ?> map) {
		return organizationDao.findOrgName(map);
	}
	@Override
	public List<Organization> findOrgNameAndId() {
		return organizationDao.findOrgNameAndId();
	}
	@Override
	public List<Long> findChildIdsByParentId(Long parentId) {
		return organizationDao.findChildIdsByParentId(parentId);
	}
	
	@Override
	public List<Selects> findOrgNameAndIds(Map<?, ?> map) {
		return organizationDao.findOrgNameAndIds(map);
	}

	@Override
	public Organization findUserOrg(long uid) {
		return organizationDao.findUserOrg(uid);
	}
}
