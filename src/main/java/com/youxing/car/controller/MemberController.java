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

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.dao.DriverDao;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Result;

@Api(value = "member", description = "member")
@Controller
public class MemberController {
	@Resource
	private MemberService memberService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private ControlService controlService;
	@Resource
	private DriverDao driverDao;
	@Resource
	private UserService userService;

	/**
	 * 
	 * @author mars @date 2017年4月6日 上午10:59:08 @Description:
	 * TODO(跳转到用户管理页面) @param @return 设定文件 @return String 返回类型 @throws
	 */
	@RequiresPermissions(value = "member/list")
	@RequestMapping("/member/list")
	public String list() {
		return "user/member";
	}

	/**
	 * 
	 * @author mars @date 2017年3月17日 上午11:22:18 @Description:
	 * TODO(添加用户) @param @param member @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "添加用户")
	@ApiOperation(value = "添加用户", httpMethod = "POST", response = Result.class, notes = "必须的参数：{member:{用户对象}}")
	@TranslationControl(value = "member/add")
	@RequestMapping(value = "web/member/add", method = RequestMethod.POST)
	@ResponseBody
	public Response addMemeber(Member member, HttpServletRequest request) {
		if (member == null) {
			return new FailedResponse("参数错误");
		}
		try {
			member.setCreatedate(new Date());
			// User user = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);
			User u = new User();
			String id = request.getParameter("keyid");
			u.setId(Long.parseLong(id));
			User user = userService.findBy(u);
			// 从登陆用户的session里面取
			// member.setStatus(Constant.ADD_STATUS);
			member.setCreateby(user.getId());
			memberService.add(member);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年3月17日 上午11:23:20 @Description:
	 * TODO(修改用户) @param @param member @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "修改用户")
	@ApiOperation(value = "修改用户", httpMethod = "POST", response = Result.class, notes = "必须的参数：{member:{用户对象}}")
	@TranslationControl(value = "member/update")
	@RequestMapping(value = "web/member/update", method = RequestMethod.POST)
	@ResponseBody
	public Response update(Member member) {
		if (member == null) {
			return new FailedResponse("参数错误");
		}
		try {
			memberService.modify(member);
			//修改用户机构的同时修改账号机构
			Long org = member.getOrg();
			if(org != null){
				User user = new User();
				user.setMid(member.getId().toString());
				User user2 = userService.findBy(user);
				user2.setOrg(member.getOrg());
				userService.modify(user2);
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年3月17日 下午1:10:18 @Description:
	 * TODO(查询用户) @param @param id @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "查询用户")
	@RequestMapping(value = "web/member/get", method = RequestMethod.GET)
	@ResponseBody
	public Response findMemeber(String id) {
		if (StringUtils.isBlank(id)) {
			return new FailedResponse("参数错误");
		}
		try {
			Member mb = memberService.findById(id);
			if (mb == null) {
				return new FailedResponse("查询不到该用户");
			}
			return new ObjectResponse<Member>(mb);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}

	/**
	 * 
	 * @author mars @date 2017年3月20日 下午3:13:54 @Description:
	 * TODO(分页获取数据) @param @param limit @param @param pageIndex @param @param
	 * request @param @return 设定文件 @return Page 返回类型 @throws
	 */
	@LogAnnotation(description = "用户分页查询")
	@ApiOperation(value = "用户分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：{limit:15,pageIndex:0,name:用户名,sex:性别,code:工号}")
	@TranslationControl(value = "member/list")
	@RequestMapping(value = "web/member/page", method = RequestMethod.POST)
	@ResponseBody
	public Page pageMember(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String code = request.getParameter("code");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		if (StringUtils.isNotBlank(name)) {
			map.put("name", name);
		}
		if (StringUtils.isNotBlank(sex) && !sex.equals("-1")) {
			map.put("sex", sex);
		}
		if (StringUtils.isNotBlank(code)) {
			map.put("code", code);
		}
		if (StringUtils.isNotBlank(startDate)) {
			map.put("startdate", startDate + " 00:00:00");
		}
		if (StringUtils.isNotBlank(endDate)) {
			map.put("enddate", endDate + " 23:59:59");
		}
		// User user = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u = new User();
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

		List<Long> mem_list = new ArrayList<Long>();
		User us = userService.findById(user.getId());
		mem_list.add(Long.parseLong(us.getMid()));
		User mid = userService.findById(us.getCreateby());
		if (mid != null) {
			mem_list.add(Long.parseLong(mid.getMid()));
		}
		map.put("dlist", mem_list);
		List<Member> list = memberService.pageByWeb(map);
		int recodes = memberService.countBy(map);
		return new Page<Member>(list, recodes, limit);
	}

