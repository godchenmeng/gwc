package com.youxing.car.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Permission;
import com.youxing.car.entity.RolePermission;
import com.youxing.car.entity.User;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.PermissionService;
import com.youxing.car.service.RolePermissionService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.CaptchaUtil;
import com.youxing.car.util.Constant;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.util.IdUtil;
import com.youxing.car.util.PasswordUtil;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;
import com.youxing.car.utils.redis.RedisUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author mars
 * @date 2017年5月2日 上午9:27:12
 */
@Api(value = "login", description = "登录")
@Controller
public class LoginController {
	private static final Logger logger = Logger.getLogger(LoginController.class);
	@Resource
	private UserService userService;
	@Resource
	private RedisUtils redisUtils;
	@Resource
	private MemberService memService;
	@Resource
	private RolePermissionService rolePermissionService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private PermissionService permissionService;
	

	/**
	 * 
	 * @author mars
	 * @date 2017年5月2日 上午10:03:32
	 * @Description: TODO(生成验证码)
	 * @param @param req
	 * @param @param res 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@LogAnnotation(description="验证码生成" )
	@ApiOperation(value = "验证码生成", httpMethod = "Get", notes = "authentication返回的header里面 验证验证码需要改字段的值")
	@RequestMapping(value = "app/code/get", method = RequestMethod.GET)
	public void getImageCode(HttpServletRequest req, HttpServletResponse res,String codeToken) {
		String udid = req.getHeader("udid");
		String code = CaptchaUtil.writeRespImg(res, codeToken);
		//String ip = HttpUtils.getIpAddress(req);

		redisUtils.hset(Constant.REDIS_LOGIN_CODE, code + "@" + udid,code,60);
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月2日 上午10:53:42
	 * @Description: TODO(用户登录接口)
	 * @param @param user
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="用户登录" )
	@ApiOperation(value = "用户登录", httpMethod = "POST", response = Result.class, notes = "登录必须的参数：name,password,version,platform,alias(推送的别名)")
	@RequestMapping(value = "app/user/login", method = RequestMethod.POST)
	@ResponseBody
	public Result login(User user, @ApiParam(value = "验证码") String code,
			@ApiParam(value = "验证码返回的identify  ") String identify, HttpServletRequest request) {
		String udid = request.getHeader("udid");
		Result res = Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		if (user == null || StringUtils.isBlank(code) || StringUtils.isBlank(identify)|| StringUtils.isBlank(udid)) {
			return res;
		}
		String name = user.getName();
		String pass = user.getPassword();
		if (StringUtils.isBlank(name) || StringUtils.isBlank(user.getAlias()) || StringUtils.isBlank(pass) || StringUtils.isBlank(user.getPlatform()) || StringUtils.isBlank(user.getVersion())) {
			return res;
		}

		String ip = HttpUtils.getIpAddress(request);
		if (StringUtils.isBlank(ip)) {
			return Result.error("ip地址错误");
		}

		Object obj = redisUtils.hget(Constant.REDIS_LOGIN_CODE, code + "@" + udid);

		if (null == obj || !obj.toString().equals(code)) {
			return Result.instance(ResponseCode.verify_captcha_error.getCode(), ResponseCode.verify_captcha_error.getMsg());
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", Constant.ADD_STATUS);
			map.put("name", name);
			User login = userService.findByMap(map);
			if (login == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			if ("2".equals(login.getApp())) {
				return Result.instance(ResponseCode.forbidden_account.getCode(), ResponseCode.forbidden_account.getMsg());
			}
			String salt = login.getSalt();
			Map<String, String> hmMap = PasswordUtil.encodePasswordWithConstomSalt(pass, salt);
			String pwd = hmMap.get("encodePassword");
			// 密码错误
			if (!pwd.equals(login.getPassword())) {
				return Result.instance(ResponseCode.password_incorrect.getCode(), ResponseCode.password_incorrect.getMsg());
			}
			Organization org = organizationService.findByNameService(name);
			// 修改登录用户信息
			Long id = login.getId();
			User u = new User();
			u.setId(id);
			u.setLogin(new Date());
			u.setVersion(user.getVersion());
			u.setPlatform(user.getPlatform()); 
			u.setAlias(user.getAlias());
			userService.modify(u);
			
			String token = IdUtil.getAppToken();
			login.setToken(token);
			
			redisUtils.hset(Constant.REDIS_LOGIN_APP_INFO, login.getId().toString() + "@" + udid, token + "@" + login.getOrg(),604800);//登录信息最多保留一周

			if (org != null) {
				login.setEmergency(org.getEmergency());
				login.setTel(org.getMobile());
				login.setOrg_name(org.getName());
				login.setCode(org.getCode());
				login.setDriver(org.getDriver());
				login.setSex(org.getSex());
				login.setUser_name(org.getUser_name());
				login.setPath(org.getPath());
				login.setAlias(user.getAlias());
			}
			Long role = login.getRole();
			
			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("key", "app");
			maps.put("rid", role);
			
			List<Permission> perList = permissionService.getTranslations(maps);
			login.setPerList(perList);
			return Result.success(login);
		} catch (Exception e) {
			logger.error("app/user/login", e);
			return Result.error();
		}
	}
	@LogAnnotation(description="用户无验证码登录" )
	@ApiOperation(value = "用户无验证码登录", httpMethod = "POST", response = Result.class, notes = "登录必须的参数：name,password,version,platform,alias(推送的别名)")
	@RequestMapping(value = "app/user/login/nocode", method = RequestMethod.POST)
	@ResponseBody
	public Result loginNocode(User user,HttpServletRequest request) {
		if (user == null) {
			return Result.error();
		}

		String ip = HttpUtils.getIpAddress(request);
		String name = user.getName();
		String pass = user.getPassword();
		if (StringUtils.isBlank(name) || StringUtils.isBlank(pass) || StringUtils.isBlank(user.getPlatform()) || StringUtils.isBlank(user.getVersion()) || StringUtils.isBlank(user.getAlias()) ) {
			return Result.error();
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", Constant.ADD_STATUS);
			map.put("name", name);
			User login = userService.findByMap(map);
			if (login == null) {
				return Result.error();
			}
			if ("2".equals(login.getApp())) {
				return Result.error();
			}
			String salt = login.getSalt();
			Map<String, String> hmMap = PasswordUtil.encodePasswordWithConstomSalt(pass, salt);
			String pwd = hmMap.get("encodePassword");
			// 密码错误
			if (!pwd.equals(login.getPassword())) {
				return Result.instance(ResponseCode.password_incorrect.getCode(), ResponseCode.password_incorrect.getMsg());
			}
			Organization org = organizationService.findByNameService(name);
			// 修改登录用户信息
			Long id = login.getId();
			
			//String token = IdUtil.getAppToken();
			//login.setToken(token);

			//redisUtils.hset(Constant.REDIS_LOGIN_APP_INFO, login.getId().toString() + "@" + ip, token + "@" + login.getOrg(),604800);//登录信息最多保留一周
			
			if (org != null) {
				login.setEmergency(org.getEmergency());
				login.setTel(org.getMobile());
				login.setOrg_name(org.getName());
				login.setCode(org.getCode());
				login.setDriver(org.getDriver());
				login.setSex(org.getSex());
				login.setUser_name(org.getUser_name());
				login.setPath(org.getPath());
			}
			Long role = login.getRole();

			return Result.success(login);
		} catch (Exception e) {
			logger.error("app/user/login/nocode", e);
			return Result.error();
		}
	}	

	@LogAnnotation(description="修改密码" )
	@ApiOperation(value = "修改密码", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/user/pwd", method = RequestMethod.POST)
	@ResponseBody
	public Result updatePwd(@ApiParam(required = true, value = "用户id") Long uid, @ApiParam(required = true, value = "用户旧密码") String password, @ApiParam(required = true, value = "新密码") String newpwd) {
		try {
			if (uid == null || StringUtils.isBlank(password) || StringUtils.isBlank(newpwd)) {
				Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			String salt = user.getSalt();
			Map<String, String> hmMap = PasswordUtil.encodePasswordWithConstomSalt(password, salt);
			String pwd = hmMap.get("encodePassword");
			// 密码错误
			if (!pwd.equals(user.getPassword())) {
				return Result.instance(ResponseCode.password_incorrect.getCode(), ResponseCode.password_incorrect.getMsg());
			}
			User up = new User();
			up.setId(uid);
			Map<String, String> maps = PasswordUtil.encodePasswordWithRandomSalt(newpwd);
			up.setPassword(maps.get("encodePassword"));
			up.setSalt(maps.get("salt"));
			userService.modify(up);
			return Result.success();
		} catch (Exception e) {
			logger.error("app/user/pwd", e);
			return Result.error();
		}
	}
	
	
	
	@LogAnnotation(description="用户登录" )
	@ApiOperation(value = "用户登录", httpMethod = "POST", response = Result.class, notes = "登录必须的参数：name,password,jcaptchaCode")
	@RequestMapping(value = "web/user/login", method = RequestMethod.POST)
	@ResponseBody
	public Response userLogin(Model model, String username, String password,  HttpServletRequest request) {

		String ip = HttpUtils.getIpAddress(request);
		if (StringUtils.isBlank(ip)) {
			return new FailedResponse("ip地址错误！");
		}
		try {
		User login = new User();
		login.setName(username);
		login.setStatus("1");
		login = userService.findBy(login);
		if(login == null){
			return new FailedResponse("用户名或密码错误！");
		}
		if(!"1".equals(login.getWeb())){
			 return new FailedResponse("该账号没有登陆web的权限！");
		}
		String  salt = login.getSalt();
		String ps = URLDecoder.decode(password,"UTF-8");
		Map<String, String> hmMap = PasswordUtil.encodePasswordWithConstomSalt(ps,salt);
		String pwd = hmMap.get("encodePassword");
		if(!login.getPassword().equals(pwd)){
			 return new FailedResponse("用户名或密码错误！");
		}
		
			String token = IdUtil.getAppToken();
			redisUtils.hset(Constant.REDIS_LOGIN_WEB_INFO, login.getId().toString(), token + "@" + login.getOrg() + "@" + ip + "@" + System.currentTimeMillis(),604800);//登录信息最多保留一周

			if (StringUtils.isNotBlank(token) && login.getId() != null) {
				login.setToken(token);
				login.setId(login.getId());
				login.setTel(login.getTel());
				Map<String, Object> map = new HashMap<>();
				map.put("ymid", login.getMid());
				Map orgLogo = memService.getOrgLogo(map);
				login.setOrg(Long.parseLong(orgLogo.get("org").toString()));
				if(orgLogo.get("logo") != null){
				login.setLogo(orgLogo.get("logo").toString());
				}
				return new ObjectResponse<User>(login);
			} else {
				return new FailedResponse("登录失败！");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new FailedResponse("登录失败！");
	}
	

	/**
	 * 
	 * @author mars
	 * @date 2017年4月5日 下午7:15:18
	 * @Description: TODO(跳转到系统主页)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取个人权限模块列表" )
	@ApiOperation(value = "获取个人权限模块列表", httpMethod = "GET", response = Result.class)
	@RequestMapping(value="web/user/permission",method=RequestMethod.GET)
	@ResponseBody
	public Response index(HttpServletRequest request) {
		// 根据用户id查找菜单权限
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long rid = user.getRole();
		List<Permission> list = treePermission(rid);
		return new ListResponse<Permission>(list);
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年4月6日 上午11:45:43
	 * @Description: TODO(构造树形结构)
	 * @param @param id
	 * @param @return 设定文件
	 * @return List<Permission> 返回类型
	 * @throws
	 */
	public List<Permission> treePermission(Long id) {
		List<Permission> list = rolePermissionService.getRolePermission(id);
		List<Permission> nodes = new ArrayList<Permission>();
		Map<Long, Permission> perMap = new HashMap<Long, Permission>();
		for (Permission permission : list) {
			if (permission.getParent() == null) {
				// 获取所有的根节点
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
		return nodes;
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年4月6日 下午2:13:41
	 * @Description: TODO(退出登录)
	 * @param @param request
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@LogAnnotation(description="用户退出登陆" )
	@ApiOperation(value = "退出登录", httpMethod = "GET", response = Result.class)
	@RequestMapping(value="web/user/out",method=RequestMethod.GET)
	@ResponseBody
	public Response logout(HttpServletRequest request) {
		return new SuccessResponse();
	}
	
	
	
	@LogAnnotation(description="定位工具用户登录" )
	@ApiOperation(value = "定位工具用户登录", httpMethod = "POST", response = Result.class, notes = "登录必须的参数：name,password,version,platform,alias(推送的别名)")
	@RequestMapping(value = "app/gps/user/login", method = RequestMethod.POST)
	@ResponseBody
	public Result gpsLogin(User user, @ApiParam(value = "验证码") String code, @ApiParam(value = "验证码返回的identify  ") String identify, HttpServletRequest request) {
		Result res = Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
		if (user == null || StringUtils.isBlank(code) || StringUtils.isBlank(identify)) {
			return res;
		}
		String name = user.getName();
		String pass = user.getPassword();
		if (StringUtils.isBlank(name) || StringUtils.isBlank(user.getAlias()) || StringUtils.isBlank(pass) || StringUtils.isBlank(user.getPlatform()) || StringUtils.isBlank(user.getVersion())) {
			return res;
		}

		String ip = HttpUtils.getIpAddress(request);
		if (StringUtils.isBlank(ip)) {
			return Result.error("ip地址错误");
		}
		String uid = DigestUtils.md5Hex(ip);
		//String cache_code = captchaCache.getCode(identify);

		Object obj = redisUtils.hget(Constant.REDIS_LOGIN_CODE, code + "@" + ip);
		 

		if (null == obj || !obj.toString().equals(code)) {
			return Result.instance(ResponseCode.verify_captcha_error.getCode(), ResponseCode.verify_captcha_error.getMsg());
		}

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", Constant.ADD_STATUS);
			map.put("name", name);
			User login = userService.findByMap(map);
			if (login == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			if ("2".equals(login.getApp())) {
				return Result.instance(ResponseCode.forbidden_account.getCode(), ResponseCode.forbidden_account.getMsg());
			}
			String salt = login.getSalt();
			Map<String, String> hmMap = PasswordUtil.encodePasswordWithConstomSalt(pass, salt);
			String pwd = hmMap.get("encodePassword");
			// 密码错误
			if (!pwd.equals(login.getPassword())) {
				return Result.instance(ResponseCode.password_incorrect.getCode(), ResponseCode.password_incorrect.getMsg());
			}
			Organization org = organizationService.findByNameService(name);
			Member m=new Member();
			m.setName(name);
			Member mem = memService.findBy(m);
			// 修改登录用户信息
			Long id = login.getId();
			
			//查找角色id   用于查找角色所拥有的权限
			User uu =  userService.findById(id);
			Long ro = uu.getRole();
			HashMap<String, Object> maps = new HashMap<String, Object>();
			List<Object> mlist = new ArrayList<Object>();
			maps.put("rid", ro);
			List<RolePermission> rlist = rolePermissionService.listBy(maps);
			for(RolePermission rm : rlist){
				long pid = rm.getPid();
				mlist.add(pid);
			}
			
			if(!mlist.contains(118L)){
				return Result.error("没有区域新增权限，请到后台进行授权！");
			}
			User u = new User();
			u.setId(id);
			u.setLogin(new Date());
			u.setVersion(user.getVersion());
			u.setPlatform(user.getPlatform()); 
			u.setAlias(user.getAlias());
			userService.modify(u);
			
			String token = IdUtil.getAppToken();
			login.setToken(token);

			redisUtils.hset(Constant.REDIS_LOGIN_APP_INFO, login.getId().toString() + "@" + ip, token + "@" + login.getOrg(),86400);//登录信息最多保留1天

			if (org != null) {
				login.setEmergency(org.getEmergency());
				login.setTel(org.getMobile());
				login.setOrg_name(org.getName());
				login.setCode(org.getCode());
				login.setDriver(org.getDriver());
				login.setSex(org.getSex());
				login.setUser_name(org.getUser_name());
				login.setPath(org.getPath());
			}
			Long role = login.getRole();

			return Result.success(login);
		} catch (Exception e) {
			logger.error("app/gps/user/login", e);
			return Result.error();
		}
	}
	
	
	
}
