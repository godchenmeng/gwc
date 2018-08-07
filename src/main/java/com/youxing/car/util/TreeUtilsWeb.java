package com.youxing.car.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.youxing.car.entity.Car;
import com.youxing.car.entity.OrgTree;
import com.youxing.car.entity.Organization;

/**   
 * @author mars   
 * @date 2017年3月27日 下午9:39:08 
 */
public class TreeUtilsWeb {
    /**
     * 
    * @author mars   
    * @date 2017年3月27日 下午9:39:36 
    * @Description: TODO(设置节点展开) 
    * @param @param perMap
    * @param @param org    设定文件 
    * @return void    返回类型 
    * @throws
     */
	public static void setExpanded(Map<Long, Organization> perMap,Organization org){
		Long parentId = org.getParent();
		Organization orgs = perMap.get(parentId);
    	if(orgs!=null){
    		orgs.setExpanded(true);
    		if(orgs.getParent()!=null){
    			setExpanded(perMap, orgs);
    		}
    	}
    }
	/**
	 * 
	* @author mars   
	* @date 2017年4月12日 上午10:19:20 
	* @Description: TODO(判断树是否有叶子节点) 
	* @param @param list
	* @param @param tree    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public static void setLazyTree(List<Organization> list,List<OrgTree> tree,List<Car> car,String id){
		for(Organization org:list){
			OrgTree ot = new OrgTree();
			ot.setId(org.getId());
			ot.setText(org.getName());
			if(org.getChildren().size()>0){
				ot.setLeaf(false);
			}else{
				ot.setLeaf(true);
			}
			ot.setCls(org.getCls());
			ot.setType("1");
			tree.add(ot);
		}
		if(StringUtils.isNotBlank(id)){
			Long ids = Long.valueOf(id);
			for(Car c:car){
				if(c.getOrg()==ids){				
					OrgTree ot = new OrgTree();
					ot.setId(c.getId());
					ot.setLeaf(true);
					ot.setText(c.getCar_no());
					ot.setType("2");
					tree.add(ot);
				}
			}
		}
	}
	
}


