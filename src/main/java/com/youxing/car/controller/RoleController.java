package com.youxing.car.controller;

import com.youxing.car.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Role;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.RolePermissionService;
import com.youxing.car.service.RoleService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Result;
@Api(value = "role", description = "角色管理")
@Controller
public class RoleController {
     @Resource
     private RoleService roleService;
     @Resource
     private UserService userService;
     @Resource
     private RolePermissionService rolepermissionService;
     @Resource
  	 private OrganizationService organizationService;
     
     @RequestMapping("role/list")
     public String list(){
    	 return "user/role";
     }
     
     /**
      * 
     * @author mars   
     * @date 2017年3月21日 下午4:15:05 
     * @Description: TODO(添加角色) 
     * @param @param role
     * @param @return    设定文件 
     * @return Response    返回类型 
     * @throws
      */
     @LogAnnotation(description="添加角色" )
     @ApiOperation(value = "添加角色", httpMethod = "POST", response = Result.class, notes = "必须的参数：role:{新增角色对象},pid:pidsArray[权限id数组]")
 	 @TranslationControl(value = "role/add")
     @RequestMapping(value = "web/role/add", method = RequestMethod.POST)
     @ResponseBody
 	public Response add(Role role ,HttpServletRequest request,@RequestParam(value="pid[]")String[] pid) {
 		if (role == null) {
 			return new FailedResponse("参数错误");
 		}
 		try {
 			//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
 			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user = userService.findBy(u);
 			String name=request.getParameter("name");
 			List<Object> list1=new ArrayList<Object> ();
 			List<Role> list= roleService.findRoleNames();
 			for(int i=0;i<list.size();i++){
 				Role ro=list.get(i);
 				String names=ro.getName();
 				list1.add(names);
 			}
 			if(list1.contains(name)){
 				return new FailedResponse("该角色已经存在！");
 			}else{
 				role.setCreatedate(new Date());
 	 			// 从登陆用户的session里面取
 	 			role.setStatus(Constant.ADD_STATUS);
 	 			role.setCreateby(user.getId());
 	 			role.setOrg(user.getOrg());
 	 			roleService.add(role,pid);
 	 			return new SuccessResponse();
 			}
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
 	}

 	/**
 	 * 
 	* @author mars   
 	* @date 2017年3月21日 下午4:15:50 
 	* @Description: TODO(修改角色) 
 	* @param @param role
 	* @param @return    设定文件 
 	* @return Response    返回类型  
 	* @throws
 	 */
     @LogAnnotation(description="角色更新" )
     @ApiOperation(value = "角色更新", httpMethod = "POST", response = Result.class, notes = "必须的参数： role:{编辑角色对象},pid:pidsArray[权限id数组]")
     @TranslationControl(value = "role/update")
     @RequestMapping(value = "web/role/update", method = RequestMethod.POST)
 	 @ResponseBody
 	public Response update(Role role,@RequestParam(value="pid[]")String[] pid,HttpServletRequest request) {
 		if (role == null) {
 			return new FailedResponse("参数错误");
 		}
 		try {
 			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user = userService.findBy(u);
 			
 			String name = role.getName();
 			
 			Role rou = roleService.findById(role.getId());
 			String rname  = rou.getName();
 			
 			List<Object> list1=new ArrayList<Object> ();
 			List<Role> list= roleService.findRoleNames();
 			for(int i=0;i<list.size();i++){
 				Role ro=list.get(i);
 				String names=ro.getName();
 				list1.add(names);
 			}
 			if(list1.contains(name) && !name.equals(rname)){
 				return new FailedResponse("该角色已经存在！");
 			}else{
 				roleService.modify(role,pid,user.getId());
 			}
 			return new SuccessResponse();
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
 	}

 	

 	/**
 	 * 
 	 * @author mars
 	 * @date 2017年3月20日 下午3:13:54
 	 * @Description: TODO(分页获取数据)
 	 * @param @param limit
 	 * @param @param pageIndex 
 	 * @param @param request
 	 * @param @return 设定文件
 	 * @return Page 返回类型
 	 * @throws
 	 */
    @LogAnnotation(description="角色分页查询" )
 	@SuppressWarnings("rawtypes")
    @ApiOperation(value = "角色分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数： start:0,limit:15,pageIndex:0,name:角色名称")
    @TranslationControl(value = "role/list")
    @RequestMapping(value = "web/role/page", method = RequestMethod.POST)
 	@ResponseBody
 	public Page pageRole(int limit, int pageIndex, HttpServletRequest request) {
 		HashMap<String, Object> map = new HashMap<String, Object>();
 		map.put("startIdx", pageIndex*limit);
 		map.put("limit", limit);
 		map.put("status", Constant.ADD_STATUS);
 		String name = request.getParameter("name");
 		if(StringUtils.isNotBlank(name)){
 			map.put("name", name);
 		}
 		long id=Long.parseLong(request.getParameter("keyid"));
 		User user = userService.findById(id);
		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
 		List<Role> list = roleService.pageBy(map);
 		int recodes = roleService.countBy(map);
 		return new Page<Role>(list, recodes, limit);
 	}

 	/**
 	 * 
 	 * @author mars
 	 * @date 2017年3月20日 下午3:13:41
 	 * @Description: TODO(删除--修改状态)
 	 * @param @param ids
 	 * @param @return 设定文件
 	 * @return Response 返回类型 
 	 * @throws
 	 */
    @LogAnnotation(description="角色删除" )
    @ApiOperation(value = "角色删除", httpMethod = "POST", response = Result.class, notes = "必须的参数： {ids:idsArray[角色id数组]}")
    @TranslationControl(value = "role/delete")
    @RequestMapping(value = "web/role/delete", method = RequestMethod.POST)
 	@ResponseBody
 	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
 		if (ids.length == 0) {
 			return new FailedResponse("参数错误");
 		}
 		try {
 			for (String id : ids) {
 				Role mb = roleService.findById(Long.parseLong(id));
 				if (mb != null) {
 					Role mbs = new Role();
 					mbs.setId(mb.getId());
 					mbs.setStatus(Constant.REMOVE_STATUS);
 					roleService.modify(mbs);
 				}
 			}
 			return new SuccessResponse();
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
 	} 
 	/**
 	 * 
 	* @author mars   
 	* @date 2017年3月22日 上午10:52:30 
 	* @Description: TODO(获取角色下面的权限) 
 	* @param @param rid
 	* @param @return    设定文件 
 	* @return Response    返回类型 
 	* @throws
 	 */
    @LogAnnotation(description="获取角色权限列表" )
    @ApiOperation(value = "获取角色权限列表", httpMethod = "POST", response = Result.class, notes = "必须的参数： {ids:idsArray[角色id数组]}")
 	@RequestMapping(value="web/role/permission",method=RequestMethod.POST)
 	@ResponseBody
 	public Response getRolePermission(String rid){
 		if(StringUtils.isBlank(rid)){
 			return new FailedResponse("参数错误");
 		}
 		try {
 			HashMap<String, Object> map = new HashMap<String, Object>();
 			map.put("rid", rid);
 			List<RolePermission> list = rolepermissionService.listBy(map);
 			return new ListResponse<RolePermission>(list);
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
 	}
 	/**
 	 * ============================================ gy
 	* @author mars   
 	* @date 2017年4月5日 下午6:30:08 
 	* @Description: TODO(机构id下面的角色) 
 	* @param @return    设定文件 
 	* @return List<Role>    返回类型 
 	* @throws
 	 */
    @LogAnnotation(description="获取角色列表" )
    @ApiOperation(value = "获取角色列表", httpMethod = "GET", response = Result.class, notes = "必须的参数： {ids:idsArray[角色id数组]}")
 	@RequestMapping("web/org/role")
 	@ResponseBody
 	public List<Role> getOrgRole(HttpServletRequest request){
 		Map<String,Object> map = new HashMap<String, Object>();
 		map.put("status", Constant.ADD_STATUS);
 		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
 		List<Role> role = roleService.listBy(map);
 		return role;
 	}
    
    @LogAnnotation(description="获取当前登录用户，所有非菜单（操作权限）权限列表" )
    @ApiOperation(value = "获取当前登录用户，所有非菜单（操作权限）权限列表", httpMethod = "GET", response = Result.class, notes = "必须的参数： {ids:idsArray[角色id数组]}")
 	@RequestMapping("web/user/operation/permission")
 	@ResponseBody
 	public Response getRolePermission(HttpServletRequest request){
 		Map<String,Object> map = new HashMap<String, Object>();
 		map.put("status", Constant.ADD_STATUS);
 		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long rid  = user.getRole();
 		List<Object> list = rolepermissionService.getRolePermissionUrlList(rid);
 		return new ListResponse<Object>(list);
 	}
}
