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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.PasswordUtil;
import com.youxing.car.util.Result;

/**
 * @author mars
 * @date 2017年3月16日 上午9:42:24
 */

@Api(value = "user", description = "user")
@Controller
public class UserController {

	@Resource
	private UserService userService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private ControlService controlService;
	@Resource
	private MemberService memberService;

	@RequiresPermissions(value = "user/list")
	@RequestMapping("user/list")
	public String list(HttpServletRequest requset) {
		return "user/list";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月21日 下午12:46:47
	 * @Description: TODO(添加账号)
	 * @param @param user
	 * @param @return 设定文件
	 * @return Object 返回类型
	 * @throws
	 */
	@LogAnnotation(description="账号添加" )
    @ApiOperation(value = "账号添加", httpMethod = "POST", response = Result.class, notes = "必须的参数：{user:{账号对象},login_name:登录名}")
	@RequiresPermissions(value = "web/user/add")
	@RequestMapping(value="web/user/add",method = RequestMethod.POST)
	@ResponseBody
	public Response add(User user,String login_name,HttpServletRequest request) {
		if (user == null || StringUtils.isBlank(user.getMid())||StringUtils.isBlank(login_name)) {
			return new FailedResponse("参数错误");
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		
		try {
		String name = request.getParameter("login_name");
		List list1=new ArrayList();
		List<User> list2=userService.findUserNamesService();
		for(int i=0;i<list2.size();i++){
			User u=list2.get(i);
			String names=u.getName();
			list1.add(names);
		}
		if(list1.contains(name)){
			return new FailedResponse("该账号已经被注册！");
		}else{
			maps.put("mid", user.getMid());
			maps.put("status", Constant.ADD_STATUS);
			List<User> list = userService.listBy(maps);
			if (CollectionUtils.isNotEmpty(list)) {
				return new FailedResponse("该用户已经添加过账号！");
			}
			Map<String, String> map = PasswordUtil.encodePasswordWithRandomSalt(user.getPassword());
			user.setPassword(map.get("encodePassword"));
			user.setSalt(map.get("salt"));
			user.setCreatedate(new Date());
			
			User u =new User();
 			String id = request.getParameter("keyid");
			user.setCreateby(Long.parseLong(id));
			user.setName(login_name);
			userService.add(user);
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
	 * @date 2017年3月21日 下午12:49:25
	 * @Description: TODO(修改用户信息)
	 * @param @param user
	 * @param @return 设定文件
	 * @return Response 返回类型  
	 * @throws
	 */
	@LogAnnotation(description="账号更新" )
    @ApiOperation(value = "账号更新", httpMethod = "POST", response = Result.class, notes = " {user:{账号对象},leaders:是否领导code}")
	@RequiresPermissions(value = "web/user/modify")
	@RequestMapping(value="web/user/modify",method = RequestMethod.POST)
	@ResponseBody
	public Response modify(User user) {
		if (user == null) {
			return new FailedResponse("参数错误");
		}
		try {
			userService.modify(user);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月21日 下午12:50:43
	 * @Description: TODO(删除账号)
	 * @param @param ids
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="账号删除" )
    @ApiOperation(value = "账号删除", httpMethod = "POST", response = Result.class, notes = "必须的参数：{ids:idsArray[账号id数组]}")
	@RequiresPermissions(value = "web/user/delete")
	@RequestMapping(value = "web/user/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
		if (ids.length == 0) {
			return new FailedResponse("参数错误");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("list", ids);
		List<String> mid = userService.listMid(map);
		map.clear();
		map.put("list", mid);
		List<String> list = controlService.listByDriver(map);
		if (!CollectionUtils.isEmpty(list)) {
			return new FailedResponse("删除的账号中有尚未完成任务的司机！");
		}
		
		try {
			for (String id : ids) {
				User user = userService.findById(Long.valueOf(id));
				if (user != null) {
					user.setStatus(Constant.REMOVE_STATUS);
					userService.modify(user);//逻辑删除账号
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
	 * @date 2017年3月21日 下午12:51:31
	 * @Description: TODO(分页查询账号信息)
	 * @param @param limit
	 * @param @param pageIndex
	 * @param @param request
	 * @param @return 设定文件
	 * @return Page 返回类型   
	 * @throws
	 */
	@LogAnnotation(description="账号分页查询" )
    @ApiOperation(value = "账号分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：{limit: 15,pageIndex: 0,mb_name: 用户名,lo_name: 登录名,hide_org: 机构id}")
	@TranslationControl(value = "user/list")
	@RequestMapping(value="web/user/page",method = RequestMethod.POST)
	@ResponseBody
	public Page pageUser(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<User> list1=new ArrayList<User>();
		List<Long> dlist = new ArrayList<>();
		String name = request.getParameter("mb_name");
		String lo_name = request.getParameter("lo_name");
		String hide_org = request.getParameter("hide_org");
		
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		
		map.put("startIdx", pageIndex*limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(name)) {
			map.put("name", name);
		}
		if (StringUtils.isNotBlank(lo_name)) {
			map.put("lo_name", lo_name);
		}
		if (StringUtils.isNotBlank(hide_org)) {
			map.put("org", hide_org);
		}else{			
			//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
			String mid = user.getMid();
			Member member = memberService.findById(Long.parseLong(mid));
			Long org = member.getOrg();
			//map.put("org", org);
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
				if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					map.put("list", org_list);
				}else{
					map.put("org", org);
				}
			}
		}
		/* //User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		    User u1 =new User();
			String id = request.getParameter("keyid");
			u1.setId(Long.parseLong(id));
			User user = userService.findBy(u1);*/
		 Long uid = user.getId();
		 map.put("uid", uid);
		 User user1 = userService.findById(user.getId());
		 User user2 = userService.findById(user1.getCreateby());
		 if(user2 != null){
			 dlist.add(user2.getId());
		 }
		 dlist.add(user.getId());
		 map.put("dlist", dlist);
		 List<User> list = userService.pageByWebService(map);
		 int recodes = userService.countBy(map);
		 return new Page<User>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月21日 下午2:21:54
	 * @Description: TODO(账号和用户添加) 
	 * @param @param user
	 * @param @param member
	 * @param @param login_name
	 * @param @param user_name
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="账号和用户添加" )
    @ApiOperation(value = "账号和用户添加", httpMethod = "POST", response = Result.class, notes = "必须的参数：{user: {账号对象},member: { 用户对象 },login_name: 登录名,user_name: 用户名}")
	@RequiresPermissions(value = "web/member/user/add")
	@RequestMapping(value="web/member/user/add",method=RequestMethod.POST)
	@ResponseBody
	public Response add(User user, Member member, HttpServletRequest request,String login_name, String user_name) {
		if (user == null || member == null || StringUtils.isBlank(login_name)) {
			return new FailedResponse("参数错误");
		}
		try {
			HashMap<String, String> maps = new HashMap<String, String>();
			maps.put("name", login_name.trim());
			maps.put("status",Constant.ADD_STATUS);
			List<User> list = userService.listBy(maps);
			if (CollectionUtils.isNotEmpty(list)) {
				return new FailedResponse("登录名已经存在");
			}
			if (StringUtils.isNotBlank(login_name)) {
				user.setName(login_name);
			}
			//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user1 = userService.findBy(u);
			member.setName(user_name);
			user.setCreatedate(new Date());
			Map<String, String> map = PasswordUtil.encodePasswordWithRandomSalt(user.getPassword());
			user.setPassword(map.get("encodePassword"));
			user.setSalt(map.get("salt"));
			user.setCreateby(user1.getId());
			member.setCreatedate(new Date());
			member.setCreateby(user1.getId());
			userService.add(user, member);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月21日 下午3:02:39
	 * @Description: TODO(登录名验证)
	 * @param @param name
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	@LogAnnotation(description="登录名验证" )
    @ApiOperation(value = "登录名验证",  response = Result.class, notes = "{login_name:登录名}")
	@RequestMapping(value = "web/user/login/name", produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String checkNames(String login_name) {
		if (StringUtils.isBlank(login_name)) {
			return "";
		}
		HashMap<String, String> maps = new HashMap<String, String>();
		maps.put("name", login_name.trim());
		maps.put("status", Constant.ADD_STATUS);
		List<User> list = userService.listBy(maps);
		if (CollectionUtils.isNotEmpty(list)) {
			return "登录名已经存在";
		}
		return "";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年3月21日 下午3:59:48
	 * @Description: TODO(重置密码)
	 * @param @param id
	 * @param @param password 
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="密码修改" )
    @ApiOperation(value = "密码修改",  response = Result.class, httpMethod = "POST", notes = "{id:用户id,password:新密码}")
	@RequiresPermissions(value = "web/user/pwd/reset")
	@RequestMapping(value="web/user/pwd/reset", method = RequestMethod.POST)
	@ResponseBody
	public Response modifyPwd(String id, String password) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(password)) {
			return new FailedResponse("参数错误");
		}
		try {
			User user = userService.findById(Long.parseLong(id));
			if (user != null) {
				Map<String, String> map = PasswordUtil.encodePasswordWithRandomSalt(password);
				user.setPassword(map.get("encodePassword"));
				user.setSalt(map.get("salt"));
				userService.modify(user);
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}
	
	
	/**
	 * 
	 * @Function: UserController::checkExist
	 * @Description: 验证用户是否存在账号
	 * @param mid
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月21日 上午10:50:44 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月21日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="验证用户是否存在账号" )
	@RequestMapping(value="web/user/check/exist",method = RequestMethod.POST)
	@ResponseBody
	public Response checkExist(String mid){
		if (StringUtils.isBlank(mid)) {
			return new FailedResponse("参数错误");
		}
		try {
			HashMap<String, String> maps = new HashMap<String, String>();
			maps.put("mid", mid);
			maps.put("status", Constant.ADD_STATUS);
			List<User> list = userService.listBy(maps);
			if (CollectionUtils.isNotEmpty(list)) {
				return new FailedResponse("该用户已经添加过账号！");
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}		
	}
}
