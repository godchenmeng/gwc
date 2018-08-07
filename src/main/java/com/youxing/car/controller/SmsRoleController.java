package com.youxing.car.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.youxing.car.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.SmsRole;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.SmsRoleService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.DateUtilsWeb;
import com.youxing.car.util.Result;


@Api(value = "SMSRole", description = "短信角色")
@Controller
public class SmsRoleController {
	@Resource
	private SmsRoleService smsRoleService;
	@Resource
	private UserService userService;
	@Resource
	private OrganizationService organizationService;
	
	
	
	
	/**
	 * 
	 * @param limit
	 * @param pageIndex
	 * @return
	 */
	@LogAnnotation(description="短信角色列表" )
 	@ApiOperation(value = "短信角色列表", httpMethod = "POST", response = SmsRole.class)
	@RequestMapping(value = "web/sms/role/page", method = RequestMethod.POST)
	@ResponseBody
	public Page SmsRolePage(Integer limit,Integer pageIndex,String name){
 		Map<String, Object> map = new HashMap<String, Object>();
 		List<SmsRole> list = new ArrayList<SmsRole>();
 		if(limit != null && pageIndex != null){
 			map.put("startIdx", pageIndex * limit);
 			map.put("limit", limit);
 		}
 		if(StringUtils.isNotEmpty(name)){
 			map.put("name", name);
 		}
			list = smsRoleService.pageBy(map);
			int recodes = smsRoleService.countBy(map);
			return new Page<SmsRole>(list, recodes, limit);
	}
 	
 	
 	
 	/**
 	 * 
 	 * @param smsRole
 	 * @return
 	 */
	@LogAnnotation(description="短信角色新增" )
 	@ApiOperation(value = "短信角色新增", httpMethod = "POST", response = SmsRole.class)
	@TranslationControl(value = "smsrole/add")
	@RequestMapping(value = "web/sms/role/add", method = RequestMethod.POST)
	@ResponseBody
	public Response SmsRoleAdd(SmsRole smsRole,HttpServletRequest request){
 		if (smsRole == null) {
 			return new FailedResponse("参数错误");
 		}
 		try {
 			User u =new User();
 			String id = request.getParameter("keyid");
 			u.setId(Long.parseLong(id));
 			User user = userService.findBy(u);
 			long org = user.getOrg();
 			SmsRole sR= new SmsRole();
 			
 			List<SmsRole> list = smsRoleService.listAll();
 			List list1 = new ArrayList();
 			for(int i=0;i<list.size();i++){
 				SmsRole ro=list.get(i);
 				String names=ro.getName();
 				list1.add(names);
 			}
 			String name = smsRole.getName();
 			if(list1.contains(name)){
 				return new FailedResponse("该短信角色已经存在！");
 			}else{
 				sR.setCreateby(Long.parseLong(id));
 	 			sR.setCreatedate(DateUtilsWeb.getFormatDate(new Date()));
 	 			sR.setStatus(Constant.ADD_STATUS);
 	 			sR.setCreateby(user.getId());
 	 			sR.setOrg(org);
 	 			sR.setDescr(smsRole.getDescr());
 	 			sR.setName(name);
 	 			sR.setSendtypes(smsRole.getSendtypes());
 	 			smsRoleService.add(sR);
 	 			return new SuccessResponse();
 			}
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
	}
 	
 	
 	/**
 	 * 
 	 * @param smsRole
 	 * @return
 	 */
	@LogAnnotation(description="短信角色更新" )
 	@ApiOperation(value = "短信角色更新", httpMethod = "POST", response = SmsRole.class)
	@TranslationControl(value = "smsrole/update")
	@RequestMapping(value = "web/sms/role/update", method = RequestMethod.POST)
	@ResponseBody
	public Response SmsRoleUpdate(SmsRole smsRole,HttpServletRequest request){
 		if (smsRole == null) {
 			return new FailedResponse("参数错误");
 		}
 		try {
 			SmsRole sR= new SmsRole();
 			List<SmsRole> list = smsRoleService.listAll();
 			if (CollectionUtils.isNotEmpty(list)) {
 				if (list.size() == 1 && !list.get(0).getId().equals(smsRole.getId())) {
 					return new FailedResponse("该短信角色已经存在！");
 				}
 			}
 			String name = smsRole.getName();
 	 			sR.setDescr(smsRole.getDescr());
 	 			sR.setName(name);
 	 			sR.setSendtypes(smsRole.getSendtypes());
 	 			sR.setId(smsRole.getId());
 	 			smsRoleService.modify(sR);
 	 			return new SuccessResponse();
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 			return new FailedResponse("服务器错误");
 		}
	}
 	
	  @LogAnnotation(description="短信角色删除" )
 	  @ApiOperation(value = "短信角色删除", httpMethod = "POST", response = Result.class, notes = "必须的参数： {ids:idsArray[角色id数组]}")
	  @TranslationControl(value = "smsrole/delete")	
	  @RequestMapping(value = "web/sms/role/delete", method = RequestMethod.POST)
 	  @ResponseBody
 	 	public Response smsRoledelete(@RequestParam(value = "ids[]") String[] ids) {
 	 		if (ids.length == 0) {
 	 			return new FailedResponse("参数错误");
 	 		}
 	 		try {
 	 			for (String id : ids) {
 	 				SmsRole sr = smsRoleService.findById(Long.parseLong(id));
 	 				if (sr != null) {
 	 					SmsRole sRs = new SmsRole();
 	 					sRs.setId(sr.getId());
 	 					sRs.setStatus(Constant.REMOVE_STATUS);
 	 					smsRoleService.modify(sRs);
 	 				}
 	 			}
 	 			return new SuccessResponse();
 	 		} catch (Exception e) {
 	 			e.printStackTrace();
 	 			return new FailedResponse("服务器错误");
 	 		}
 	 	} 
 	  
	@LogAnnotation(description="获取登录机构下短信角色列表" )
 	@ApiOperation(value = "获取登录机构下短信角色列表", httpMethod = "GET", response = SmsRole.class)
 	@RequestMapping(value = "web/sms/org/role", method = RequestMethod.GET)
 	@ResponseBody
 	public List<SmsRole> SmsRolePageByOrg(HttpServletRequest request){
  		Map<String, Object> map = new HashMap<String, Object>();
  		List<SmsRole> list = new ArrayList<SmsRole>();
  		
  			User u =new User();
			String id = request.getParameter("keyid");
			u.setId(Long.parseLong(id));
			User user = userService.findBy(u);
			long org = user.getOrg();
			
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(
						userOrg, org);
				if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					map.put("list", org_list);
				} else {
					map.put("org", org);
				}
			}
 			list = smsRoleService.listBy(map);
			return list;
 	}

}
