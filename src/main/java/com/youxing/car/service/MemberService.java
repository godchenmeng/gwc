package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Member;
import com.youxing.car.service.base.BaseService;
public interface MemberService extends BaseService<Member>{
	
	void modify(Member mbs, Long id);
	
	//gy
	List<Member> listMemberAndOrgService(Map<?, ?> map);
	
	List<Member> getMemberService(Map<?, ?> map);
	
	List<Member> getMemberIdsService(Map<?, ?> map);
	
	List<Member> pageByWeb(Map<?, ?> map);
	
	void updateSmsTypeService(Map<?, ?> map);
	void setSmsTypeService();

	int countDriverBy(Map<?, ?> map);
	
	void removeMemAndUserById(Long id);
	
	Map getOrgLogo(Map<?, ?> map);
}
