package com.youxing.car.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.youxing.car.entity.Car;
import com.youxing.car.entity.OrgTree;
import com.youxing.car.entity.Organization;

/**
 * @author mars
 * @date 2017年3月27日 下午9:39:08
 */
public class TreeUtils {

	/**
	 * 
	 * @author mars
	 * @date 2017年4月12日 上午10:19:20
	 * @Description: TODO(判断树是否有叶子节点)
	 * @param @param list
	 * @param @param tree 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void setLazyTree(List<Organization> list, List<OrgTree> tree, List<Car> car, String id) {
		for (Organization org : list) {
			OrgTree ot = new OrgTree();
			ot.setId(org.getId());
			ot.setText(org.getName());
			ot.setType("1");
			tree.add(ot);
		}
		if (StringUtils.isNotBlank(id)) {
			Long ids = Long.valueOf(id);
			for (Car c : car) {
				if (c.getOrg() == ids) {
					OrgTree ot = new OrgTree();
					ot.setId(c.getId());
					ot.setText(c.getCar_no());
					ot.setType("2");
					ot.setAcc(c.getAcc());
					ot.setLatitude(c.getLatitude());
					ot.setLongitude(c.getLongitude());
					tree.add(ot);
				}
			}
		}
	}
	
}
