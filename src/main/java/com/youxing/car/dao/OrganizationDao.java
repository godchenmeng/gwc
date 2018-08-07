package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Selects;
public interface OrganizationDao extends BaseDao<Organization>{
	
	public Organization findByName(String name);
	
	Long findIdByName(String name);

	Organization findUserOrg(long uid);
	
	List<Organization>findIdsById(long id);
	
	/**
	 * ============== web
	 */
	
	//gy
   void modifyChild(Map<String,Object> map);
	
	String findTypeById(String id);
	
	List<Organization> countChild(long oid);
	
	List<Organization> findIdsBytype(Map<?, ?> map);
	
	List<Organization> findOrgName(Map<?, ?> map);
	
	List<Organization> findOrgNameAndId();
	
	List<Long> findChildIdsByParentId(long id);
	
	List<Selects> findOrgNameAndIds(Map<?, ?> map);
	
	
}
