package com.youxing.car.controller;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.CountInfo;
import com.youxing.car.entity.InfoCenter;
import com.youxing.car.entity.JsonObject;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.CarService;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.OverstepService;
import com.youxing.car.service.UserService;
import com.youxing.car.utils.other.QueryUtilsWeb;

@Api(value = "info", description = "消息提醒")
@Controller
public class InfoCenterController {

	@Resource
	private InfoCenterService infoService;
	@Resource
	private UserService userService;
	@Resource
	private CarService carService;
	@Resource
	private OrganizationService orgService;

	@Resource
	private OrganizationService organizationService;

	@Resource
	private QueryUtilsWeb queryUtilsWeb;
	
	@Resource
	private OverstepService overstepService;

	/**
	 * 消息提醒 ：违规类型（0违章，1越界，2超速，3超时，4拔插警报，5保险提醒，6年检提醒，7保养提醒）
	 * 
	 * @param page
	 * @param size
	 * @param type
	 * @param uid
	 * @return
	 */
	@LogAnnotation(description="消息提醒" )
	@ApiOperation(value = "消息提醒    参数：name:登陆名     type:违规类型", httpMethod = "POST", response = Result.class)
	@RequestMapping(value = "app/info/infoList", method = RequestMethod.POST)
	@ResponseBody
	public Result infoList(
			@ApiParam(value = "哪一页") Integer page,
			@ApiParam(value = "每页大小") Integer size,
			@ApiParam(value = "违规类型（0违章，1越界，2超速，3超时，4拔插警报，5保险提醒，6年检提醒，7保养提醒）  ") String type,
			@ApiParam(value = "登陆名id") String uid) {
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(type)
				|| page == null || size == null) {
			return Result.instance(ResponseCode.missing_parameter.getCode(),
					ResponseCode.missing_parameter.getMsg());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("illegal_type", type);
		map.put("startIdx", page * size);
		map.put("limit", size);

		User u = new User();
		u.setId(Long.parseLong(uid));
		User user = userService.findBy(u);

		List list3 = new ArrayList();
		long org = user.getOrg();
		List<Organization> list1 = orgService.countChildService(org);

		for (Organization o : list1) {
			long id = o.getId();
			list3.add(id);
		}
		list3.add(org);

		map.put("list", list3);
		List<InfoCenter> list = infoService.findInfoListService(map);
		return Result.success(list);
	}

	/*
	 * web端消息中心
	 */

	// 返回异常统计页面
	@RequiresPermissions(value = "info/exception")
	@RequestMapping("info/exception")
	public String list(HttpServletRequest requset) {
		return "info/exception";
	}

	// 返回消息中心页面
	@RequiresPermissions(value = "info/infoCenter")
	@RequestMapping("info/infoCenter")
	public String list2(HttpServletRequest requset) {
		return "info/infoCenter";
	}

	/**
	 * 消息中心 ：违规类型（0违章，1越界，2超速，3超时，4拔插警报，5保险提醒，6年检提醒，7保养提醒）
	 * 
	 * @param limit
	 * @param pageIndex
	 * @param startDate
	 * @param endDate
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="分页查询消息列表" )
	@ApiOperation(value = "分页查询消息列表", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0,hide_org: 机构id,car_id:车辆id,//多台时，用号隔开type:违规类型,driver:驾驶员姓名,startDate:查询开始时间, endDate:查询结束时间")
	@TranslationControl(value = "info/infoCenter")
	@RequestMapping(value = "web/info/find", method = RequestMethod.POST)
	@ResponseBody
	public Page findIllegalInfoController(Integer limit, int pageIndex,
			String startDate, String endDate, HttpServletRequest request)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		HashMap<String, Object> map = new HashMap<String, Object>();
		String car_id = request.getParameter("car_id");
		String driver = request.getParameter("driver");
		String illegal_type = request.getParameter("type");
		String org = request.getParameter("hide_org");
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		map.put("startDate", sdf.parse(startDate + " 00:00:00"));
		map.put("endDate", sdf.parse(endDate + " 23:59:59"));
		// map.put("car_id", car_id);

		// User u = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u1 = new User();
		String id1 = request.getParameter("keyid");
		u1.setId(Long.parseLong(id1));
		User u = userService.findBy(u1);
		String u_org = Long.toString(u.getOrg());
		if (org == null || org == "" || org.equals(u_org)) {
			queryUtilsWeb.queryByOrg(request, map);
			if (map.get("id") != null) {
				String o = map.get("id").toString();
				map.remove("id");
				map.put("org", o);
			}
		} else {
			// map.put("org", org);
			queryUtilsWeb.queryByOrgOne(Long.parseLong(org), map);
		}
		map.put("driver", driver);
		map.put("illegal_type", illegal_type);
		if (StringUtils.isNotEmpty(car_id)) {
			String[] car = car_id.split(",");
			List<String> car_list = new ArrayList<String>();
			for (int i = 0; i < car.length; i++) {
				car_list.add(car[i].trim());
			}
			map.put("car_list", car_list);
		}

		List<InfoCenter> list = infoService.findIllegalInfoService(map);
		int recodes = infoService.findInfoCount(map);
		return new Page<InfoCenter>(list, recodes, limit);

	}

	/**
	 * 异常统计（表格显示）
	 * 
	 * @throws ParseException
	 */
	@LogAnnotation(description="分页查询车辆异常数据统计" )
	@ApiOperation(value = "分页查询车辆异常数据统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：  limit:15,pageIndex:0,hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@TranslationControl(value = "info/exception")
	@RequestMapping(value = "web/info/exceptionCount", method = RequestMethod.POST)
	@ResponseBody
	public Page exceptionCountController(int limit, int pageIndex,
			String startDate, String endDate, HttpServletRequest request)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		String car_id = request.getParameter("car_id");
		String org = request.getParameter("hide_org");
		String driver = request.getParameter("driver");
		HashMap<String, Object> map = new HashMap<String, Object>();

		if (StringUtils.isNotEmpty(car_id)) {
			String[] car = car_id.split(",");
			List<String> car_list = new ArrayList<String>();
			for (int i = 0; i < car.length; i++) {
				car_list.add(car[i].trim());
			}
			map.put("car_list", car_list);
		}
		// User u = (User)
		// request.getSession().getAttribute(Constant.SESSION_KEY);
		User u1 = new User();
		String id1 = request.getParameter("keyid");
		u1.setId(Long.parseLong(id1));
		User u = userService.findBy(u1);

		String u_org = Long.toString(u.getOrg());
		if (org == null || org == "" || org.equals(u_org)) {
			queryUtilsWeb.queryByOrgWeb(request, map);
		} else {
			queryUtilsWeb.queryByOrgOne(Long.parseLong(org), map);
		}
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("driver", driver);
		map.put("startDate", sdf.parse(startDate + " 00:00:00"));
		map.put("endDate", sdf.parse(endDate + " 23:59:59"));

		List<InfoCenter> list = infoService.exceptionCountService(map);

		/*
		 * HashMap map1 = new HashMap(); List<Organization> list1 =
		 * orgService.countChildService(u.getOrg()); List list2=new ArrayList();
		 * for(Organization o:list1){ long id=o.getId(); list2.add(id); }
		 * list2.add(u.getOrg()); map1.put("list2", list2);
		 */
		int recodes = infoService.exCountService(map);
		return new Page<InfoCenter>(list, recodes, limit);

	}

