package com.youxing.car.controller;

import com.youxing.car.util.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.ROCRelation;
import com.youxing.car.entity.Region;
import com.youxing.car.entity.RegionRelation;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.ROCRelationService;
import com.youxing.car.service.RegionRelationService;
import com.youxing.car.service.RegionService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;
import com.youxing.car.utils.other.QueryUtilsWeb;


@Api(value = "car", description = "区域管理")
@Controller
public class RegionController {
	
	@Resource
	private RegionService regionService;
	@Resource
	private UserService userService;
	@Resource
	private RegionRelationService rrService;
	@Resource
	private ROCRelationService rocService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private QueryUtilsWeb queryUtilsWeb;
	
	@LogAnnotation(description="新增区域" )
	@ApiOperation(value = "新增区域", httpMethod = "POST", response = Car.class)
	@TranslationControl(value = "region/add")
	@RequestMapping(value = "web/region/add", method = RequestMethod.POST)
	@ResponseBody
	public Response add(@ApiParam(required = true, value = "区域对象") Region region, String str, HttpServletRequest request) {
		try {
			if (region == null || StringUtils.isBlank(str)) {
				return new FailedResponse("参数错误");
			}
			List<RegionRelation> list = new ArrayList<>();
			JSONArray strArr = JSONArray.fromObject(str);
			String id = request.getParameter("keyid");
			region.setStatus("1");
			region.setCreatebd(id);
			for (int i = 0; i < strArr.size(); i++) {
				JSONObject job = strArr.getJSONObject(i); // 遍历 jsonarray
				String lon = job.get("lng").toString();
				String lat = job.get("lat").toString();
				RegionRelation rr = new RegionRelation();
				rr.setLat(Double.parseDouble(lat));
				rr.setLon(Double.parseDouble(lon));
				list.add(rr);
			}
			rrService.add(region, list);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}

	}
	
	@LogAnnotation(description="区域修改" )
	@ApiOperation(value = "区域修改", httpMethod = "POST", response = Car.class)
	@TranslationControl(value = "region/update")
	@RequestMapping(value = "web/region/modify", method = RequestMethod.POST)
	@ResponseBody
	public Response modify(@ApiParam(required = true, value = "区域对象") Region region, String str, HttpServletRequest request) {
		try {
			if (region == null || StringUtils.isBlank(str)||region.getId() == null) {
				return new FailedResponse("参数错误");
			}
			List<RegionRelation> list = new ArrayList<>();
			JSONArray strArr = JSONArray.fromObject(str);
			String id = request.getParameter("keyid");
			region.setCreatebd(id);
			if (strArr.size() > 0) {
				for (int i = 0; i < strArr.size(); i++) {
					JSONObject job = strArr.getJSONObject(i); // 遍历 jsonarray
					String lon = job.get("lng").toString();
					String lat = job.get("lat").toString();
					RegionRelation rr = new RegionRelation();
					rr.setLat(Double.parseDouble(lat));
					rr.setLon(Double.parseDouble(lon));
					list.add(rr);
				}
			}
			rrService.modify(region,list);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}
	}
	
