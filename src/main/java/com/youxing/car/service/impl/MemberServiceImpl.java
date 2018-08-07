package com.youxing.car.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.youxing.car.dao.DriverDao;
import com.youxing.car.dao.MemberDao;
import com.youxing.car.dao.UserDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.User;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class MemberServiceImpl extends BaseServiceImpl<Member> implements MemberService{
	@Resource
	private MemberDao memberDao;	
	@Resource
	private UserDao userDao;
	@Resource
	private DriverDao driverDao;
	
	public BaseDao<Member> getBaseDao() {
		return this.memberDao;
	}
	@Override
	public void modify(Member mbs, Long id) {
		memberDao.modify(mbs);
	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("mid", id);
	    List<User> list= userDao.listBy(map);
	    if(!CollectionUtils.isEmpty(list)){
	    	User u1 = new User();
	    	for(User u:list){
	    		u1.setId(u.getId());
	    		u1.setStatus(Constant.REMOVE_STATUS);
	    		userDao.modify(u1);
	    	}
	    }
		
	}
	@Override
	public List<Member> listMemberAndOrgService(Map<?, ?> map) {
		return memberDao.listMemberAndOrg(map);
	}
	@Override
	public List<Member> getMemberService(Map<?, ?> map) {
		return memberDao.getMember(map);
	}
	@Override
	public List<Member> getMemberIdsService(Map<?, ?> map) {
		return memberDao.getMemberIds(map);
	}
	@Override
	public void updateSmsTypeService(Map<?, ?> map) {
		memberDao.updateSmsType(map);
	}
	@Override
	public void setSmsTypeService() {
		memberDao.setSmsType();
	}
	@Override
	public List<Member> pageByWeb(Map<?, ?> map) {
		return memberDao.pageByWeb(map);
	}
	@Override
	public int countDriverBy(Map<?, ?> map) {
		return memberDao.countDriverBy(map);
	}
	@Override
	public void removeMemAndUserById(Long id) {
		memberDao.removeById(id);
	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("mid", id);
	    List<User> list= userDao.listBy(map);
	    if(!CollectionUtils.isEmpty(list)){
	    	for(User u:list){
	    		userDao.removeById(u.getId());
	    	}
	    }
		
	}
	@Override
	public Map getOrgLogo(Map<?, ?> map) {
		return memberDao.getOrgLogo(map);
	}
}