	/**
	 * 统计异常某时间段内的总次数（饼状图）
	 */
	@LogAnnotation(description="分页查询车辆异常数据统计" )
	@ApiOperation(value = "饼状图", httpMethod = "POST", response = Result.class, notes = "必须的参数：hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/exceptionAllCount")
	@RequestMapping(value = "web/info/exceptionAllCount", method = RequestMethod.POST)
	@ResponseBody
	public Object exceptionAllCount(String startDate, String endDate,
			String org, String driver, String car_id, HttpServletRequest request) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {

			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("startDate", sdf.parse(startDate + " 00:00:00"));
			maps.put("endDate", sdf.parse(endDate + " 23:59:59"));
			// User u = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);

			User u1 = new User();
			String id1 = request.getParameter("keyid");
			u1.setId(Long.parseLong(id1));
			User u = userService.findBy(u1);
			String u_org = Long.toString(u.getOrg());
			if (org == null || org == "" || org.equals(u_org)) {
				queryUtilsWeb.queryByOrgWeb(request, maps);
			} else {
				// maps.put("org", org);
				queryUtilsWeb.queryByOrgOne(Long.parseLong(org), maps);
			}
			maps.put("driver", driver);

			// maps.put("car_id", car_id);
			if (StringUtils.isNotEmpty(car_id)) {
				String[] car = car_id.split(",");
				List<String> car_list = new ArrayList<String>();
				for (int i = 0; i < car.length; i++) {
					car_list.add(car[i].trim());
				}
				maps.put("car_list", car_list);
			}

			List<InfoCenter> list = infoService.exceptionAllCountService(maps);
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject("10004", PropertiesKey.readValue("10004"));
		}
	}

	/**
	 * 曲线图 ，查询一个时间段内每一天的各种违规记录
	 * 
	 * @throws ParseException
	 */
	@LogAnnotation(description="曲线图" )
	@ApiOperation(value = "曲线图", httpMethod = "POST", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/dayExCount")
	@RequestMapping(value = "web/info/dayExCount", method = RequestMethod.POST)
	@ResponseBody
	public Object dayExCount(String startDate, String endDate, String org,
			String driver, String car_id, HttpServletRequest request)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {

			HashMap<String, Object> maps = new HashMap<String, Object>();
			maps.put("startDate", sdf.parse(startDate + " 00:00:00"));
			maps.put("endDate", sdf.parse(endDate + " 23:59:59"));
			// User u = (User)
			// request.getSession().getAttribute(Constant.SESSION_KEY);

			User u1 = new User();
			String id1 = request.getParameter("keyid");
			u1.setId(Long.parseLong(id1));
			User u = userService.findBy(u1);
			String u_org = Long.toString(u.getOrg());
			if (org == null || org == "" || org.equals(u_org)) {
				queryUtilsWeb.queryByOrgWeb(request, maps);
			} else {
				queryUtilsWeb.queryByOrgOne(Long.parseLong(org), maps);

			}
			maps.put("driver", driver);

			if (StringUtils.isNotEmpty(car_id)) {
				String[] car = car_id.split(",");
				List<String> car_list = new ArrayList<String>();
				for (int i = 0; i < car.length; i++) {
					car_list.add(car[i].trim());
				}
				maps.put("car_list", car_list);
			}
			List<InfoCenter> list = infoService.dayExCountService(maps);
			return list;

		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject("10004", PropertiesKey.readValue("10004"));
		}
	}

	/**
	 * 消息中心 点击未处理违规 ，变更处理时间为当前时间
	 */
	@LogAnnotation(description="消息处理" )
	@ApiOperation(value = "消息处理", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequiresPermissions(value = "web/info/updateStatus")
	@RequestMapping(value = "web/info/updateStatus", method = RequestMethod.POST)
	@ResponseBody
	public Object updateStatus(String id) {

		try {

			int count = infoService.updateStatusService(id);
			return count;

		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject("10004", PropertiesKey.readValue("10004"));
		}

	}

	/**
	 * 消息中心导出excel
	 * 
	 * @throws ParseException
	 */
	@LogAnnotation(description="消息中心数据导出" )
	@ApiOperation(value = "消息中心数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/exportExcel")
	@RequestMapping(value = "web/info/exportExcel", method = RequestMethod.GET)
	@ResponseBody
	public Object exportExcel(HttpServletResponse response,
			HttpServletRequest request, String driver, String type, String org,
			String id, String startDate, String endDate) throws ParseException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!"".equals(id) && id != null) {
			String ids[] = id.split(",");
			List<Object> idsList = new ArrayList<Object>();
			for (int i = 0; i < ids.length; i++) {
				idsList.add(Long.parseLong(ids[i]));
			}
			map.put("list", idsList);

		}
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("org", org);
		map.put("driver", driver);
		map.put("illegal_type", type);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "消息异常" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "消息明细";
		// 列名
		String[] title = new String[] { "Id", "车牌号", "车辆别名", "驾驶员", "部门",
				"发生时间", "处理时间", "违规类型", "违规时速" };
		// 内容list
		List<InfoCenter> list = infoService.exportExcelService(map);

		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			InfoCenter info = list.get(i);
			values[i][0] = info.getId() + "";
			values[i][1] = info.getCar_no();
			values[i][2] = info.getCar_name();
			values[i][3] = info.getDriver();
			values[i][4] = info.getName();
			values[i][5] = info.getHappen_time();
			values[i][6] = info.getSolve_time();

			String str = info.getIllegal_type();
			// 违规类型（0违章，1越界，2超速，3超时，4拔插警报，5保险提醒，6年检提醒，7保养提醒）
			if (str.equals("0")) {
				values[i][7] = "违章";
			}
			if (str.equals("1")) {
				values[i][7] = "越界";
			}
			if (str.equals("2")) {
				values[i][7] = "超速";
			}
			if (str.equals("3")) {
				values[i][7] = "超时";
			}
			if (str.equals("4")) {
				values[i][7] = "拔插警报";
			}
			if (str.equals("5")) {
				values[i][7] = "保险提醒";
			}
			if (str.equals("6")) {
				values[i][7] = "年检提醒";
			}
			if (str.equals("7")) {
				values[i][7] = " 保养提醒";
			}

			values[i][8] = info.getIllegal_speed();

		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}

	/**
	 * 异常统计导出excel
	 * 
	 * @throws ParseException
	 */
	@LogAnnotation(description="异常统计数据导出" )
	@ApiOperation(value = "异常统计数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id,//多台时，用号隔开type:违规类型,driver:驾驶员姓名,startDate:查询开始时间, endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/exceptionExcel")
	@RequestMapping(value = "web/info/exceptionExcel", method = RequestMethod.GET)
	@ResponseBody
	public Object exceptionExcel(HttpServletResponse response,
			HttpServletRequest request, String id, String startDate,
			String endDate, String driver, String org) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		if (!"".equals(id) && id != null) {
			String ids[] = id.split(",");
			List<Object> idsList = new ArrayList<Object>();
			for (int i = 0; i < ids.length; i++) {
				idsList.add(Long.parseLong(ids[i]));
			}
			map.put("list", idsList);
		}

		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("org", org);
		map.put("driver", driver);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "消息 异常" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "消息明细";
		// 列名
		String[] title = new String[] { "车牌号", "违章", "越界", "超速", "非规定时段",
				"拔插报警" };

		List<InfoCenter> list = infoService.exceptionExcelService(map);
		// 内容list
		String[][] values = new String[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			values[i] = new String[title.length];
			// 将对象内容转换成string
			InfoCenter info = list.get(i);
			values[i][0] = info.getCar_no();
			int tmpIlleagal = info.getIllegal_count();
			values[i][1] = String.valueOf(tmpIlleagal);
			int tmpIns = info.getInspection_count();
			values[i][2] = String.valueOf(tmpIns);
			int tmpSpe = info.getSpeed();
			values[i][3] = String.valueOf(tmpSpe);
			int tmpOver = info.getOvertime();
			values[i][4] = String.valueOf(tmpOver);
			int tmpEle = info.getEle_count();
			values[i][5] = String.valueOf(tmpEle);

		}

		// 生成表格
		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}

	// 自定义下载文件
	public void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			try {
				// 浏览器下载中文名文件兼容性处理
				fileName = new String(fileName.getBytes(), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// 自动匹配文件类型
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 超速统计
	 * 
	 * @throws ParseException
	 */
	@LogAnnotation(description="超速统计" )
	@ApiOperation(value = "超速统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/over_speed/page", method = RequestMethod.POST)
	@ResponseBody
	public Page overSpeedCount(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request) throws ParseException {
		String[] car_ids = request.getParameterValues("car_ids[]");
		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<Long> orgIdList = new ArrayList<Long>();// 记录有数据的机构
		List<Long> orgList = new ArrayList<Long>();// 没有数据的机构，补0用
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();
		List<Object> listreturn = new ArrayList<Object>();

		if (StringUtils.isNotBlank(start_time) && StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time + " 00:00:00");
			map.put("endDate", end_time + " 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
				// 转换long类型的数组
				Long[] strArrNum = (Long[]) ConvertUtils.convert(org_ids, Long.class);
				orgList = Arrays.asList(strArrNum);
			} else {
				List<Long> listOrg = getAllOrg(request);
				if (listOrg != null) {
					// 去除出重复元素
					for (int i = 0; i < listOrg.size() - 1; i++) {
						for (int j = listOrg.size() - 1; j > i; j--) {
							if (listOrg.get(j).equals(listOrg.get(i))) {
								listOrg.remove(j);
							}
						}
					}
					map.put("list", listOrg);
					orgList.addAll(listOrg);
				}
			}
			list = infoService.overSpeedCountService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();
					String id = in.getOrg();
					String date = in.getDate();
					String count = in.getSpeedCount();
					if (listName.size() == 0) {
						maps.put("org_name", orgName);
						maps.put(date, count);
						maps.put("org_id", id);
						listDates.add(date);
						listName.add(orgName);
						orgIdList.add(Long.parseLong(id));
					} else if (listName.contains(orgName)) {
						maps.put(date, count);
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
					} else {

						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}

						lists.add(maps);

						listDates.clear();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						maps.put("org_id", id);
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
						if (!listName.contains(orgName)) {
							listName.add(orgName);
						}
						if (!orgIdList.contains(id)) {
							orgIdList.add(Long.parseLong(id));
						}
					}

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}

				lists.add(maps);

				// 没有数据的部门补0
				List<Long> receiveDefectList = ListUtil.receiveDefectList(orgList, orgIdList);
				if (receiveDefectList != null) {
					for (Long org : receiveDefectList) {
						Organization organization = organizationService.findById(org);
						Map<Object, Object> ma = new HashMap<Object, Object>();
						ma.put("org_name", organization.getName());
						ma.put("org_id", org);
						for (String day : listDate) {
							ma.put(day, 0);
						}
						lists.add(ma);
					}
				}

			} else {
				if (org_ids != null) {
					map.put("list", org_ids);
					orgNames = orgService.listBy(map);
				} else {
					List<Long> listOrg = getOrg(request);
					// 去除出重复元素
					if (listOrg != null) {
						for (int i = 0; i < listOrg.size() - 1; i++) {
							for (int j = listOrg.size() - 1; j > i; j--) {
								if (listOrg.get(j).equals(listOrg.get(i))) {
									listOrg.remove(j);
								}
							}
						}
						map.put("list", listOrg);
					}
					orgNames = orgService.findOrgNameService(map);
				}
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			} else {
				List<Long> listOrg = getOrg(request);
				if (listOrg != null) {
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long> li = new ArrayList<Long>();
				if (carList != null) {
					for (Car car : carList) {
						long id = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			List<HashMap<String, Object>> listcar = infoService.overSpeedCountByCarService(map);

			for (int i = 0; i < listcar.size(); i++) {
				CountInfo info = new CountInfo();
				HashMap<String, Object> car = listcar.get(i);
				info.setEnd_site(car.get("end_address").toString());
				info.setEnd_time(car.get("end_happen_date").toString());
				info.setMax_speed(Long.parseLong(car.get("apex").toString()));
				info.setStart_site(car.get("start_address").toString());
				info.setStart_time(car.get("start_happen_date").toString());
				info.setOrg_name(car.get("org_name").toString());
				info.setCar_no(car.get("car_no").toString());
				double time = Double.parseDouble(car.get("time").toString());
				time  = time / 1000;
				info.setDuration(time);
				lists.add(info);
			}

		}

		for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
			if (mm < lists.size()) {
				lists.get(mm);
				listreturn.add(lists.get(mm));
			}
		}

		return new Page<Object>(listreturn, lists.size(), limit);

	}

	/**
	 * 超速统计 按月 按周
	 * 
	 * @param request
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="超速统计 按月 按周" )
	@ApiOperation(value = "超速统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/over_speed", method = RequestMethod.GET)
	@ResponseBody
	public Response overSpeedMonthAndWeek(String start_time, String end_time,
			HttpServletRequest request) throws ParseException {

		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time + " 00:00:00");
			map.put("endDate", end_time + " 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg  != null){
				map.put("list", listOrg);
			}
		}
		list = infoService.overSpeedCountService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();
				String id = in.getOrg();
				String date = in.getDate();
				String count = in.getSpeedCount();
				if (listName.size() == 0) {
					maps.put("org_name", orgName);
					maps.put(date, count);
					maps.put("org_id", id);
					listDates.add(date);
					listName.add(orgName);
				} else if (listName.contains(orgName)) {
					maps.put(date, count);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
				} else {

					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}

					lists.add(maps);

					listDates.clear();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					maps.put("org_id", id);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
					if (!listName.contains(orgName)) {
						listName.add(orgName);
					}
				}

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}

			lists.add(maps);

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}
		}

		return new ListResponse<Object>(lists);
	}
	@LogAnnotation(description="超速导出" )
	@ApiOperation(value = "超速导出", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/over_speed/export", method = RequestMethod.GET)
	@ResponseBody
	public Object overSpeedExport(String start_time, String end_time,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {

		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time + " 00:00:00");
			map.put("endDate", end_time + " 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg  != null){
				map.put("list", listOrg);
			}
		}
		list = infoService.overSpeedCountService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();
				String id = in.getOrg();
				String date = in.getDate();
				String count = in.getSpeedCount();
				if (listName.size() == 0) {
					maps.put("org_name", orgName);
					maps.put(date, (String) count);
					maps.put("org_id", id);
					listDates.add(date);
					listName.add(orgName);
				} else if (listName.contains(orgName)) {
					maps.put(date, (String) count);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
				} else {

					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, "0");
						}
					}

					lists.add(maps);

					listDates.clear();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, (String) count);
					maps.put("org_id", id);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
					if (!listName.contains(orgName)) {
						listName.add(orgName);
					}
				}

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, "0");
				}
			}

			lists.add(maps);

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, "0");
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "超速数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;

		String[][] values = null;
		if (lists.size() > 0) {
			values = new String[lists.size()][];
			for (int i = 0; i < lists.size(); i++) {
				title = new String[] { "部门名称", listDate.get(0),
						listDate.get(1), listDate.get(2), listDate.get(3),
						listDate.get(4), listDate.get(5), listDate.get(6) };
				Object obj = lists.get(i);
				Map<String, String> mapobj = (Map<String, String>) obj;
				values[i] = new String[title.length];
				// 将对象内容转换成string

				for (Entry<String, String> entry : mapobj.entrySet()) {
					String key = entry.getKey();

					if ("org_name".equals(key)) {
						values[i][0] = entry.getValue();
					} else if (listDate.get(0).equals(key)) {
						values[i][1] = entry.getValue();
					} else if (listDate.get(1).equals(key)) {
						values[i][2] = entry.getValue();
					} else if (listDate.get(2).equals(key)) {
						values[i][3] = entry.getValue();
					} else if (listDate.get(3).equals(key)) {
						values[i][4] = entry.getValue();
					} else if (listDate.get(4).equals(key)) {
						values[i][5] = entry.getValue();
					} else if (listDate.get(5).equals(key)) {
						values[i][6] = entry.getValue();
					} else if (listDate.get(6).equals(key)) {
						values[i][7] = entry.getValue();
					}
				}
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ("ok");

	}

	/**
	 * 越界统计
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="越界统计" )
	@ApiOperation(value = "越界统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/outside/page", method = RequestMethod.POST)
	@ResponseBody
	public Page overBorderCount(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request) throws ParseException {
		String[] car_ids = request.getParameterValues("car_ids[]");
		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<Map<String, Object>> list = new ArrayList<>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();
		List<Object> listreturn = new ArrayList<Object>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time +" 00:00:00");
			map.put("endDate", end_time +" 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg  != null){
					map.put("list", listOrg);
				}
			}
			list = overstepService.overStepCountByOrg(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> overStepMap = list.get(i);
					String orgName = overStepMap.get("orgName").toString();
					String id = overStepMap.get("org").toString();
					String date = overStepMap.get("date").toString();
					Integer count = Integer.parseInt(overStepMap.get("inspection_count").toString());
					if (listName.size() == 0) {
						maps.put("org_name", orgName);
						maps.put(date, count);
						maps.put("org_id", id);
						listDates.add(date);
						listName.add(orgName);
					} else if (listName.contains(orgName)) {
						maps.put(date, count);
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
					} else {

						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}

						lists.add(maps);

						listDates.clear();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						maps.put("org_id", id);
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
						if (!listName.contains(orgName)) {
							listName.add(orgName);
						}
					}

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}

				lists.add(maps);

			} else {
				if(org_ids != null){
					map.put("list", org_ids);
					orgNames = orgService.listBy(map);
				}else{
					List<Long> listOrg = getOrg(request);
					//去除出重复元素
					if(listOrg  != null){
						for  ( int  i  =   0 ; i  <  listOrg.size()  -   1 ; i ++ )   { 
						    for  ( int  j  =  listOrg.size()  -   1 ; j  >  i; j -- )   { 
						      if  (listOrg.get(j).equals(listOrg.get(i)))   { 
						    	  listOrg.remove(j); 
						      } 
						    } 
						  } 
						map.put("list", listOrg);
					}
					orgNames =  orgService.findOrgNameService(map);
				}
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();

					if (!listName.contains(name)) {
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j).toString();
							maps.put(day, 0);
						}
						maps.put("org_name", name);
						maps.put("org_id", oid);
						lists.add(maps);
						listName.add(name);
						maps = new HashMap<Object, Object>();
					}

				}
			}

		}
		if ("car".equals(show_type)) {

			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			
			if (limit != null && pageIndex != null) {
				map.put("startIdx", pageIndex * limit);
				map.put("limit", limit);
			}

			List<HashMap<String,Object>> listcar = overstepService.overStepByCar(map);
			int count = overstepService.overStepNum(map);
			return new Page<HashMap<String,Object>>(listcar, count, limit);
		}

		for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
			if (mm < lists.size()) {
				lists.get(mm);
				listreturn.add(lists.get(mm));
			}
		}

		return new Page<Object>(listreturn, listName.size(), limit);

	}

	/**
	 * 越界统计 按月 按周
	 * 
	 * @param org_ids
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="越界统计 按月 按周" )
	@ApiOperation(value = "越界统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/outside", method = RequestMethod.GET)
	@ResponseBody
	public Response overBorderMonthAndWeek(
			@RequestParam(value = "org_ids[]") String[] org_ids,
			String start_time, String end_time,HttpServletRequest request) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<Map<String, Object>> list = new ArrayList<>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time +" 00:00:00");
			map.put("endDate", end_time +" 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg != null){
				map.put("list", listOrg);
			}
		}
		list = overstepService.overStepCountByOrg(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> overStepMap = list.get(i);
				String orgName = overStepMap.get("orgName").toString();
				String id = overStepMap.get("org").toString();
				String date = overStepMap.get("date").toString();
				Integer count = Integer.parseInt(overStepMap.get("inspection_count").toString());
				if (listName.size() == 0) {
					maps.put("org_name", orgName);
					maps.put(date, count);
					maps.put("org_id", id);
					listDates.add(date);
					listName.add(orgName);
				} else if (listName.contains(orgName)) {
					maps.put(date, count);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
				} else {

					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}

					lists.add(maps);

					listDates.clear();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					maps.put("org_id", id);
					if (!listDates.contains(date)) {
						listDates.add(date);
					}
					if (!listName.contains(orgName)) {
						listName.add(orgName);
					}
				}

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}

			lists.add(maps);

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}
		}
		return new ListResponse<Object>(lists);
	}

	/**
	 * 越界数据导出
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="越界数据导出" )
	@ApiOperation(value = "越界数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/outside/export", method = RequestMethod.GET)
	@ResponseBody
	public Object overBorderExport(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletResponse response, HttpServletRequest request)
			throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String,Object>> listcar = new ArrayList<Map<String,Object>>();
		List<Organization> orgNames = new ArrayList<Organization>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<String> listDate = new ArrayList<String>();
		List<String> listName = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		List<Object> lists = new ArrayList<Object>();

		String[] org_ids = request.getParameterValues("org_ids[]");

		String[] car_ids = request.getParameterValues("car_ids[]");


		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time +" 00:00:00");
			map.put("endDate", end_time +" 23:59:59");
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "越界数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;
		String[][] values = null;

		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = overstepService.overStepMonthAndWeek(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> overStepMap = list.get(i);
					String orgName = overStepMap.get("orgName").toString();
					String id = overStepMap.get("org").toString();
					String date = overStepMap.get("date").toString();
					Integer count = Integer.parseInt(overStepMap.get("inspection_count").toString());
					if (listName.size() == 0) {
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						maps.put("org_id", id);
						listDates.add(date);
						listName.add(orgName);
					} else if (listName.contains(orgName)) {
						maps.put(date, String.valueOf(count));
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
					} else {

						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0");
							}
						}

						lists.add(maps);

						listDates.clear();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						maps.put("org_id", id);
						if (!listDates.contains(date)) {
							listDates.add(date);
						}
						if (!listName.contains(orgName)) {
							listName.add(orgName);
						}
					}

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, "0");
					}
				}

				lists.add(maps);

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

			if (lists.size() > 0) {
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] { "部门名称", listDate.get(0),
							listDate.get(1), listDate.get(2), listDate.get(3),
							listDate.get(4), listDate.get(5), listDate.get(6) };
					Object obj = lists.get(i);
					Map<String, String> mapobj = (Map<String, String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string

					for (Entry<String, String> entry : mapobj.entrySet()) {
						String key = entry.getKey();

						if ("org_name".equals(key)) {
							values[i][0] = entry.getValue();
						} else if (listDate.get(0).equals(key)) {
							values[i][1] = entry.getValue();
						} else if (listDate.get(1).equals(key)) {
							values[i][2] = entry.getValue();
						} else if (listDate.get(2).equals(key)) {
							values[i][3] = entry.getValue();
						} else if (listDate.get(3).equals(key)) {
							values[i][4] = entry.getValue();
						} else if (listDate.get(4).equals(key)) {
							values[i][5] = entry.getValue();
						} else if (listDate.get(5).equals(key)) {
							values[i][6] = entry.getValue();
						} else if (listDate.get(6).equals(key)) {
							values[i][7] = entry.getValue();
						}
					}
				}
			}

		}

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			listcar = overstepService.overStepByCarEx(map);

			if (listcar.size() > 0) {
				values = new String[listcar.size()][];
				for (int i = 0; i < listcar.size(); i++) {
					title = new String[] { "部门名称", "车牌号", "触发时间", "类型", "触发地点" };

					Map<String, Object> carMap = listcar.get(i);
					values[i] = new String[title.length];
					// 将对象内容转换成string
					values[i][0] = carMap.get("org_name").toString();
					values[i][1] = carMap.get("car_no").toString();
					values[i][2] = carMap.get("happen_date").toString();
					String type = carMap.get("fence_type").toString();
					if ("1".equals(type)) {
						values[i][3] = "驶入触发";
					}
					if ("2".equals(type)) {
						values[i][3] = "驶出触发";
					}
					if ("3".equals(type)) {
						values[i][3] = "驶入驶出触发";
					}
					values[i][4] = carMap.get("address").toString();
				}
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ("ok");
	}

	/**
	 * 违章统计
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	@LogAnnotation(description="违章统计" )
	@ApiOperation(value = "违章统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/violation/page", method = RequestMethod.POST)
	@ResponseBody
	public Page illegalCount(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request) throws ParseException {

		String[] car_ids = request.getParameterValues("car_ids[]");
		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		List<Object> listreturn = new ArrayList<Object>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.illegalCountByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getIllegal_count();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}
		if ("car".equals(show_type) && car_ids != null) {

			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			List<CountInfo> listcar = infoService.illegalCountByCarService(map);
			int count = infoService.illegalNumService(map);
			return new Page<CountInfo>(listcar, count, limit);
		}

		for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
			if (mm < lists.size()) {
				listreturn.add(lists.get(mm));
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);

	}
	@LogAnnotation(description="违章数据统计 按月 按周" )
	@ApiOperation(value = "违章数据统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/violation", method = RequestMethod.GET)
	@ResponseBody
	public Response illegalMonthAndWeek(String start_time, String end_time,
			HttpServletRequest request) throws ParseException {
		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg  != null){
				map.put("list", listOrg);
			}
		}
		
		list = infoService.illegalCountMonthAndWeekService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();

				if (listName.size() == 0) {
					String date = in.getDate();
					int count = in.getIllegal_count();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else if (listName.contains(orgName)) {
					String date = in.getDate();
					int count = in.getIllegal_count();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else {
					String date = in.getDate();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}
					lists.add(maps);
					listDates.clear();
					int count = in.getIllegal_count();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				}
				listName.add(orgName);

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}
			lists.add(maps);
			maps = new HashMap<Object, Object>();

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}

		}
		return new ListResponse<Object>(lists);
	}

	/**
	 * 违章数据导出
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="违章数据导出" )
	@ApiOperation(value = "违章数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/violation/export", method = RequestMethod.GET)
	@ResponseBody
	public Object illegalEx(String show_type, Integer limit, Integer pageIndex,
			String start_time, String end_time, HttpServletResponse response,
			HttpServletRequest request) throws ParseException {
		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		List<CountInfo> listcar = new ArrayList<CountInfo>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "违章数据" + sdf.format(System.currentTimeMillis())+ ".xls";
		// sheet名
		String sheetName = "数据明细";
		String[] title = null;
		String[][] values = null;

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if ("org".equals(show_type)) {

			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg  != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.illegalCountByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0");
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getIllegal_count();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, "0");
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

			if (lists.size() > 0) {
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] {
						"部门名称", listDate.get(0),
						listDate.get(1), listDate.get(2), listDate.get(3),
						listDate.get(4), listDate.get(5), listDate.get(6) 
						};
					Object obj = lists.get(i);
					Map<String, String> mapobj = (Map<String, String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string

					for (Entry<String, String> entry : mapobj.entrySet()) {
						String key = entry.getKey();

						if ("org_name".equals(key)) {
							values[i][0] = entry.getValue();
						} else if (listDate.get(0).equals(key)) {
							values[i][1] = entry.getValue();
						} else if (listDate.get(1).equals(key)) {
							values[i][2] = entry.getValue();
						} else if (listDate.get(2).equals(key)) {
							values[i][3] = entry.getValue();
						} else if (listDate.get(3).equals(key)) {
							values[i][4] = entry.getValue();
						} else if (listDate.get(4).equals(key)) {
							values[i][5] = entry.getValue();
						} else if (listDate.get(5).equals(key)) {
							values[i][6] = entry.getValue();
						} else if (listDate.get(6).equals(key)) {
							values[i][7] = entry.getValue();
						}
					}
				}
			}
		}

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			listcar = infoService.illegalExByCarService(map);
			values = new String[listcar.size()][];
			for (int i = 0; i < listcar.size(); i++) {
				title = new String[] { "部门名称", "车牌号", "违章时间", "违章位置", "违章类型","驾驶员" };

				CountInfo ci = listcar.get(i);
				values[i] = new String[title.length];
				// 将对象内容转换成string
				values[i][0] = ci.getOrg_name();
				values[i][1] = ci.getCar_no();
				values[i][2] = ci.getViolation_time();
				values[i][3] = ci.getViolation_site();
				values[i][4] = ci.getViolation_type();
				values[i][5] = ci.getDriver_name();

			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}

	/**
	 * 非规定时段
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	@LogAnnotation(description="非规定时段统计" )
	@ApiOperation(value = "非规定时段统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/foul_time/page", method = RequestMethod.POST)
	@ResponseBody
	public Page foulTimeCount(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request) throws ParseException {

		String[] org_ids = request.getParameterValues("org_ids[]");
		String[] car_ids = request.getParameterValues("car_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		List<Object> listreturn = new ArrayList<Object>();
		List<CarNotice> listc = new ArrayList<CarNotice>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.foulTimeByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getOvertime();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getOvertime();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getOvertime();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}
		if ("car".equals(show_type) && car_ids != null) {

			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			List<CountInfo> listcar = infoService.foulTimeByCarService(map);
			if(listcar.size() > 0){
				lists.addAll(listcar);
			}
			
		}
		if(lists.size() > 0){
			for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
				if (mm < lists.size()) {
					listreturn.add(lists.get(mm));
				}
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);

	}

	/**
	 * 非规定时段数据统计 按月 按周
	 * 
	 * @param org_ids
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="非规定时段数据统计 按月 按周" )
	@ApiOperation(value = "非规定时段数据统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/foul_time", method = RequestMethod.GET)
	@ResponseBody
	public Response foulTimeMonthAndWeek(
			@RequestParam(value = "org_ids[]") String[] org_ids,
			String start_time, String end_time,HttpServletRequest request) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg != null){
				map.put("list", listOrg);
			}
		}
		list = infoService.foulTimeCountMonthAndWeekService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();

				if (listName.size() == 0) {
					String date = in.getDate();
					int count = in.getOvertime();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else if (listName.contains(orgName)) {
					String date = in.getDate();
					int count = in.getOvertime();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else {
					String date = in.getDate();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}
					lists.add(maps);
					listDates.clear();
					int count = in.getOvertime();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				}
				listName.add(orgName);

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}
			lists.add(maps);
			maps = new HashMap<Object, Object>();

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}

		}

		lists.add(maps);
		return new ListResponse<Object>(lists);
	}
	@LogAnnotation(description="非规定时段数据导出" )
	@ApiOperation(value = "非规定时段数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/foul_time/export", method = RequestMethod.GET)
	@ResponseBody
	public Object foulTimeEx(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletResponse response, HttpServletRequest request)
			throws ParseException {

		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}
		

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();
		
		List<CountInfo> listcar = new ArrayList<CountInfo>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "非规定时段数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";
		String[] title = null;
		String[][] values = null;

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if ("org".equals(show_type)) {

			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.foulTimeExByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getIllegal_count();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0");
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getIllegal_count();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, "0");
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

			if (lists.size() > 0) {
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] {
						"部门名称", listDate.get(0),
						listDate.get(1), listDate.get(2), listDate.get(3),
						listDate.get(4), listDate.get(5), listDate.get(6) 
						};
					Object obj = lists.get(i);
					Map<String, String> mapobj = (Map<String, String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string

					for (Entry<String, String> entry : mapobj.entrySet()) {
						String key = entry.getKey();

						if ("org_name".equals(key)) {
							values[i][0] = entry.getValue();
						} else if (listDate.get(0).equals(key)) {
							values[i][1] = entry.getValue();
						} else if (listDate.get(1).equals(key)) {
							values[i][2] = entry.getValue();
						} else if (listDate.get(2).equals(key)) {
							values[i][3] = entry.getValue();
						} else if (listDate.get(3).equals(key)) {
							values[i][4] = entry.getValue();
						} else if (listDate.get(4).equals(key)) {
							values[i][5] = entry.getValue();
						} else if (listDate.get(5).equals(key)) {
							values[i][6] = entry.getValue();
						} else if (listDate.get(6).equals(key)) {
							values[i][7] = entry.getValue();
						}
					}
				}
			}
		}		

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			listcar = infoService.foulTimeExByCarService(map);
			if (listcar.size() > 0) {

				values = new String[listcar.size()][];
				for (int i = 0; i < listcar.size(); i++) {
					title = new String[] { "部门名称", "车牌号", "触发时间", "持续时长", "类型",
							"触发地点" };

					CountInfo ci = listcar.get(i);
					values[i] = new String[title.length];
					// 将对象内容转换成string
					values[i][0] = ci.getOrg_name();
					values[i][1] = ci.getCar_no();
					values[i][2] = ci.getTrigger_time();
					Double duration = ci.getDuration();
					values[i][3] = String.valueOf(duration);
					values[i][4] = ci.getTrigger_type();
					values[i][5] = ci.getTrigger_site();

				}
			} else {
				values = new String[1][];
				for (int i = 0; i < 1; i++) {
					title = new String[] { "部门名称", "车牌号", "触发时间", "持续时长", "类型",
							"触发地点" };
					values[i] = new String[title.length];
					// 将对象内容转换成string
					values[i][0] = "当前查询条件下无数据";
					values[i][1] = "当前查询条件下无数据";
					values[i][2] = "当前查询条件下无数据";
					values[i][3] = "当前查询条件下无数据";
					values[i][4] = "当前查询条件下无数据";
					values[i][5] = "当前查询条件下无数据";

				}
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}

	/**
	 * 驾驶统计
	 * 
	 * @param show_type
	 * @param request
	 * @param limit
	 * @param pageIndex
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="驾驶统计" )
	@ApiOperation(value = "驾驶统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/drive/page", method = RequestMethod.POST)
	@ResponseBody
	public Page driverCount(String show_type, Integer limit, Integer pageIndex,
			String start_time, String end_time, HttpServletRequest request)
			throws ParseException {

		String[] org_ids = request.getParameterValues("org_ids[]");
		String[] car_ids = request.getParameterValues("car_ids[]");

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		List<Object> listreturn = new ArrayList<Object>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.driveCountByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						Double count = in.getMileSum();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						Double count = in.getMileSum();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}
						lists.add(maps);
						listDates.clear();
						Double count = in.getMileSum();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}
		if ("car".equals(show_type)) {

			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList  != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			List<CountInfo> listcar = infoService.driveCountByCarService(map);
			for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
				if (mm < listcar.size()) {
					listreturn.add(listcar.get(mm));
				}
			}
			return new Page<Object>(listreturn, listcar.size(), limit);
		}

		for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
			if (mm < lists.size()) {
				listreturn.add(lists.get(mm));
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);

	}

	/**
	 * 驾驶数据统计 按月 按周
	 * 
	 * @param org_ids
	 * @param start_time
	 * @param end_time
	 * @return
	 * @throws ParseException
	 */
	@LogAnnotation(description="驾驶数据统计 按月 按周" )
	@ApiOperation(value = "驾驶数据统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/drive", method = RequestMethod.GET)
	@ResponseBody
	public Response driverMonthAndWeek(
			@RequestParam(value = "org_ids[]") String[] org_ids,
			String start_time, String end_time, HttpServletRequest request) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}else{
			List<Long> listOrg = getOrg(request);
			if(listOrg != null){
				map.put("list", listOrg);
			}
		}
		list = infoService.driveCountMonthAndWeekService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();

				if (listName.size() == 0) {
					String date = in.getDate();
					Double count = in.getMileSum();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else if (listName.contains(orgName)) {
					String date = in.getDate();
					Double count = in.getMileSum();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else {
					String date = in.getDate();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}
					lists.add(maps);
					listDates.clear();
					Double count = in.getMileSum();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				}
				listName.add(orgName);

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}
			lists.add(maps);
			maps = new HashMap<Object, Object>();

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}

		}
		return new ListResponse<Object>(lists);
	}
	@LogAnnotation(description="驾驶数据导出" )
	@ApiOperation(value = "驾驶数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/drive/export", method = RequestMethod.GET)
	@ResponseBody
	public Object driverEx(String show_type, Integer limit, Integer pageIndex,
			String start_time, String end_time, HttpServletResponse response,
			HttpServletRequest request) throws ParseException {

		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		List<CountInfo> listcar = new ArrayList<CountInfo>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "驾驶数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;

		String[][] values = null;

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);

		if ("org".equals(show_type)) {

			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.driveCountByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						Double count = in.getMileSum();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						Double count = in.getMileSum();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0");
							}
						}
						lists.add(maps);
						listDates.clear();
						Double count = in.getMileSum();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, "0");
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}

			}
			if (lists.size() > 0) {
				values = new String[lists.size()][];
				for (int i = 0; i < lists.size(); i++) {
					title = new String[] { "部门名称", listDate.get(0),
							listDate.get(1), listDate.get(2), listDate.get(3),
							listDate.get(4), listDate.get(5), listDate.get(6) };
					Object obj = lists.get(i);
					Map<String, String> mapobj = (Map<String, String>) obj;
					values[i] = new String[title.length];
					// 将对象内容转换成string

					for (Entry<String, String> entry : mapobj.entrySet()) {
						String key = entry.getKey();

						if ("org_name".equals(key)) {
							values[i][0] = entry.getValue();
						} else if (listDate.get(0).equals(key)) {
							values[i][1] = entry.getValue();
						} else if (listDate.get(1).equals(key)) {
							values[i][2] = entry.getValue();
						} else if (listDate.get(2).equals(key)) {
							values[i][3] = entry.getValue();
						} else if (listDate.get(3).equals(key)) {
							values[i][4] = entry.getValue();
						} else if (listDate.get(4).equals(key)) {
							values[i][5] = entry.getValue();
						} else if (listDate.get(5).equals(key)) {
							values[i][6] = entry.getValue();
						} else if (listDate.get(6).equals(key)) {
							values[i][7] = entry.getValue();
						}
					}
				}
			}

		}

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   li = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						li.add(id);
					}
					map.put("lists", li);
				}
			}
			listcar = infoService.driveExByCarService(map);
			values = new String[listcar.size()][];
			for (int i = 0; i < listcar.size(); i++) {
				title = new String[] { "部门名称", "车牌号", "里程", "驾驶时长", "油耗", "三急" };

				CountInfo ci = listcar.get(i);
				values[i] = new String[title.length];
				// 将对象内容转换成string
				values[i][0] = ci.getOrg_name();
				values[i][1] = ci.getCar_no();
				Double mile = ci.getMileage();
				values[i][2] = String.valueOf(mile);
				Double time = ci.getDrive_duration();
				values[i][3] = String.valueOf(time);
				Double fuel = ci.getFuel();
				values[i][4] = String.valueOf(fuel);
				values[i][5] = ci.getBreak_drive();
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}
	@LogAnnotation(description="三急数据统计" )
	@ApiOperation(value = "三急数据统计", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/drive/break", method = RequestMethod.GET)
	@ResponseBody
	public Response driverThree(String start_time, String end_time,
			HttpServletRequest request) {

		String[] org_ids = request.getParameterValues("org_ids[]");

		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> maps = new HashMap<String, Object>();
		List<CountInfo> list = new ArrayList<CountInfo>();
		List<Object> lists = new ArrayList<Object>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		if (org_ids != null) {
			map.put("org_list", org_ids);
		}

		list = infoService.driveThreeService(map);
		for (int i = 0; i < list.size(); i++) {
			CountInfo ci = list.get(i);
			if(ci != null){
				
			// 急加速
			int trip_accelerate_sum = ci.getTrip_accelerate_sum();
			// 急减速
			int trip_decelerate_sum = ci.getTrip_decelerate_sum();
			// 急转弯
			int trip_sharp_sum = ci.getTrip_sharp_sum();

			maps.put("name", "急加速");
			maps.put("y", trip_accelerate_sum);
			maps.put("count", trip_accelerate_sum);
			lists.add(maps);
			maps = new HashMap<String, Object>();

			maps.put("name", "急减速");
			maps.put("y", trip_decelerate_sum);
			maps.put("count", trip_decelerate_sum);
			lists.add(maps);
			maps = new HashMap<String, Object>();

			maps.put("name", "急转弯");
			maps.put("y", trip_sharp_sum);
			maps.put("count", trip_sharp_sum);
			lists.add(maps);
			
			}
		}
		return new ListResponse<Object>(lists);
	}
	@LogAnnotation(description="无单违规用车统计" )
	@ApiOperation(value = "无单违规用车统计", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/foul_task/page", method = RequestMethod.POST)
	@ResponseBody
	public Page noTaskUseCar(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request) throws ParseException {

		String[] org_ids = request.getParameterValues("org_ids[]");
		String[] car_ids = request.getParameterValues("car_ids[]");

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		List<Object> listreturn = new ArrayList<Object>();
		List<CarNotice> listc = new ArrayList<CarNotice>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.nonTaskByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getNoTaskCount();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getNoTaskCount();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, 0);
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getNoTaskCount();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, count);
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, 0);
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, 0);
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}

		for (int mm = pageIndex * limit; mm < ((pageIndex + 1) * limit); mm++) {
			if (mm < lists.size()) {
				listreturn.add(lists.get(mm));
			}
		}
		return new Page<Object>(listreturn, lists.size(), limit);

	}
	@LogAnnotation(description="无单违规用车统计导出" )
	@ApiOperation(value = "无单违规用车统计导出", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/foul_task/export", method = RequestMethod.GET)
	@ResponseBody
	public Object noTaskUseCarExport(String show_type, Integer limit,
			Integer pageIndex, String start_time, String end_time,
			HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		if (StringUtils.isNotBlank(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}
			list = infoService.nonTaskByOrgService(map);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					InfoCenter in = list.get(i);
					String orgName = in.getOrgName();

					if (listName.size() == 0) {
						String date = in.getDate();
						int count = in.getNoTaskCount();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else if (listName.contains(orgName)) {
						String date = in.getDate();
						int count = in.getNoTaskCount();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					} else {
						String date = in.getDate();
						for (int j = 0; j < listDate.size(); j++) {
							String day = listDate.get(j);
							if (!listDates.contains(day)) {
								maps.put(day, "0");
							}
						}
						lists.add(maps);
						listDates.clear();
						int count = in.getNoTaskCount();
						maps = new HashMap<Object, Object>();
						maps.put("org_name", orgName);
						maps.put(date, String.valueOf(count));
						String id = in.getOrg();
						maps.put("org_id", id);
						listDates.add(date);
					}
					listName.add(orgName);

				}
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					if (!listDates.contains(day)) {
						maps.put(day, "0");
					}
				}
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			} else {
				map.put("list", org_ids);
				orgNames = orgService.listBy(map);
				for (Organization o : orgNames) {
					String name = o.getName();
					Long oid = o.getId();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j).toString();
						maps.put(day, "0");
					}
					maps.put("org_name", name);
					maps.put("org_id", oid);
					lists.add(maps);
					maps = new HashMap<Object, Object>();

				}
			}

		}

		/*
		 * if ("car".equals(show_type)) {
		 * 
		 * if (car_ids != null) { map.put("car_list", car_ids); }
		 * List<CountInfo> listcar = infoService.driveCountByCarService(map);
		 * lists.add(listcar); }
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "无单违规用车数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;

		String[][] values = null;
		if (lists.size() > 0) {
			values = new String[lists.size()][];
			for (int i = 0; i < lists.size(); i++) {
				title = new String[] { "部门名称", listDate.get(0),
						listDate.get(1), listDate.get(2), listDate.get(3),
						listDate.get(4), listDate.get(5), listDate.get(6) };
				Object obj = lists.get(i);
				Map<String, String> mapobj = (Map<String, String>) obj;
				values[i] = new String[title.length];
				// 将对象内容转换成string

				for (Entry<String, String> entry : mapobj.entrySet()) {
					String key = entry.getKey();

					if ("org_name".equals(key)) {
						values[i][0] = entry.getValue();
					} else if (listDate.get(0).equals(key)) {
						values[i][1] = entry.getValue();
					} else if (listDate.get(1).equals(key)) {
						values[i][2] = entry.getValue();
					} else if (listDate.get(2).equals(key)) {
						values[i][3] = entry.getValue();
					} else if (listDate.get(3).equals(key)) {
						values[i][4] = entry.getValue();
					} else if (listDate.get(4).equals(key)) {
						values[i][5] = entry.getValue();
					} else if (listDate.get(5).equals(key)) {
						values[i][6] = entry.getValue();
					} else if (listDate.get(6).equals(key)) {
						values[i][7] = entry.getValue();
					}
				}
			}
		}

		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}
	@LogAnnotation(description="无单违规用车数据统计 按月 按周" )
	@ApiOperation(value = "无单违规用车数据统计 按月 按周", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/foul_task", method = RequestMethod.GET)
	@ResponseBody
	public Response noTaskMonthAndWeek(
			@RequestParam(value = "org_ids[]") String[] org_ids,
			String start_time, String end_time) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> maps = new HashMap<Object, Object>();
		List<InfoCenter> list = new ArrayList<InfoCenter>();
		List<Object> listName = new ArrayList<Object>();
		List<String> listDate = new ArrayList<String>();
		List<Object> listDates = new ArrayList<Object>();
		List<Object> lists = new ArrayList<Object>();
		List<Organization> orgNames = new ArrayList<Organization>();

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		if (org_ids != null) {
			map.put("org_list", org_ids);
		}
		list = infoService.nonTaskMonthAndWeekService(map);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				InfoCenter in = list.get(i);
				String orgName = in.getOrgName();

				if (listName.size() == 0) {
					String date = in.getDate();
					int count = in.getNoTaskCount();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else if (listName.contains(orgName)) {
					String date = in.getDate();
					int count = in.getNoTaskCount();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				} else {
					String date = in.getDate();
					for (int j = 0; j < listDate.size(); j++) {
						String day = listDate.get(j);
						if (!listDates.contains(day)) {
							maps.put(day, 0);
						}
					}
					lists.add(maps);
					listDates.clear();
					int count = in.getNoTaskCount();
					maps = new HashMap<Object, Object>();
					maps.put("org_name", orgName);
					maps.put(date, count);
					String id = in.getOrg();
					maps.put("org_id", id);
					listDates.add(date);
				}
				listName.add(orgName);

			}
			for (int j = 0; j < listDate.size(); j++) {
				String day = listDate.get(j).toString();
				if (!listDates.contains(day)) {
					maps.put(day, 0);
				}
			}
			lists.add(maps);
			maps = new HashMap<Object, Object>();

		} else {
			map.put("list", org_ids);
			orgNames = orgService.listBy(map);
			for (Organization o : orgNames) {
				String name = o.getName();
				Long oid = o.getId();
				for (int j = 0; j < listDate.size(); j++) {
					String day = listDate.get(j).toString();
					maps.put(day, 0);
				}
				maps.put("org_name", name);
				maps.put("org_id", oid);
				lists.add(maps);
				maps = new HashMap<Object, Object>();

			}

		}
		lists.add(maps);
		return new ListResponse<Object>(lists);
	}
	@LogAnnotation(description="综合统计" )
	@ApiOperation(value = "综合统计 ", httpMethod = "POST", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/synthesize/page", method = RequestMethod.POST)
	@ResponseBody
	public Page allCount(String show_type, Integer limit, Integer pageIndex,
			String start_time, String end_time, HttpServletRequest request)
			throws ParseException {

		String[] car_ids = request.getParameterValues("car_ids[]");
		String[] org_ids = request.getParameterValues("org_ids[]");
		Map<String, Object> map = new HashMap<String, Object>();
		List<CountInfo> list = new ArrayList<CountInfo>();
		int count = 0;
		
		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time + " 00:00:00");
			map.put("endDate", end_time + " 23:59:59");
		}

		if (limit != null && pageIndex != null) {
			map.put("startIdx", pageIndex * limit);
			map.put("limit", limit);
		}

		if ("time".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   lists = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						lists.add(id);
					}
					map.put("lists", lists);
				}
			}
			list = infoService.allCountByTime(map);
			count = infoService.allCountByTimeNum(map);
		}
		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			
			}
			list = infoService.allCountByOrg(map);
			count = infoService.allCountByOrgNum(map);
		}

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   lists = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						lists.add(id);
					}
					map.put("lists", lists);
				}
			}
			list = infoService.allCountByCar(map);
			count = infoService.allCountByCarNum(map);
		}
		return new Page<CountInfo>(list, count, limit);
	}
	@LogAnnotation(description="综合统计 按周 按月查询" )
	@ApiOperation(value = "综合统计 按周 按月查询 ", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:消息id}")
	@RequestMapping(value = "web/statistics/synthesize", method = RequestMethod.GET)
	@ResponseBody
	public Response allCountAndMW(String show_type, String start_time,
			String end_time, HttpServletRequest request) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<CountInfo> list = new ArrayList<CountInfo>();

		String[] car_ids = request.getParameterValues("car_ids[]");
		String[] org_ids = request.getParameterValues("org_ids[]");
		
		List<String> listDate = new ArrayList<String>();
		Date dBegin = new SimpleDateFormat("yyyy-MM-dd").parse(start_time);
		Date dEnd = new SimpleDateFormat("yyyy-MM-dd").parse(end_time);
		listDate = FindDatesUtils.findDates(dBegin, dEnd);
		
		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time + " 00:00:00");
			map.put("endDate", end_time + " 23:59:59");
		}
		
			if (org_ids != null && org_ids.length > 0) {
				map.put("org_list", org_ids);
			}
			
			if (car_ids != null  && car_ids.length > 0) {
				map.put("car_list", car_ids);
			}

			if ((car_ids == null || car_ids.length == 0) && (org_ids == null || org_ids.length == 0)) {
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
			}
			
			List<Car> carList = carService.getCarIds(map);
			List<Long>   lists = new ArrayList<Long>();
			if(carList != null){
				for(Car car:carList){
					long id  = car.getId();
					lists.add(id);
				}
				if(lists.size() > 0){
					map.put("lists", lists);
				}
			}
			
			list = infoService.allCountByTimeAndMW(map);
		
		
		List<CountInfo> listInfo = new ArrayList<CountInfo>();
		List<CountInfo> infos = new ArrayList<CountInfo>();
		
		for(int j = 0; j<listDate.size();j++){
			CountInfo countInfo = new CountInfo();
			String date = listDate.get(j);
			countInfo.setDate_time(date);
			listInfo.add(countInfo);
		}
		
		for(CountInfo cInfo : listInfo) {
			String date1 = cInfo.getDate_time();
			boolean exists = false;
			
			for(CountInfo info2 : list) {
				if(info2 == null) {
					break;
				}
				
				String date2 = info2.getDate_time();
				if(date1.equals(date2)) {
					infos.add(info2);
					exists = true;
				}
				
				if(exists) {
					break;
				}
			}
			
			if(!exists) {
				infos.add(cInfo);
			}
		}
		
		if (infos != null) {
			return new ListResponse<CountInfo>(infos);
		} else {
			return new FailedResponse("请求数据为空！");
		}
	}
	
	
	
	@LogAnnotation(description="综合统计数据导出" )
	@ApiOperation(value = "综合统计数据导出", httpMethod = "GET", response = Result.class, notes = "必须的参数： hide_org: 机构id,car_id:车辆id//多台时，用号隔开driver:驾驶员姓名,startDate:查询开始时间,endDate:查询结束时间")
	@RequestMapping(value = "web/statistics/synthesize/export", method = RequestMethod.GET)
	@ResponseBody
	public Object allCountEx(String show_type, String start_time,
			String end_time, HttpServletResponse response,
			HttpServletRequest request) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<CountInfo> list = new ArrayList<CountInfo>();
		String org_ids_str = request.getParameter("org_ids");
		String[] org_ids = null;
		if (StringUtils.isNotBlank(org_ids_str)) {
			org_ids = org_ids_str.split(",");
		}
		String car_ids_str = request.getParameter("car_ids");
		String[] car_ids = null;
		
		if (StringUtils.isNotBlank(car_ids_str) && !"null".equals(car_ids_str)) {
			car_ids = car_ids_str.split(",");
		}

		if (StringUtils.isNotBlank(start_time)
				&& StringUtils.isNotBlank(end_time)) {
			map.put("startDate", start_time);
			map.put("endDate", end_time);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 文件名
		String fileName = "综合统计数据数据" + sdf.format(System.currentTimeMillis())
				+ ".xls";
		// sheet名
		String sheetName = "数据明细";

		String[] title = null;
		String[][] values = null;

		if ("car".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarIds(map);
				List<Long>   lists = new ArrayList<Long>();
				if(carList != null){
					for(Car car:carList){
						long id  = car.getId();
						lists.add(id);
					}
					map.put("lists", lists);
				}
			}
			list = infoService.allCountByCarAndMW(map);
			values = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				title = new String[] { "车牌号", "超速", "越界", "未入库", "违停", "无单违规",
						"非规定时段", "里程", "油耗", "时长", "违章" };

				CountInfo ci = list.get(i);
				values[i] = new String[title.length];
				// 将对象内容转换成string
				values[i][0] = ci.getCar_no();
				
				Integer overSpeed = ci.getOverspeed();
				if(overSpeed == null){
					overSpeed = 0;
				}
				values[i][1] = String.valueOf(overSpeed);
				Integer outSide = ci.getOutside();
				if(outSide == null){
					outSide = 0;
				}
				values[i][2] = String.valueOf(outSide);
				
				Integer notreturn = ci.getNot_return();
				if(notreturn == null){
					notreturn = 0;
				}
				values[i][3] = String.valueOf(notreturn);
				
				Integer foul_park = ci.getFoul_park();
				if(foul_park == null){
					foul_park = 0;
				}
				values[i][4] = String.valueOf(foul_park);
				
				Integer foul_task = ci.getFoul_task();
				if(foul_task == null){
					foul_task = 0;
				}
				values[i][5] = String.valueOf(foul_task);
				
				Integer foul_time = ci.getFoul_time();
				if(foul_time == null){
					foul_time = 0;
				}
				values[i][6] = String.valueOf(foul_time);
				
				Double mileage = ci.getMileage();
				if(mileage == null){
					mileage = 0.0;
				}
				values[i][7] = String.valueOf(mileage);
				
				Double fuel = ci.getFuel();
				if(fuel == null){
					fuel = 0.0;
				}
				values[i][8] = String.valueOf(fuel);
				
				Double duration = ci.getDuration();
				if(duration == null){
					duration = 0.0;
				}
				values[i][9] = String.valueOf(duration);
				
				Integer break_rule = ci.getBreak_rule();
				if(break_rule == null){
					break_rule = 0;
				}
				values[i][10] = String.valueOf(break_rule);
			}
		}

		if ("org".equals(show_type)) {
			if (org_ids != null) {
				map.put("org_list", org_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg  != null){
					map.put("list", listOrg);
				}
			}
			list = infoService.allCountByOrgAndMW(map);
			values = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				title = new String[] { "部门", "上线率", "超速", "越界", "未入库", "违停",
						"无单违规", "非规定时段", "里程", "油耗", "时长", "违章" };

				CountInfo ci = list.get(i);
				values[i] = new String[title.length];
				// 将对象内容转换成string

				values[i][0] = ci.getOrg_name();
				Double online = ci.getOnline_rate();
				values[i][1] = String.valueOf(online);
				Integer overSpeed = ci.getOverspeed();
				values[i][2] = String.valueOf(overSpeed);
				Integer outSide = ci.getOutside();
				values[i][3] = String.valueOf(outSide);
				Integer notreturn = ci.getNot_return();
				values[i][4] = String.valueOf(notreturn);
				Integer foul_park = ci.getFoul_park();
				values[i][5] = String.valueOf(foul_park);
				Integer foul_task = ci.getFoul_task();
				values[i][6] = String.valueOf(foul_task);
				Integer foul_time = ci.getFoul_time();
				values[i][7] = String.valueOf(foul_time);
				Double mileage = ci.getMileage();
				values[i][8] = String.valueOf(mileage);
				Double fuel = ci.getFuel();
				values[i][9] = String.valueOf(fuel);
				Double duration = ci.getDuration();
				values[i][10] = String.valueOf(duration);
				Integer break_rule = ci.getBreak_rule();
				values[i][11] = String.valueOf(break_rule);
			}

		}

		if ("time".equals(show_type)) {
			if (car_ids != null) {
				map.put("car_list", car_ids);
			}else{
				List<Long> listOrg = getOrg(request);
				if(listOrg != null){
					map.put("list", listOrg);
				}
				List<Car> carList = carService.getCarListByOrg(map);
				List<Long>   lists = new ArrayList<Long>();
				if(carList.size() > 0){
					for(Car car:carList){
						long id  = car.getId();
						lists.add(id);
					}
					map.put("lists", lists);
				}
			}
			list = infoService.allCountByTimeAndMW(map);
			values = new String[list.size()][];
			for (int i = 0; i < list.size(); i++) {
				title = new String[] { "时间", "上线率", "超速", "越界", "未入库", "违停",
						"无单违规", "非规定时段", "里程", "油耗", "时长", "违章" };

				CountInfo ci = list.get(i);
				values[i] = new String[title.length];
				// 将对象内容转换成string

				values[i][0] = ci.getDate_time();
				Double online = ci.getOnline_rate();
				values[i][1] = String.valueOf(online);
				Integer overSpeed = ci.getOverspeed();
				values[i][2] = String.valueOf(overSpeed);
				Integer outSide = ci.getOutside();
				values[i][3] = String.valueOf(outSide);
				Integer notreturn = ci.getNot_return();
				values[i][4] = String.valueOf(notreturn);
				Integer foul_park = ci.getFoul_park();
				values[i][5] = String.valueOf(foul_park);
				Integer foul_task = ci.getFoul_task();
				values[i][6] = String.valueOf(foul_task);
				Integer foul_time = ci.getFoul_time();
				values[i][7] = String.valueOf(foul_time);
				Double mileage = ci.getMileage();
				values[i][8] = String.valueOf(mileage);
				Double fuel = ci.getFuel();
				values[i][9] = String.valueOf(fuel);
				Double duration = ci.getDuration();
				values[i][10] = String.valueOf(duration);
				Integer break_rule = ci.getBreak_rule();
				values[i][11] = String.valueOf(break_rule);
			}

		}
		if (list.size() == 0) {
			return null;
		}
		HSSFWorkbook wb = ExcelUtils.getHSSFWorkbook(sheetName, title, values,
				null);

		// 将文件存到指定位置
		try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ("ok");

	}
	
	
	public List<Long> getOrg(HttpServletRequest request){
		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(
					userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				return org_list;
			}
			
		}
		return null;

	}
	
	public List<Long> getAllOrg(HttpServletRequest request){
		User u = new User();
		String id1 = request.getParameter("keyid");
		u.setId(Long.parseLong(id1));
		User user = userService.findBy(u);

		Long org = user.getOrg();
		Organization userOrg = organizationService.findById(org);
			List<Long> org_list = organizationService.getOrganizationUser(
					userOrg, org);
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(org);
				return org_list;
			}
			return null;
	}
	
	
	@LogAnnotation(description="2.0分页查询消息列表" )
	@ApiOperation(value = "2.0分页查询消息列表", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0,hide_org: 机构id,car_id:车辆id,//多台时，用号隔开type:违规类型,driver:驾驶员姓名,startDate:查询开始时间, endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/find/page")
	@RequestMapping(value = "web/info/find/page", method = RequestMethod.POST)
	@ResponseBody
	public Page infoPage(Integer limit, int pageIndex,
			String find_type,HttpServletRequest request)
			throws ParseException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		User u1 = new User();
		String id1 = request.getParameter("keyid");
		u1.setId(Long.parseLong(id1));
		User u = userService.findBy(u1);
		String u_org = Long.toString(u.getOrg());

		Organization userOrg = organizationService.findById(Long.parseLong(u_org));
		if (userOrg.getParent() != null) {
			List<Long> org_list = organizationService.getOrganizationUser(
					userOrg, Long.parseLong(u_org));
			if (!CollectionUtils.isEmpty(org_list)) {
				org_list.add(Long.parseLong(u_org));
				map.put("list", org_list);
			} else {
				map.put("org", u_org);
			}
		}
		
		if("1".equals(find_type)){
			map.put("find_type_1", find_type);
		}if("2".equals(find_type)){
			map.put("find_type_2", find_type);
		}
		
		List<InfoCenter> list = infoService.infoPage(map);
		int recodes = infoService.infoPageCount(map);
		return new Page<InfoCenter>(list, recodes, limit);

	}
	
	
	@LogAnnotation(description="2.0消息状态修改接口" )
	@ApiOperation(value = "2.0消息状态修改接口", httpMethod = "POST", response = Result.class, notes = "必须的参数： limit:15,pageIndex:0,hide_org: 机构id,car_id:车辆id,//多台时，用号隔开type:违规类型,driver:驾驶员姓名,startDate:查询开始时间, endDate:查询结束时间")
	@RequiresPermissions(value = "web/info/update/status")
	@RequestMapping(value = "web/info/update/status", method = RequestMethod.POST)
	@ResponseBody
	public Response updateInfoStatus(@RequestParam(value = "ids[]") String[] ids,String status,
			String find_type,HttpServletRequest request)
			throws ParseException {

		HashMap<String, Object> map = new HashMap<String, Object>();
		if(ids != null){
			map.put("list", ids);
		}else{
			return new FailedResponse("请选择需要更改的行！");
		}
		if(StringUtils.isNotBlank(status)){
			map.put("status", status);
		}
		infoService.updateInfoStatus(map);
		return new SuccessResponse();
	}
	
	
}
