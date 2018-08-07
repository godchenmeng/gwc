package com.youxing.car.controller;

import com.youxing.car.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Permission;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.entity.User;
import com.youxing.car.service.PermissionService;
import com.youxing.car.service.RolePermissionService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;

@Api(value = "promiss", description = "promiss")
@Controller
public class PermissionController {
     @Resource
     private PermissionService permissionService;
     @Resource
     UserService userService;
     @Resource
     private RolePermissionService rolePermissionService;

    @LogAnnotation(description="获取权限列表，默认不展开" )
    @ApiOperation(value = "获取权限列表，默认不展开", httpMethod = "GET", response = Result.class)
 	@RequestMapping(value="web/permission/list",method=RequestMethod.GET)
 	@ResponseBody
 	public List<Permission> listAll(HttpServletRequest request) {
 		List<Permission> list = new ArrayList<Permission>();
 		List<Permission> nodes  = new ArrayList<Permission>();
 		try {
 			HashMap<String, Object> maps = new HashMap<String, Object>();
 			maps.put("status", "1");
 			//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
 			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user = userService.findBy(u);
 			
 			HashMap<String, Long> map = new HashMap<String, Long>();
 			map.put("rid", user.getRole());
 			List<RolePermission> rp_list = rolePermissionService.listBy(map);
 			maps.put("list", rp_list);
 			list = permissionService.listBy(maps);
 			Map<Long, Permission> perMap = new HashMap<Long, Permission>();
 			for (Permission permission : list) {
 				if (permission.getParent() == null) {
 					// 获取所有的根节点
 					nodes .add(permission);
 				}
 				// 把所有节点放进map
 				perMap.put(permission.getId(), permission);
 			}
 			for (Permission permission : list) {
 				Long parentId = permission.getParent();
 				// 如果父亲id不为空并且map里面包含了改父亲id就找去该父亲id并且把当前这个permission添加到subs里面去
 				if (parentId != null && perMap.containsKey(parentId)) {
 					perMap.get(parentId).addChildren(permission);
 				}
 			}
 			return nodes ;
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new RuntimeException("查询失败");
 		}
 	}
    
    
    /**
     * ===========================================gy
     * @param request
     * @return
     */
    @LogAnnotation(description="获取权限列表，默认展开" )
    @ApiOperation(value = "获取权限数，默认展开", httpMethod = "GET", response = Result.class)
 	@RequestMapping(value="web/permission/expanded" ,method=RequestMethod.GET)
 	@ResponseBody
 	public List<Permission> listAllExpanded(HttpServletRequest request) {
 		List<Permission> list = new ArrayList<Permission>();
 		List<Permission> nodes  = new ArrayList<Permission>();
 		try {
 			HashMap<String, Object> maps = new HashMap<String, Object>();
 			maps.put("status", Constant.ADD_STATUS);
 			//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
 			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user = userService.findBy(u);
 			HashMap<String, Long> map = new HashMap<String, Long>();
 			map.put("rid", user.getRole());
 			List<RolePermission> rp_list = rolePermissionService.listBy(map);
 			maps.put("list", rp_list);
 			list = permissionService.listBy(maps);
 			Map<Long, Permission> perMap = new HashMap<Long, Permission>();
 			for (Permission permission : list) {
 				if (permission.getParent() == null) {
 					// 获取所有的根节点
 					permission.setExpanded(true);
 					nodes.add(permission);
 				}
 				// 把所有节点放进map
 				perMap.put(permission.getId(), permission);
 			}
 			for (Permission permission : list) {
 				Long parentId = permission.getParent();
 				// 如果父亲id不为空并且map里面包含了改父亲id就找去该父亲id并且把当前这个permission添加到subs里面去
 				if (parentId != null && perMap.containsKey(parentId)) {
 					perMap.get(parentId).addChildren(permission);
 				}
 			}
 			return nodes ;
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new RuntimeException("查询失败");
 		}
 	}
    
    
    @LogAnnotation(description="app获取权限模块" )
    @ApiOperation(value = "app获取权限模块", httpMethod = "GET", response = Result.class)
 	@RequestMapping(value="app/getPermission",method=RequestMethod.GET)
 	@ResponseBody
 	public Result getPermission(String uid) {
 			User u =new User();
 			u.setId(Long.parseLong(uid));
 			User user = userService.findBy(u);
 			if(user == null){
 				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
 			}
 			
 			HashMap<String, Object> map = new HashMap<String, Object>();
 			map.put("rid", user.getRole());
 			map.put("key", "app");
 			List<Permission> rp_list = permissionService.getTranslations(map);
 			return Result.success(rp_list);
 	}
    
}
