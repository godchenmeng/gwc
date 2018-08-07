package com.youxing.car.controller;

import java.util.*;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.OrgTree;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.CarService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.utils.other.QueryUtilsWeb;

@Api(value = "org", description = "部门管理")
@Controller
public class OrganizationController {
	@Resource
	private OrganizationService organizationService;
	@Resource
	private UserService userService;

//	@ApiOperation(value = "部门列表", httpMethod = "GET", response = Result.class)
//	@RequestMapping(value = "app/org/list", method = RequestMethod.GET)
//	@ResponseBody
//	public Result list(){
//		return null;
//	}
	
	
	@Resource
	private CarService carService;
	@Resource
	private QueryUtilsWeb queryUtils1;

	/**
	 * 
	 * @author mars
	 * @date 2017年3月22日 下午3:45:06
	 * @Description: TODO(返回机构部门管理页面)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequiresPermissions(value = "org/list")
	@RequestMapping("org/list")
	public String list() {
		return "org/list";
	}

	/**
	 * ====================================== gy
	 * @author mars
	 * @date 2017年3月22日 下午3:58:03
	 * @Description: TODO(组织机构树的获取)
	 * @param @return 设定文件
	 * @return List<Organization> 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取组织机构树" )
	@ApiOperation(value = "获取组织机构树", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:当前登录用户所属机构id}")
	@RequestMapping(value="web/org/car/tree", method = RequestMethod.GET)
	@ResponseBody
	public List<Organization> listAllOrg(HttpServletRequest request) {
		List<Organization> list = organizationService.listAll();
		List<Long> orgIDs = new ArrayList<>();
		String uOrg = request.getParameter("uorg");
		Organization userOrg = new Organization();
		userOrg.setId(Long.parseLong(uOrg));
		HashMap<String, Object> cmap = new HashMap<>();
		List<Map<String, Object>> carList  = carService.findCarByDevice(cmap);
		List<Organization> reslut = new ArrayList<>();
		reslut.add(QueryUtils.queryOrgTree(list,userOrg,orgIDs,carList));
		return reslut;
	}
	/**
	 * ====================================== gy
	 * @author mars
	 * @date 2017年3月22日 下午3:58:03
	 * @Description: TODO(组织机构树的获取)
	 * @param @return 设定文件
	 * @return List<Organization> 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="获取组织机构树" )
    @ApiOperation(value = "获取组织机构树", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:当前登录用户所属机构id}")
	@RequestMapping(value="web/org/tree", method = RequestMethod.GET)
	@ResponseBody
	public List<Organization> listAll(String id, HttpServletRequest request) {
		List<Organization> list = new ArrayList<Organization>();
		List<Organization> nodes = new ArrayList<Organization>();
		//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		User u =new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);
		Long u_org = user.getOrg();
		try {
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("status", Constant.ADD_STATUS);
			queryUtils1.queryByOrg(request, maps);
			//得到parentId
			list = organizationService.listBy(maps);
			Map<Long, Organization> perMap = new HashMap<Long, Organization>();
			for (Organization org : list) {
				
				if (org.getParent() == null) {
					// 获取所有的根节点
					if (org.getId().toString().equals(id)) {
						org.setExpanded(true);
					}
					nodes.add(org);
				}else if(org.getId().toString().equals(u_org.toString())){
					nodes.add(org);
				}
				if ("1".equals(org.getType())) {
					org.setCls("tree_org");
				} else if ("2".equals(org.getType())) {
					org.setCls("tree_dept");
				}
				// 把所有节点放进map
				perMap.put(org.getId(), org);
				
				Long parentId = org.getParent();
				Long current = org.getId();
				// 如果父亲id不为空并且map里面包含了改父亲id就找去该父亲id并且把当前这个permission添加到subs里面去
				if (parentId != null && perMap.containsKey(parentId)) {
					if (current.toString().equals(id)) {
						TreeUtilsWeb.setExpanded(perMap, org);
					}
					perMap.get(parentId).addChildren(org);
				}
			}

			/*for (Organization org : list) {
				Long parentId = org.getParent();
				Long current = org.getId();
				// 如果父亲id不为空并且map里面包含了改父亲id就找去该父亲id并且把当前这个permission添加到subs里面去
				if (parentId != null && perMap.containsKey(parentId)) {
					if (current.toString().equals(id)) {
						TreeUtils.setExpanded(perMap, org);
					}
					perMap.get(parentId).addChildren(org);
				}
			}*/
			if (nodes.size() == 0) {
				nodes.addAll(list);
			}
			return nodes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("查询失败");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年4月12日 上午10:06:43
	 * @Description: TODO(异步加载树)
	 * @param @param id
	 * @param @param request
	 * @param @return 设定文件
	 * @return List<Organization> 返回类型
	 * @throws
	 */
	@LogAnnotation(description="异步获取车辆数" )
 	@ApiOperation(value = "异步获取车辆数", httpMethod = "GET", response = Result.class, notes = "必须的参数： {id:节点id}")
	@RequestMapping(value="web/org/tree/lazy", method = RequestMethod.GET)
	@ResponseBody
	public List<OrgTree> listLazy(String id, HttpServletRequest request) {
		List<Organization> nodes = new ArrayList<Organization>();
		List<OrgTree> tree = new ArrayList<OrgTree>();
		List<Long> oid = new ArrayList<Long>();
		List<Car> lists = new ArrayList<Car>();
		Map<Long, Organization> perMap = new HashMap<Long, Organization>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cmap = new HashMap<String, Object>();
		cmap.put("status", Constant.ADD_STATUS);
		map.put("status", Constant.ADD_STATUS);
		map.put("car_status", Constant.CHECK_STATUS);
		map.put("obd", Constant.BIND_OBD);
		try {
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("status", Constant.ADD_STATUS);
			if (StringUtils.isBlank(id)) {
				queryUtils1.queryByOrg(request, maps);
				if(maps.get("list")==null&&maps.get("id")==null){
					maps.put("root", "1");
				}
			} else {
				oid.add(Long.valueOf(id));
				maps.put("parent", id);
			}
			List<Organization> list = organizationService.listBy(maps);
			for (Organization org : list) {
				Long orgId = org.getId();
				nodes.add(org);
				oid.add(orgId);
				cmap.put("parent", orgId);
				int count = organizationService.countBy(cmap);
				if (count > 0) {
					org.addChildren(new Organization());
				}
				if ("1".equals(org.getType())) {
					org.setCls("tree_org");
				} else if ("2".equals(org.getType())) {
					org.setCls("tree_dept");
				}
				perMap.put(org.getId(), org);
			}
			if (!CollectionUtils.isEmpty(oid)) {
				map.put("list", oid);
				lists = carService.listBy(map);
				for (Car c : lists) {
					Long org_id = c.getOrg();
					if (perMap.containsKey(org_id)) {
						perMap.get(org_id).addChildren(new Organization());
					}
				}
			}
			TreeUtilsWeb.setLazyTree(nodes, tree, lists, id);
			return tree;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("查询失败");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月22日 下午3:48:36
	 * @Description: TODO(查询或者部门的信息)
	 * @param @param id
	 * @param @return 设定文件
	 * @return Response 返回类型 
	 * @throws
	 */
	@LogAnnotation(description="获取机构或者部门信息" )
	@ApiOperation(value = "获取机构或者部门信息", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:机构或部门id}")
	@RequestMapping(value="web/org/id", method = RequestMethod.GET)
	@ResponseBody
	public Response getOrgById(String id) {
		if (StringUtils.isBlank(id)) {
			return new FailedResponse("参数错误!");
		}
		try {
			Organization org = organizationService.findById(Long.parseLong(id));
			return new ObjectResponse<Organization>(org);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误!");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月22日 下午3:49:23
	 * @Description: TODO(添加机构或者部门)
	 * @param @param org
	 * @param @return 设定文件
	 * @return Response 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="添加机构或者部门" )
	@ApiOperation(value = "添加机构或者部门", httpMethod = "POST", response = Result.class, notes = "必须的参数：{org:{机构对象},org_id:父级机构或部门id}")
	@TranslationControl(value = "org/add")
	@RequestMapping(value="web/org/add", method = RequestMethod.POST)
	@ResponseBody
	public Response add(Organization org, HttpServletRequest request, String org_id) {
		if (org == null) {
			return new FailedResponse("参数错误!");
		}
		if (StringUtils.isBlank(org.getName())) {
			return new FailedResponse("参数错误!");
		}
		try {
			
			String type = organizationService.findTypeByIdService(org_id);
			if ("2".equals(type)) {
				return new FailedResponse("部门已是最小单位，请选择机构下进行创建!");
			}else{
				//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
				User u =new User();
				String id1 = request.getParameter("keyid");
				u.setId(Long.parseLong(id1));
				User user = userService.findBy(u);
				org.setCreateby(user.getId());
				org.setStatus(Constant.ADD_STATUS);
				org.setEmergency(org.getEmergency());
				String date = DateUtils.getDateTime();
				org.setCreatedate(DateUtils.parseDate(date));
				if (StringUtils.isNotBlank(org_id)) {
					org.setParent(Long.valueOf(org_id));
				}
				organizationService.add(org);
				return new SuccessResponse();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误!");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月22日 下午5:16:17
	 * @Description: TODO(删除机构或者部门)
	 * @param @param id
	 * @param @return 设定文件
	 * @return Response 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="删除机构或者部门" )
	@ApiOperation(value = "删除机构或者部门", httpMethod = "POST", response = Result.class, notes = "必须的参数：{ id:机构或部门id}")
	@TranslationControl(value = "org/del")
	@RequestMapping(value="web/org/del",method=RequestMethod.POST)
	@ResponseBody
	public Response delete(String id) {
		if (StringUtils.isBlank(id)) {
			return new FailedResponse("参数错误!");
		}
		try {
			List<Long> org_list = new ArrayList<>();
			Organization org = organizationService.findById(Long.parseLong(id));
			if (org != null) {
					org_list = organizationService.getOrganizationUser(org, org.getId());
					if (CollectionUtils.isEmpty(org_list)) {
						org_list.add(org.getId());
					}
				for(Long orgId : org_list){
				Organization og = new Organization();
				og.setId(orgId);
				og.setStatus(Constant.REMOVE_STATUS);
				organizationService.modify(og);
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误!");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月22日 下午5:28:25
	 * @Description: TODO(更新组织机构)
	 * @param @param org
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="更新组织机构" )
	@TranslationControl(value = "org/modify")
	@RequestMapping("web/org/modify")
	@ResponseBody
	public Response update(Organization org) {
		if (org == null) {
			return new FailedResponse("参数错误!");
		}
		if (StringUtils.isBlank(org.getId().toString()) || StringUtils.isBlank(org.getName())) {
			return new FailedResponse("参数错误!");
		}
		try {
			Organization o = organizationService.findById(org.getId());
			if(o==null){
				return new FailedResponse("查询不到该部门");
			}
			if("1".equals(o.getType())){
				String em = org.getEmergency();
				if(StringUtils.isBlank(em)){
					return new FailedResponse("保险救援电话为空！");
				}
				if(!em.equals(o.getEmergency())){
				long id = org.getId();
					List<Long> child = organizationService.getChildren(org.getId());
					if(!CollectionUtils.isEmpty(child)){						
						organizationService.modify(org,child);
					}
					else{
						organizationService.modify(org);
					}
				}else{
					organizationService.modify(org);
				}
			}else{				
				organizationService.modify(org);
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误!");
		}
	}

	
	@LogAnnotation(description="获取机构车辆列表" )
 	@ApiOperation(value = "获取机构车辆列表", httpMethod = "POST", response = Result.class)
	@RequestMapping("app/org/getOrgName")
	@ResponseBody
	public Result getOrgName(String uid, HttpServletRequest request) {
 		
 		HashMap<String, Object> map = new HashMap<String, Object>();
		List<Organization> list = new ArrayList<Organization>();
		List<Organization> nodes = new ArrayList<Organization>();
		List<Car> carList = new ArrayList<Car>();
 		
		if(!"".equals(uid)){
			User u =new User();
			u.setId(Long.parseLong(uid));
			User user = userService.findBy(u);
			Long u_org = user.getOrg();
			
		    list = organizationService.countChildService(u_org);
			List list2=new ArrayList();
			for(Organization o:list){
				long id=o.getId();
				list2.add(id);
			}
			list2.add(u_org);
			map.put("list", list2);
			nodes = organizationService.findOrgNameService(map);
			return  Result.success(nodes);
 		}else{
 			return Result.error();
 					
 		}
	}
 	
 	
	@LogAnnotation(description="获取机构名称和id" )
 /*	@ApiOperation(value = "获取机构名称和id", httpMethod = "GET", response = Result.class)
	@RequestMapping(value="app/org/getOrgNameAndId", method = RequestMethod.GET)
	@ResponseBody
	public Result getOrgNameAndId() {
		List<Organization> list = new ArrayList<Organization>();
			list = organizationService.findOrgNameAndId();
			return  Result.success(list); 
 	
}*/
 	
 	@ApiOperation(value = "获取组织机构树", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:当前登录用户所属机构id}")
	@RequestMapping(value="app/org/getOrgNameAndId", method = RequestMethod.GET)
	@ResponseBody
	public List<Organization> getOrgTree(HttpServletRequest request) {
		List<Organization> list = new ArrayList<Organization>();
		List<Organization> nodes = new ArrayList<Organization>();
		
		User u =new User();
		String id1 = "1";
		String id= "1";
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);
		Long u_org = user.getOrg();
		try {
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("status", Constant.ADD_STATUS);
			//queryUtils1.queryByOrg(request, maps);
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
			//得到parentId
			list = organizationService.listBy(maps);
			Map<Long, Organization> perMap = new HashMap<Long, Organization>();
			for (Organization org : list) {
				
				if (org.getParent() == null) {
					// 获取所有的根节点
					if (org.getId().toString().equals(id)) {
						org.setExpanded(true);
					}
					nodes.add(org);
				}else if(org.getId().toString().equals(u_org.toString())){
					nodes.add(org);
				}
				if ("1".equals(org.getType())) {
					org.setCls("tree_org");
				} else if ("2".equals(org.getType())) {
					org.setCls("tree_dept");
				}
				// 把所有节点放进map
				perMap.put(org.getId(), org);
				
				Long parentId = org.getParent();
				Long current = org.getId();
				// 如果父亲id不为空并且map里面包含了改父亲id就找去该父亲id并且把当前这个permission添加到subs里面去
				if (parentId != null && perMap.containsKey(parentId)) {
					if (current.toString().equals(id)) {
						TreeUtilsWeb.setExpanded(perMap, org);
					}
					perMap.get(parentId).addChildren(org);
				}
			}
			if (nodes.size() == 0) {
				nodes.addAll(list);
			}
			return nodes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("查询失败");
		}
	}
 	
 	
 	
 	
}
