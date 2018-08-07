package com.youxing.car.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Push;
import com.youxing.car.entity.User;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.PushService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;

/**   
 * @author mars   
 * @date 2017年5月18日 下午1:49:03 
 */
@Api(value = "push", description = "消息管理")
@Controller
public class PushController {
	private static Logger LOGGER = LoggerFactory.getLogger(PushController.class);
	@Resource
	private PushService pushService;
	@Resource
	private OrganizationService orgService;
	@Resource
	private UserService userService;
	
	@LogAnnotation(description="消息列表" )
	@ApiOperation(value = "消息列表", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = Push.class) })
	@RequestMapping(value = "app/push/list", method = RequestMethod.POST)
	@ResponseBody
	public Result getPush(@ApiParam(required = true, name = "uid", value = "用户id")Long uid,@ApiParam(required = true, name = "page", value = "哪一页")Integer page,@ApiParam(required = true, name = "size", value = "每一页大小")Integer size,@ApiParam(required = true, name = "type", value = "类型1-系统通知 2-超速 3-越界 4-非规定 5-拔插")String type){
		try {
			if(uid == null||page == null || size==null||StringUtils.isBlank(type)){
				return Result.instance(ResponseCode.missing_parameter.getCode(),ResponseCode.missing_parameter.getMsg());
			}
			List<Push> list = new ArrayList<>();
			Map<String,Object> map = new HashMap<>();
			
			User u1 =new User();
			u1.setId(uid);
			User user = userService.findBy(u1);
			
			long org = user.getOrg();
			List  list2 = new ArrayList();
			List  list3 = new ArrayList();
			List<Organization> lists=orgService.countChildService(org);
			for(Organization o : lists){
				long id = o.getId();
				list2.add(id);
			}
			list2.add(org);
			map.put("lists", list2);
			List<User> listIds = userService.countIdsService(map);
			for(User u : listIds){
				Long id = u.getId();
				list3.add(id);
			}
			map.clear();
			map.put("type", type);
			map.put("startIdx", page*size);
			map.put("limit", size);
			map.put("lists", list3);
			list = pushService.pageBy(map);
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/push/list", e);
			return Result.error();
		}
		
	}
	
//	@ApiOperation(value = "每种消息条数", httpMethod = "GET", response = Result.class)
//	@ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = CountPushType.class) })
//	@RequestMapping(value = "app/push/type", method = RequestMethod.GET)
//	@ResponseBody
//	public Result getPushByType(@ApiParam(required = true, name = "uid", value = "用户id")Long uid){
//		try {
//			if(uid == null){
//				return Result.instance(ResponseCode.missing_parameter.getCode(),ResponseCode.missing_parameter.getMsg());
//			}
//			List<CountPushType> count = new ArrayList<CountPushType>();
//			Map<String,Object> map = new HashMap<>();
//			map.put("push", uid);
//			map.put("read", 0);
//			count = pushService.groupByType(map);
//			return Result.success(count);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return Result.error();
//		}
//		
//	}
}


