package com.youxing.car.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.SMS;
import com.youxing.car.entity.SMSSet;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.SmsService;
import com.youxing.car.service.SmsSetService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Result;
import com.youxing.car.util.TreeUtilsWeb;
import com.youxing.car.utils.other.QueryUtilsWeb;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;



@Api(value = "SMS", description = "短信管理")
@Controller
public class SmsController {
	
	@Resource
	private SmsService smsService;
	@Resource
	private SmsSetService smsSetService;
	@Resource
	private UserService userService;
	@Resource
	private QueryUtilsWeb queryUtilsWeb;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private MemberService memberService;
	/**
	 * 短信信息分页查询
	 * @param limit
	 * @param pageIndex
	 * @param request
	 * @return
	 */
	@LogAnnotation(description="短信信息分页查询" )
	@ApiOperation(value = "短信信息分页查询", httpMethod = "POST", response = Result.class)
	@TranslationControl(value = "sms/list")
	@RequestMapping(value="web/sms/page", method = RequestMethod.POST)
	@ResponseBody
	public Page smsPage(int limit, int pageIndex, HttpServletRequest request,String startDate,String endDate,String type) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex*limit);
		map.put("limit", limit);
		if(type != null && !"".equals(type)){
			map.put("type", type.split(","));
		}
		if(startDate != null && !"".equals(startDate)){
			map.put("startDate",startDate + " 00:00:00");			
		}
		if(endDate != null && !"".equals(endDate)){			
			map.put("endDate", endDate + " 23:59:59");			
		}
		List<SMS> list = smsService.pageBy(map);
		//查询用户姓名
		for(SMS sms :list){
			List<String> lists = new ArrayList<>();
			String sendtarget = sms.getSendtarget();
			if(sendtarget != null && !"".equals(sendtarget)){
			String[] ids = sendtarget.split(",");
			for(String id : ids){
				Member member = memberService.findById(Long.parseLong(id));
				if(member != null){
				lists.add(member.getName());
				}
			}
			String memberNames = org.apache.commons.lang.StringUtils.join(lists.toArray(),","); 
			sms.setMemberNames(memberNames);
		}
		}
		
		int recodes = smsService.countBy(map);
		return new Page<SMS>(list, recodes, limit);
	}
	
	
	/**
	 * 获取用户权限下所有机构部门成员列表
	 * @param request
	 * @return
	 */
	@LogAnnotation(description="获取用户权限下所有机构部门成员列表" )
	@ApiOperation(value = "获取用户权限下所有机构部门成员列表", httpMethod = "GET", response = Result.class)
	@RequestMapping("web/org/member/tree")
	@ResponseBody
	public List<Organization> listMemberAndOrg(HttpServletRequest request) {
		List<Organization> list = new ArrayList<Organization>();
		List<Organization> nodes = new ArrayList<Organization>();
		List<Member> memList = new ArrayList<Member>();
		User u =new User();
		String id = request.getParameter("keyid");
		u.setId(Long.parseLong(id));
		User user = userService.findBy(u);
		Long u_org = user.getOrg();
		try {
			HashMap<String, Object> maps = new HashMap<String, Object>();
			queryUtilsWeb.queryByOrg(request, maps);
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
				if(nodes.size() == 0){
					nodes.addAll(list);
				}
				HashMap<String, Object> mmap = new HashMap<String, Object>();
				
				if(maps.get("list") != null){
					mmap.put("list", maps.get("list"));
				}
				if(maps.get("id") != null){
					mmap.put("list", new Long[]{(Long)maps.get("id")});
				}
				memList = memberService.listMemberAndOrgService(mmap);
				for(Member m :memList){
					long oid =m.getOrg();
					if (perMap.containsKey(oid)) {
						Organization oo = new Organization();
						oo.setName(m.getName());
						oo.setMobile(m.getMobile());
						oo.setUser_name(m.getName());
						oo.setType("4");
						oo.setCls("tree_member");
						oo.setId(m.getId());
						perMap.get(oid).addChildren(oo);
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
	
	/**
	 * 短信设置对象
	 * @param request
	 * @return
	 */
	@LogAnnotation(description="短信设置对象" )
	@ApiOperation(value = "短信设置对象", httpMethod = "POST", response = Result.class)
	@TranslationControl(value = "sms/add")
	@RequestMapping(value="web/sms/set", method = RequestMethod.POST)
	@ResponseBody
	public Response smsSet(String id,String sendtarget,String sendphones,String sendname,String types,HttpServletRequest request) {
		if(sendtarget != null && sendphones != null && sendname != null && types != null){
			SMSSet sms =new SMSSet();
			sms.setId(Long.parseLong(id));
			sms.setSendtarget(sendtarget);
			sms.setSendname(sendname);
			sms.setSendphones(sendphones);
			sms.setTypes(types);
			smsSetService.modify(sms);
			return new SuccessResponse();
		}else{
			return null;
		}
			
	}
	
	@LogAnnotation(description="短信设置对象" )
	@ApiOperation(value = "短信设置对象", httpMethod = "POST", response = Result.class)
	@TranslationControl(value = "sms/add")
	@RequestMapping(value="web/sms/get",  method = RequestMethod.POST)
	@ResponseBody
	public Response smsGet() {
		List<SMSSet> list = smsSetService.listAll();
		if(list != null && list.size() == 1){
			//return new ListResponse<SMSSet>(list);
			return new ObjectResponse<SMSSet>(list.get(0));
		}else{
			return null;
		}
		
	}
	@LogAnnotation(description="短信删除" )
	@ApiOperation(value = "短信删除", httpMethod = "POST", response = Result.class)
	@TranslationControl(value = "sms/delete")
	@RequestMapping(value="web/sms/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response smsDelete(HttpServletRequest request,@RequestParam(value = "ids[]") String[] ids) {
		if(ids.length > 0){
			List<Object> idsList = new ArrayList<Object>();
			for (int i = 0; i < ids.length; i++) {
				idsList.add(Long.parseLong(ids[i]));
			}
			smsService.removeByIds(idsList);
			return new SuccessResponse();
		}
		return null;
	}
	
	@LogAnnotation(description="短信设置对象" )
	@ApiOperation(value = "短信设置对象", httpMethod = "POST", response = Result.class)
	@RequestMapping(value="web/set/sms/config",  method = RequestMethod.POST)
	@ResponseBody
	public Response smsConfig(String[] ids,String[] mobiles,String[] smsType) {
		List<SMSSet> list = smsSetService.listAll();
		HashMap<String, Object> map = new HashMap<String, Object>();
		 if(StringUtils.isNoneBlank(smsType)){
			 String smsStr = StringUtils.join(smsType,",");
				map.put("sms_type", smsStr);
		 }
		
		 if(StringUtils.isNoneBlank(ids)){
			 if(ids.length > 0){
				 List idList = java.util.Arrays.asList(ids);
				map.put("idlist", idList);
		 }
			
		}if(StringUtils.isNoneBlank(mobiles)){
			if(mobiles.length > 0){
				List idList = java.util.Arrays.asList(mobiles);
				map.put("mobilelist", idList);
		}
			
		}
		
		//设置前先将所有人员状态设置为0（表示为设置发送）
		memberService.setSmsTypeService();
		memberService.updateSmsTypeService(map);
		return new SuccessResponse();
		
	}
	

}
