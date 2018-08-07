package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Selects;
import com.youxing.car.service.base.BaseService;
public interface OrganizationService extends BaseService<Organization>{
	//gy
	List<Long> getOrganizationUser(Organization o ,Long org);
	
	//gy
	List<Long> getChildren(Long org);
	
    Organization findByNameService(String name);
    
    //gy
    Long findIdByNameService(String name);

    //gy
    List<Organization>findIdsByIdService(long id);
    
    
    /**
     * =======================web
     */
    
    //gy
	void modify(Organization org, List<Long> child);
	
	
	//gy
	String findTypeByIdService(String id);
    
	List<Organization>countChildService(long oid);
    
	List<Organization> findIdsBytypeService(Map<?, ?> map);
	//gy
	List<Organization> findOrgNameService(Map<?, ?> map);
	
	List<Organization> findOrgNameAndId();
	
	List<Long> findChildIdsByParentId(Long parentId);
	
	List<Selects> findOrgNameAndIds(Map<?, ?> map);

	Organization findUserOrg(long uid);
}
