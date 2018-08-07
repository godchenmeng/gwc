package com.youxing.car.util;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;

/**
 * @author mars
 * @date 2017年4月12日 上午10:43:19
 */
@Component
public class QueryUtils {
	@Resource
	private OrganizationService organizationService;
	@Resource
	private UserService userService;

	/**
	 * 迭代获取用户树结构
	 * @param allOrg
	 * @param orgTree
	 */
	public static Organization queryOrgTree(List<Organization> allOrg, Organization orgTree, List<Long> orgIDs, List<Map<String, Object>> carList){
		Organization result = orgTree;
		boolean isRoot = false;
		for(int i = 0; i < allOrg.size(); i++){
			Organization tmpOrg = allOrg.get(i);
			if(result.getId().equals(tmpOrg.getId()) && StringUtils.isEmpty(result.getName())) {
				result = tmpOrg;
				isRoot = true;
			}
			if ("1".equals(tmpOrg.getType())) {
				tmpOrg.setCls("tree_org");
			} else if ("2".equals(tmpOrg.getType())) {
				tmpOrg.setCls("tree_dept");
			}
			orgIDs.add(tmpOrg.getId());
			if(null != tmpOrg.getParent() && tmpOrg.getParent().equals(result.getId())){
				queryOrgTree(allOrg,tmpOrg,orgIDs,carList);
				for (Map<String, Object> c : carList) {
					Long org_id = Long.parseLong(c.get("org_id").toString());
					if (tmpOrg.getId().equals(org_id)) {
						Organization cOrg = new Organization();
						cOrg.setId(Long.parseLong(c.get("car_id").toString()));
						cOrg.setName(c.get("car_no").toString());//用该字段保存车牌用于显示
						cOrg.setShort_name(c.get("device").toString());//用该字段保存设备编号
						cOrg.setType("3");
						tmpOrg.addChildren(cOrg);
					}
				}
				result.addChildren(tmpOrg);
			}

		}
		if(isRoot){
			for (Map<String, Object> c : carList) {
				Long org_id = Long.parseLong(c.get("org_id").toString());
				if (result.getId().equals(org_id)) {
					Organization cOrg = new Organization();
					cOrg.setId(Long.parseLong(c.get("car_id").toString()));
					cOrg.setName(c.get("car_no").toString());//用该字段保存车牌用于显示
					cOrg.setShort_name(c.get("device").toString());//用该字段保存设备编号
					cOrg.setType("3");
					result.addChildren(cOrg);
				}
			}
		}
		return result;
	}

	public void queryByOrg(String id, Map<String, Object> maps) {
		User user=userService.findById(Long.valueOf(id));
		long org=user.getOrg();
		Organization userOrg = organizationService.findById(user.getOrg());
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, Long.valueOf(user.getOrg()));
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				maps.put("list", org_list);
			} else {
				maps.put("id", id);
			}
		}
	}

}