	@LogAnnotation(description="开启关闭删除区域" )
	@ApiOperation(value = "开启关闭删除区域", httpMethod = "POST")
	@TranslationControl(value = "route/open")
 	@RequestMapping(value="web/region/ocd",method=RequestMethod.POST)
	@ResponseBody
	public Response openRoute(@RequestParam(value = "ids[]") String[] ids,String status) {
		try {
			if(ids == null ||StringUtils.isBlank(status)) {
				return new FailedResponse("参数错误！");
			}
			//status = 1表示开启  2  关闭 3 删除
			rrService.modify(ids,status);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}
		
	}
	@LogAnnotation(description="开启关闭删除区域" )
	@ApiOperation(value = "开启关闭删除区域", httpMethod = "POST")
	@TranslationControl(value = "region/delete")
 	@RequestMapping(value="web/region/find",method=RequestMethod.POST)
	@ResponseBody
	public Response findRoute(String id) {
		try {
			if(StringUtils.isBlank(id)) {
				return new FailedResponse("参数错误！");
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("region_id", id);
			List<RegionRelation> list = rrService.listBy(map);
			return new ListResponse<RegionRelation>(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}
		
	}
	
	@LogAnnotation(description="区域分页查询" )
	@ApiOperation(value = "区域分页查询", httpMethod = "POST", response = Car.class)
	@TranslationControl(value = "area/list")
	@RequestMapping(value = "web/get/region/all", method = RequestMethod.POST)
	@ResponseBody
	public Page findAllRegion(Integer limit,Integer pageIndex,String name,String type){
		List<Region> list = new ArrayList<Region>();
		HashMap<String, Object> map = new HashMap<String, Object>();
 		if(limit != null && pageIndex != null){
 			map.put("startIdx", pageIndex * limit);
 			map.put("limit", limit);
 		}
 		if(StringUtils.isNotBlank(name)){
 			map.put("name", name);
 		}
 		if(StringUtils.isNotBlank(type)){
 			map.put("status", type);
 		}
 		list = regionService.findAllRegion(map);
 		int count = regionService.countAllRegion(map);
 		return new Page<Region>(list, count, limit);
	}
	@ApiOperation(value = "区域关联关系查询", httpMethod = "POST", response = Region.class)
	@TranslationControl(value = "relation/region")
	@RequestMapping(value = "web/region/relation/find", method = RequestMethod.POST)
	@ResponseBody
	public Response finRegionRelation(String type,String region){
		try {
			if(StringUtils.isBlank(region) || StringUtils.isBlank(type)  ){
				return new FailedResponse("参数错误！");
			}
			Map<String,String> map = new HashMap<>();
			map.put("type", type);
			map.put("region", region);
			List<ROCRelation> list = rocService.listBy(map);
			return new ListResponse<ROCRelation>(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}

	}
	
	@LogAnnotation(description="区域关联关系新增" )
	@ApiOperation(value = "区域关联关系新增", httpMethod = "POST", response = Region.class)
	@TranslationControl(value = "relation/region")
	@RequestMapping(value = "web/region/relation/add", method = RequestMethod.POST)
	@ResponseBody
	public Response addRegionRelation(
			@ApiParam(required = true, name = "type", value = "类型1工地  2 搅拌站")String type,
			@ApiParam(required = true, name = "org", value = "机构id")String org,
			@ApiParam(required = true, name = "region", value = "区域号")String region,
			@ApiParam(required = true, name = "car_ids", value = "车辆id数组")@RequestParam(value = "car_ids[]")String[] car_ids){
		try {
			if(StringUtils.isBlank(region) || StringUtils.isBlank(type) ){
				return new FailedResponse("参数错误！");
			}
			ROCRelation roc = new ROCRelation();
			roc.setType(type);
			roc.setRegion(Long.parseLong(region));
			
			rocService.removeByRegionId(region);
			if("2".equals(type)){
				roc.setOrg(Long.parseLong(org));
				rocService.add(roc);
			}
			if("1".equals(type)){
				if(car_ids != null){
					for(int  i = 0;i<car_ids.length;i++){
						String car_id = car_ids[i];
						roc.setCar_id(Long.parseLong(car_id));
						rocService.add(roc);
					}
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}
		

	}
	
	
	@LogAnnotation(description="区域关联关系编辑" )
	@ApiOperation(value = "区域关联关系编辑", httpMethod = "POST", response = Region.class)
	@RequestMapping(value = "web/region/relation/modify", method = RequestMethod.POST)
	@ResponseBody
	public Response modifyRegionRelation(
			@ApiParam(required = true, name = "type", value = "类型1搅拌站 2工地   ")String type,
			@ApiParam(required = true, name = "org", value = "机构id")String org,
			@ApiParam(required = true, name = "region", value = "区域号")String region,
			@ApiParam(required = true, name = "car_ids", value = "车辆id数组")String[] car_ids){
		
		ROCRelation roc = new ROCRelation();
		if(StringUtils.isBlank(region) || StringUtils.isBlank(type)  ){
			return new FailedResponse("参数错误！");
		}else{
			roc.setType(type);
			roc.setRegion(Long.parseLong(region));
			if("1".equals(type)){
				roc.setOrg(Long.parseLong(org));
				rocService.modify(roc);
			}
			if("2".equals(type)){
				//先删除关联关系再新增
				rocService.removeByRegionId(region);
				if(car_ids != null){
					for(int  i = 0;i<car_ids.length;i++){
						String car_id = car_ids[i];
						roc.setCar_id(Long.parseLong(car_id));
						rocService.add(roc);
					}
					
				}else{
					return new FailedResponse("请选择车辆！");
				}
				
			}
			return new SuccessResponse();
		}

	}
	
	@LogAnnotation(description="获取机构下的搅拌站" )
	@ApiOperation(value = "获取机构下的搅拌站", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:机构id,parent:机构父级id}")
	@RequestMapping(value="web/region/list", method = RequestMethod.GET)
	@ResponseBody
	public List<Selects> getOrgCar(String id,String parent) {
		List<Selects> list = new ArrayList<Selects>();
		if (StringUtils.isBlank(id)) {
			return list;
		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("status", Constant.ADD_STATUS);
		map.put("type", "2");
		if(StringUtils.isBlank(parent)){			
			map.put("org", Long.parseLong(id));
			list = regionService.listOrg(map);
		}else{
			List<Long> child = organizationService.getChildren(Long.parseLong(id));
			child.add(Long.parseLong(id));
			map.put("list", child);
			list = regionService.listOrg(map);
		}
		return list;
	}
	
	
	@LogAnnotation(description="供给次数统计" )
	@ApiOperation(value = "供给次数统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:机构id,parent:机构父级id}")
	@RequestMapping(value="web/count/region/page", method = RequestMethod.POST)
	@ResponseBody
	public Page  countCarRanks(Integer limit, Integer pageIndex,Long org, String starts, String end,String car_id,HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(starts)&&StringUtils.isNotBlank(end)) {
			map.put("start", starts+" 00:00:00");
			map.put("end", end+" 23:59:59");
		}
		if(limit != null && pageIndex !=null) {
			map.put("limit", limit);
			map.put("startIdx", pageIndex*limit);
		}
		
		if(StringUtils.isNotBlank(car_id)) {
			String ids[] = car_id.split(",");
			List<Object> idsList = new ArrayList<Object>();
			for (int i = 0; i < ids.length; i++) {
				idsList.add(Long.parseLong(ids[i]));
			}
			map.put("car",  idsList);
		}else{
			Organization userOrg = organizationService.findById(org);
			if (userOrg.getParent() != null) {
				List<Long> org_list = organizationService.getOrganizationUser(userOrg, org);
				//if (!CollectionUtils.isEmpty(org_list)) {
					org_list.add(org);
					map.put("list", org_list);
				//} 
			}
			
		}
		
		List<Object> list  = regionService.getSupplyCount(map);
		int total = regionService.getSupplyCountNum(map);
		return  new Page<Object>(list, total, limit);
		
	}
	
	
	@LogAnnotation(description="供给次数统计" )
	@ApiOperation(value = "供给次数统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:机构id,parent:机构父级id}")
	@RequestMapping(value="web/count/countCarGD/page", method = RequestMethod.POST)
	@ResponseBody
	public Page  getSupplyByCarId(Integer limit, Integer pageIndex, String starts, String end,String car_id,HttpServletRequest request) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(starts)&&StringUtils.isNotBlank(end)) {
			map.put("start", starts+" 00:00:00");
			map.put("end", end+" 23:59:59");
		}
		if(limit != null && pageIndex !=null) {
			map.put("limit", limit);
			map.put("startIdx", pageIndex*limit);
		}
		
		if(StringUtils.isBlank(car_id)){
			return null;
		}else{
			map.put("car_id", car_id);
		}
		
		List<Object> list  = regionService.getSupplyByCarId(map);
		int total = regionService.getSupplyByCarIdNum(map);
		return  new Page<Object>(list, total, limit);
		
	}
	
	@LogAnnotation(description="app单独工具新增区域" )
	@ApiOperation(value = "app单独工具新增区域", httpMethod = "POST", response = Car.class)
	@RequestMapping(value = "app/region/add", method = RequestMethod.POST)
	@ResponseBody
	public Result addGpsRegion(@ApiParam(required = true, value = "区域对象") Region region, String uid,String str, HttpServletRequest request) {
		try {
			if (region == null || StringUtils.isBlank(str)) {
				return Result.error("参数错误");
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<RegionRelation> list = new ArrayList<>();
			JSONArray strArr = JSONArray.fromObject(str);
			//String id = request.getParameter("keyid");
			region.setStatus("1");
			region.setCreatebd(uid);
			for (int i = 0; i < strArr.size(); i++) {
				JSONObject job = strArr.getJSONObject(i); // 遍历 jsonarray
				String lon = job.get("lng").toString();
				String lat = job.get("lat").toString();
				String index = job.get("index").toString();
				RegionRelation rr = new RegionRelation();
				rr.setLat(Double.parseDouble(lat));
				rr.setLon(Double.parseDouble(lon));
				rr.setIndex(Integer.parseInt(index));
				list.add(rr);
			}
			String reName = region.getName();
			map.put("name", reName);
			map.put("status", 1);
			map.put("type", region.getType());
			
			Region re = regionService.findByMap(map);
			if(re != null){
				region.setId(re.getId());
				rrService.modify(region, list);
			}else{
				rrService.add(region, list);
			}
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}

	}
	
	@LogAnnotation(description="区域搅拌站和工地统计查询" )
	@ApiOperation(value = "区域搅拌站和工地统计查询", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "web/get/regionCount/all", method = RequestMethod.POST)
	@ResponseBody
	public Response findCountRegion(){
		List<Region> list = new ArrayList<Region>();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
 		map.put("status", 1);	
 		list = regionService.findCountRegion(map);
 		return new ListResponse<Region>(list);
	}
	
	
	@LogAnnotation(description="区域数据查询" )
	@ApiOperation(value = "区域数据查询", httpMethod = "GET", response = Response.class)
	@RequestMapping(value = "web/get/regionDatas", method = RequestMethod.GET)
	@ResponseBody
	public Response findRegionDatas(HttpServletRequest request){
 		try{
 			List<Region> list = new ArrayList<Region>();
 			String user_id = request.getParameter("keyid");
 			HashMap<String, Object> map = new HashMap<String, Object>();
 	 		map.put("user_id", user_id);
 	 		list = regionService.findRegionDatas(map);
 	 		return new ListResponse<Region>(list);
 		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}
	
	@LogAnnotation(description="app区域数据查询" )
	@ApiOperation(value = "app区域数据查询", httpMethod = "GET", response = Result.class)
	@RequestMapping(value = "app/get/regionDatas", method = RequestMethod.GET)
	@ResponseBody
	public Result getRegionDatas(HttpServletRequest request) {
		try {
 			List<Region> list = new ArrayList<Region>();
 			String user_id = request.getParameter("uid");
 			User user  = new User();
 			user.setId(Long.parseLong(user_id));
 			user = userService.findBy(user);
 			long org = 0;
 			if(user != null){
 				org = user.getOrg();
 			}
 			if("51".equals(String.valueOf(org))){
 				HashMap<String, Object> map = new HashMap<String, Object>();
 	 	 		map.put("user_id", user_id);
 	 	 		list = regionService.findRegionDatas(map);
 	 	 		return Result.success(list);
 			}else{
 				return Result.instance(ResponseCode.unauthorized.getCode(), ResponseCode.unauthorized.getMsg());
 			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error();
		}

	}
		
}
