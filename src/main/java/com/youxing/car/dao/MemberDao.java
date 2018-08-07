package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Member;
public interface MemberDao extends BaseDao<Member>{
	
	List<Member> listMemberAndOrg(Map<?, ?> map);
	
	List<Member> getMember(Map<?, ?> map);
	
	List<Member> getMemberIds(Map<?, ?> map);
	
	List<Member> pageByWeb(Map<?, ?> map);
	
	void updateSmsType(Map<?, ?> map);
	void setSmsType();
	
	int countDriverBy(Map<?, ?> map);
	
	Map getOrgLogo(Map<?, ?> map);
}
