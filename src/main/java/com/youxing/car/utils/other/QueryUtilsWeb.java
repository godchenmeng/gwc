package com.youxing.car.utils.other;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;

/**   
 * @author mars   
 * @date 2017年4月12日 上午10:43:19 
 */
@Component
public class QueryUtilsWeb {
	@Resource
	private  OrganizationService organizationService;
	
	@Resource
	private MemberService memberService;
	@Resource
	private UserService userService;
	
	public  void queryByOrg(HttpServletRequest request,Map<String,Object> maps){
		//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long u_org = user.getOrg();
		Organization userOrg = organizationService.findById(u_org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, u_org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(u_org);
				maps.put("list", org_list);
			} else {
				maps.put("id", u_org);
			}
		}
	}
	public  void queryByOrgWeb(HttpServletRequest request,Map<String,Object> maps){
		//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long u_org = user.getOrg();
		Organization userOrg = organizationService.findById(u_org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, u_org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(u_org);
				maps.put("list", org_list);
			} else {
				maps.put("org", u_org);
			}
		}
	}
	
	
	/**
	 * @author zzb
	 * @date 2017年7月10日09:30:52
	 * 当单独选择某个机构时，查询其下的所有子机构
	 * */
	public  void queryByOrgOne(Long org,Map<String,Object> maps){
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				maps.put("list", org_list);
			} else {
				maps.put("org", org);
			}
		}
	}
	
	public  void queryByOrg(Long org,Map<String,Object> maps){
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				maps.put("org_list", org_list);
			} else {
				maps.put("org", org);
			}
		}
	}
	
	public  void queryChildOrgsByParentId(Long parentId,Map<String,Object> map,String key){
		List<Long> childOrgs = organizationService.findChildIdsByParentId(parentId);
		childOrgs.add(parentId);
		map.put(key, childOrgs);
	}
	
	//后期全部改成该方法获取member的org以及子机构
	public  void getOrgList(HttpServletRequest request,Map<String,Object> maps){
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long mid = Long.parseLong(user.getMid());
		Member mem = memberService.findById(mid);
		Long m_org = mem.getOrg();
		Organization userOrg = organizationService.findById(m_org);
		if (userOrg.getParent() != null) {//如果没有父级，则属于顶级机构，查询全部数据
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, m_org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(m_org);
				maps.put("org_list", org_list);
			} else {
				maps.put("org", m_org);
			}
		}
	}
	
	//机构及下属机构拥有车辆查询
	public  void queryOrg(String orgId,Map<String,Object> maps){
		Long u_org = Long.parseLong(orgId);
		Organization userOrg = organizationService.findById(u_org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, u_org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(u_org);
				maps.put("org_list", org_list);
			} else {
				maps.put("org_id", u_org);
			}
		}
	}
}
	