	/**
	 * 
	 * @author mars @date 2017年3月20日 下午3:13:41 @Description:
	 * TODO(删除--修改状态) @param @param ids @param @return 设定文件 @return Response
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "删除用户")
	@ApiOperation(value = "删除用户", httpMethod = "POST", response = Result.class, notes = "必须的参数：{ids:idsArray[用户id数组]}")
	@TranslationControl(value = "member/delete")
	@RequestMapping(value = "web/member/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
		if (ids.length == 0) {
			return new FailedResponse("参数错误");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("list", ids);
		List<String> list = controlService.listByDriver(map);
		if (!CollectionUtils.isEmpty(list)) {
			return new FailedResponse("删除的用户中有尚未完成任务的司机！");
		}
		try {
			for (String id : ids) {
				Member mb = memberService.findById(Long.parseLong(id));
				if (mb != null) {
					mb.setStatus(Constant.REMOVE_STATUS);
					memberService.modify(mb, Long.parseLong(id));// 逻辑删除用户和账号
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
	 * @author mars @date 2017年5月15日 上午11:13:52 @Description:
	 * TODO(用户列表中是驾驶员的) @param @param limit @param @param
	 * pageIndex @param @param request @param @return 设定文件 @return Page
	 * 返回类型 @throws
	 */
	@LogAnnotation(description = "用户列表中的驾驶员列表")
	@ApiOperation(value = "用户列表中的驾驶员列表", httpMethod = "POST", response = Result.class, notes = "必须的参数：")
	@RequestMapping(value = "web/member/driver", method = RequestMethod.POST)
	@ResponseBody
	public Page pageDriver(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String name = request.getParameter("name");
		if (StringUtils.isNotBlank(name)) {
			map.put("name", name);
		}
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		List<String> dr = driverDao.listDr();
		if (!CollectionUtils.isEmpty(dr)) {
			map.put("dlist", dr);
		}
		User u = new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Member member = memberService.findById(Long.parseLong(user.getMid()));
		Long org = member.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				map.put("list", org_list);
			} else {
				map.put("org", org);
			}
		}
		List<Member> list = memberService.pageBy(map);
		int recodes = memberService.countDriverBy(map);
		return new Page<Member>(list, recodes, limit);
	}

	@LogAnnotation(description = "通过用户名获取成员对象")
	@ApiOperation(value = "通过用户名获取成员对象", httpMethod = "GET", response = Result.class, notes = "必须的参数：")
	@RequestMapping(value = "web/get/member", method = RequestMethod.GET)
	@ResponseBody
	public Response getMember(String use_name, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (use_name != null && !"".equals(use_name)) {
			map.put("use_name", use_name);
			List<Member> list = memberService.getMemberService(map);
			if (list != null) {
				return new ListResponse<Member>(list);
			} else {
				return new FailedResponse("用户名不存在");
			}
		} else {
			return new FailedResponse("参数错误");
		}
	}

	@LogAnnotation(description = "电话号码是否属于系统")
	@ApiOperation(value = "电话号码是否属于系统", httpMethod = "GET", response = Result.class, notes = "必须的参数：")
	@RequestMapping(value = "web/check/member/mobile", method = RequestMethod.GET)
	@ResponseBody
	public Response checkMobile(String mobile) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (mobile != null && !"".equals(mobile)) {
			map.put("mobile", mobile);
			List<Member> list = memberService.listBy(map);
			if (list.size() > 0) {
				return new SuccessResponse();
			} else {
				return new FailedResponse("该号码不属于公司员工号码！");
			}
		} else {
			return new FailedResponse("手机号为空！");
		}
	}
}
