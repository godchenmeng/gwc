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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.config.Config;
import com.youxing.car.entity.Bars;
import com.youxing.car.entity.BarsLat;
import com.youxing.car.entity.BarsRule;
import com.youxing.car.entity.BarsRuleRelation;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.BarsLatService;
import com.youxing.car.service.BarsRuleRelationService;
import com.youxing.car.service.BarsRuleService;
import com.youxing.car.service.BarsService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.BaiduMapUtils;
import com.youxing.car.util.Result;

@Api(value = "bas", description = "bas")
@Controller
public class BarsController {
	@Resource
	private BarsService barsService;
	@Resource
	private UserService userService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private BarsLatService barsLatService;
	@Resource
	private BarsRuleService barsRuleService;
	@Resource
	private BarsRuleRelationService barsRuleRelationService;
	@Resource
	private Config config;
	/**
	 * 
	 * @author mars   
	 * @date 2017年4月25日 下午1:32:12 
	 * @Description: TODO(分页显示电子栅栏) 
	 * @param @param limit
	 * @param @param pageIndex
	 * @param @param request
	 * @param @return    设定文件 
	 * @return Page    返回类型 
	 * @throws
	 */
	@LogAnnotation(description="栅栏分页显示" )
 	@ApiOperation(value = "栅栏分页显示", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0")
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="web/bars/page",method=RequestMethod.POST)
	@ResponseBody
	public Page bars(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex*limit);
		map.put("limit", limit);
		//map.put("status", Constant.ADD_STATUS);
		//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
		User u =new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);
		
		Long orgId = user.getOrg();
		Organization userOrg = organizationService.findById(orgId);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(userOrg, orgId);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(orgId);
				map.put("list", org_list);
			} else {
				map.put("org", orgId);
			}
		}
		List<Bars> list = barsService.pageBarsAndLatsBy(map);
		int recodes = barsService.countBarsAndLatsBy(map);
		return new Page<Bars>(list, recodes, limit);
	}
	
	/**
	 * 添加电子栅栏
	 * @param bar 实体
	 * @param request
	 * @return
	 */
	@LogAnnotation(description="添加电子栅栏" )
 	@ApiOperation(value = "添加电子栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数：bar:{电子栅栏对象},barRule:栅栏规则对象,name:栅栏名称,coordinates:{电子栅栏图形绘制坐标json对象},carids_devices:{车辆设备json对象}")
	@TranslationControl(value = "bars/add")
	@RequestMapping(value="web/bars/add",method=RequestMethod.POST)
	@ResponseBody
	public Response add(Bars bar,BarsRule barRule,HttpServletRequest request) {
		String coordinates = request.getParameter("coordinates");
		String name= request.getParameter("name");
		
		List<Bars> list= barsService.findNameService();
		List list1=new ArrayList();
			int j;
	        for(j = 0; j<list.size();j++){
	        	Bars ba=list.get(j);
	            String n=ba.getName();
	            list1.add(n);
	         }
	        if(list1.contains(name)){
	        	return new FailedResponse("该栅栏已经存在！");
	        }else{
	        	String carids_devices = request.getParameter("carids_devices");
	    		if (bar == null || StringUtils.isBlank(coordinates)) {
	    			return new FailedResponse("参数错误！");
	    		}
	        	try {
	    			//保存电子围栏
	    			//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
	        		User u =new User();
	        		String id1 = request.getParameter("keyid");
	        		u.setId(Long.parseLong(id1));
	        		User user = userService.findBy(u);
	        		
	        		
	    			bar.setCreateby(user.getId());
	    			bar.setOrg(user.getOrg());
	    			bar.setStatus(Constant.ADD_STATUS);
	    			bar.setCreatedate(new Date());
	    			barsService.add(bar);
	    			//保存电子围栏坐标
	    			JSONArray coordinatesJson = JSONArray.fromObject(coordinates);
	    			if(coordinatesJson.size() <= 0){
	    				return new FailedResponse("未绘制图形");
	    			}
	    			String ak = config.getAk();
	    			String service_id = config.getServiceId();
	    			String longitude = "";
	    			String latitude = "";
	    			String vertexes = "";
	    			List<BarsLat> barsLatList = new ArrayList<BarsLat>();
	    			for(int i=0;i<coordinatesJson.size();i++){
	    				JSONObject job = coordinatesJson.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
	    				String lon=job.get("lng").toString();//经度
	    				String lat=job.get("lat").toString();
	    				if(bar.getDrawingType().equals(Constant.BMAP_DRAWING_CIRCLE)){
	    					longitude = lon;
	    					latitude = lat;
	    				}else{
	    					if(i == coordinatesJson.size() - 1){
	    						vertexes += lat + "," + lon;
	    					}else{
	    						vertexes += lat + "," + lon + ";";
	    					}
	    				}
	    				BarsLat barsLat=new BarsLat();
	    				barsLat.setBarid(bar.getId());
	    				barsLat.setLat(Double.parseDouble(lat));
	    				barsLat.setLon(Double.parseDouble(lon));
	    				barsLatList.add(barsLat);
	    			}
	    			barsLatService.insertBatch(barsLatList);
	    			//保存电子栅栏规则
	    			barRule.setBarid(bar.getId());
	    			barsRuleService.add(barRule);

	    			//保存电子栅栏规则关联关系
	    			JSONArray carids_devicesJson = JSONArray.fromObject(carids_devices);
	    			if(carids_devicesJson.size() > 0){
	    				List<BarsRuleRelation> brrList = new ArrayList<BarsRuleRelation>();
	    				for(int i = 0; i<carids_devicesJson.size() ; i++){
	    					JSONObject jsonObject = carids_devicesJson.getJSONObject(i);
	    					BarsRuleRelation barsRuleRelation = new BarsRuleRelation();
	    					barsRuleRelation.setRuleid(barRule.getId());
	    					barsRuleRelation.setCarid(jsonObject.getLong("car_id"));
	    					Long fenceid = null;
	    					if(bar.getDrawingType().equals(Constant.BMAP_DRAWING_CIRCLE)){
	    						fenceid = BaiduMapUtils.createCircleFence(ak, service_id, bar.getName(), jsonObject.getString("device_no"), longitude, latitude, bar.getRadius());
	    					}else{
	    						fenceid = BaiduMapUtils.createPolygonFence(ak, service_id, bar.getName(), jsonObject.getString("device_no"), vertexes);
	    					}
	    					barsRuleRelation.setFence_id(fenceid);
							if (Constant.BARS_RULE_TYPE_1.equals(barRule.getType())) {
								barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_OUT);
							} else if (Constant.BARS_RULE_TYPE_2.equals(barRule.getType())) {
								barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_IN);
							} else if (Constant.BARS_RULE_TYPE_3.equals(barRule.getType())) {
								barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_UNKNOWN);
							}
	    					brrList.add(barsRuleRelation);
	    				}
	    				barsRuleRelationService.insertBatch(brrList);
	    			}
	    			return new SuccessResponse();
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			return new FailedResponse("服务器错误");
	    		}
	        	
	        }
	
	
	}
	
	/**
	 * 
	 * @Function: BarsController::update
	 * @Description: 电子栅栏更新
	 * @param bar 电子栅栏对象
	 * @param barRule  电子栅栏规则
	 * @param request
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 上午9:00:12 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="电子栅栏更新" )
 	@ApiOperation(value = "电子栅栏更新", httpMethod = "POST", response = Result.class, notes = "必须的参数：bar:{电子栅栏对象},barRule:栅栏规则对象,name:栅栏名称,coordinates:{电子栅栏图形绘制坐标json对象},carids_devices:{车辆设备json对象}")
	@TranslationControl(value = "bars/update")
	@RequestMapping(value = "web/bars/update",method=RequestMethod.POST)
	@ResponseBody
	public Response update(Bars bar,BarsRule barRule,HttpServletRequest request){
		String coordinates = request.getParameter("coordinates");//获取绘图的坐标
		String carids_devices = request.getParameter("carids_devices");//获取规则监控对象
		String ruleid = request.getParameter("ruleid");
		if (bar == null || barRule == null || StringUtils.isBlank(ruleid)) {
			return new FailedResponse("参数错误！");
		}
		try{
			barRule.setId(Long.parseLong(ruleid));
			barRule.setBarid(bar.getId());
			barsService.modify(bar);
			barsRuleService.modify(barRule);
			
			String ak = config.getAk();
			String service_id = config.getServiceId();
			String longitude = "";
			String latitude = "";
			String vertexes = "";
			Map<String,Object> map = new HashMap<String,Object>();
			if(!StringUtils.isBlank(coordinates)){
				//保存电子围栏坐标
				JSONArray coordinatesJson = JSONArray.fromObject(coordinates);
				if(coordinatesJson.size() > 0){
					barsLatService.removeByBarid(bar.getId());//删除之前所有坐标
					//保存当前绘图坐标
					List<BarsLat> barsLatList = new ArrayList<BarsLat>();
					for(int i=0;i<coordinatesJson.size();i++){
						JSONObject job = coordinatesJson.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
						String lon=job.get("lng").toString();//经度
						String lat=job.get("lat").toString();
						if(bar.getDrawingType().equals(Constant.BMAP_DRAWING_CIRCLE)){
							longitude = lon;
							latitude = lat;
						}else{
							if(i == coordinatesJson.size() - 1){
								vertexes += lat + "," + lon;
							}else{
								vertexes += lat + "," + lon + ";";
							}
						}
						BarsLat barsLat=new BarsLat();
						barsLat.setBarid(bar.getId());
						barsLat.setLat(Double.parseDouble(lat));
						barsLat.setLon(Double.parseDouble(lon));
						barsLatList.add(barsLat);
					}
					barsLatService.insertBatch(barsLatList);
				}
			}else{
				map.put("barid", bar.getId());
				List<BarsLat> list = barsLatService.listBy(map);
				if(list != null){
					for(int i = 0; i < list.size() ; i++){
						BarsLat barsLat = list.get(i);
						String lon = barsLat.getLon().toString();//经度
						String lat = barsLat.getLat().toString();
						if(bar.getDrawingType().equals(Constant.BMAP_DRAWING_CIRCLE)){
							longitude = lon;
							latitude = lat;
						}else{
							if(i == list.size() - 1){
								vertexes += lon + "," + lat;
							}else{
								vertexes += lon + "," + lat + ";";
							}
						}						
					}
				}
			}
			
			//删除百度电子栅栏
			List<BarsRuleRelation> rrlist = barsRuleRelationService.listBrrCarDevice(barRule.getId());
			if(rrlist != null){
				for(int i = 0; i < rrlist.size(); i++){
					Long fenceid = rrlist.get(i).getFenceid();
					String device_no = rrlist.get(i).getCar().getDevice().getDevice();
					if(fenceid != null && device_no != null){
						BaiduMapUtils.deleteFence(ak, service_id, device_no, fenceid);
					}
				}
			}
			
			//保存电子栅栏规则关联关系
			if(!StringUtils.isBlank(carids_devices)){
				JSONArray carids_devicesJson = JSONArray.fromObject(carids_devices);
				if(carids_devicesJson.size() > 0){
					//先删除之前的规则
					barsRuleRelationService.removeByRuleid(barRule.getId());
					List<BarsRuleRelation> brrList = new ArrayList<BarsRuleRelation>();
					for(int i = 0; i<carids_devicesJson.size() ; i++){
						JSONObject jsonObject = carids_devicesJson.getJSONObject(i);
						BarsRuleRelation barsRuleRelation = new BarsRuleRelation();
						barsRuleRelation.setRuleid(barRule.getId());
						barsRuleRelation.setCarid(jsonObject.getLong("car_id"));
						Long fenceid = null;
						if(bar.getDrawingType().equals(Constant.BMAP_DRAWING_CIRCLE)){
							fenceid = BaiduMapUtils.createCircleFence(ak, service_id, bar.getName(), jsonObject.getString("device_no"), longitude, latitude, bar.getRadius());
						}else{
							fenceid = BaiduMapUtils.createPolygonFence(ak, service_id, bar.getName(), jsonObject.getString("device_no"), vertexes);
						}
						barsRuleRelation.setFence_id(fenceid);
						if (Constant.BARS_RULE_TYPE_1.equals(barRule.getType())) {
							barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_OUT);
						} else if (Constant.BARS_RULE_TYPE_2.equals(barRule.getType())) {
							barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_IN);
						} else if (Constant.BARS_RULE_TYPE_3.equals(barRule.getType())) {
							barsRuleRelation.setMonitored_status(Constant.MONITORED_STATUS_UNKNOWN);
						}
						brrList.add(barsRuleRelation);
					}
					//批量插入新规则
					barsRuleRelationService.insertBatch(brrList);
				}else{
					barsRuleRelationService.removeByRuleid(barRule.getId());
				}
			}
			return new SuccessResponse();
		}catch(Exception e){
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}
	
	/**
	 * 
	 * @Function: BarsController::close
	 * @Description: 批量更电子栅栏关闭状态
	 * @param ids id数组
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 上午9:02:22 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="关闭栅栏" )
 	@ApiOperation(value = "关闭栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数{ids:idsArray[栅栏id数组]}")
	@TranslationControl(value = "bars/close")
	@RequestMapping(value="web/bars/close",method=RequestMethod.POST)
	@ResponseBody
	public Response close(@RequestParam(value = "ids[]") String[] ids){
		try{
			if(ids.length == 0){
				return new FailedResponse("参数错误");
			}
			List<Long> idsList = new ArrayList<Long>();
			for(int i=0; i<ids.length; i++){
				idsList.add(Long.parseLong(ids[i]));
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("list", idsList);
			map.put("status", Constant.REMOVE_STATUS);
			barsService.updateStatusByIds(map);
			return new SuccessResponse();
		}catch(Exception e){
			return new FailedResponse("服务器错误");
		}
	}
	
	/**
	 * 
	 * @Function: BarsController::open
	 * @Description: 批量更新电子栅栏开启状态
	 * @param ids
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 上午9:03:36 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="开启栅栏" )
 	@ApiOperation(value = "开启栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数{ids:idsArray[栅栏id数组]}")
	@TranslationControl(value = "bars/open")
	@RequestMapping(value="web/bars/open",method=RequestMethod.POST)
	@ResponseBody
	public Response open(@RequestParam(value = "ids[]") String[] ids){
		try{
			if(ids.length == 0){
				return new FailedResponse("参数错误");
			}
			List<Long> idsList = new ArrayList<Long>();
			for(int i=0; i<ids.length; i++){
				idsList.add(Long.parseLong(ids[i]));
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("list", idsList);
			map.put("status", Constant.ADD_STATUS);
			barsService.updateStatusByIds(map);
			return new SuccessResponse();
		}catch(Exception e){
			return new FailedResponse("服务器错误");
		}
	}
	
	
	/**
	 * 
	 * @Function: BarsController::delete
	 * @Description: 批量物理删除电子栅栏
	 * @param ids id数组
	 * @return
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月31日 上午9:04:52 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月31日     Banji Uoocent          v1.1.0               修改原因
	 */
	@LogAnnotation(description="删除栅栏" )
 	@ApiOperation(value = "删除栅栏", httpMethod = "POST", response = Result.class, notes = "必须的参数{ids:idsArray[栅栏id数组]}")
	@TranslationControl(value = "bars/delete")
	@RequestMapping(value="web/bars/delete",method=RequestMethod.POST)
	@ResponseBody
	public Response delete(@RequestParam(value = "ids[]") String[] ids){
		try{
			if(ids.length == 0){
				return new FailedResponse("参数错误");
			}
			List<Long> idsList = new ArrayList<Long>();
			for(int i=0; i<ids.length; i++){
				idsList.add(Long.parseLong(ids[i]));
			}
			barsService.removeByIds(idsList);
			return new SuccessResponse();
		}catch(Exception e){
			return new FailedResponse("服务器错误");
		}
	}
 	
 	
}
