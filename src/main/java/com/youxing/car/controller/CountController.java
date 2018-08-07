package com.youxing.car.controller;

import com.youxing.car.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.dao.BehaviorDao;
import com.youxing.car.entity.Alm;
import com.youxing.car.entity.AlmDetail;
import com.youxing.car.entity.AlmNotice;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CarAlmType;
import com.youxing.car.entity.CountEntity;
import com.youxing.car.entity.Device;
import com.youxing.car.entity.OrgCount;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.Selects;
import com.youxing.car.entity.User;
import com.youxing.car.export.XlsCodeExporter;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.ObjectResponse;
import com.youxing.car.response.Response;
import com.youxing.car.service.BehaviorService;
import com.youxing.car.service.CarService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.ReduceService;
import com.youxing.car.service.UserService;
import com.youxing.car.utils.other.QueryUtilsWeb;

/**
 * @author mars
 * @date 2017年5月26日 下午3:09:57
 */
@Api(value = "count", description = "统计管理")
@Controller
public class CountController {
	@Resource
	private OrganizationService organizationService;
	@Resource
	private UserService userService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private CarService carService;
	@Resource
	private InfoCenterService infoCenterService;
	@Resource
	private BehaviorService behaviorService;
	@Resource
	private QueryUtilsWeb queryUtilsWeb;
	@Resource
	private BehaviorDao behaviorDao;

	private static Logger LOGGER = LoggerFactory.getLogger(CountController.class);

	/**
	 * 
	 * @author mars
	 * @date 2017年5月27日 下午5:03:33
	 * @Description: TODO(用户当前机构（以及子机构）的四急和油耗数据统计)
	 * @param @param uid
	 * @param @param year
	 * @param @param month
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="四急和油耗数据统计（总的）" )
	@ApiOperation(value = "四急和油耗数据统计（总的）", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = OrgCount.class) })
	@RequestMapping(value = "app/org/car/all", method = RequestMethod.GET)
	@ResponseBody
	public Result countAllOrg(@ApiParam(value = "用户id") Long uid, @ApiParam(value = "年份") Integer year, @ApiParam(value = "月份") Integer month) {
		try {
			if (uid == null || year == null || month == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			List<OrgCount> oc = new ArrayList<OrgCount>();
			List<Long> child = organizationService.getChildren(org);
			child.add(org);
			Map<String, Object> map = new HashMap<>();
			map.put("list", child);
			List<String> device = deviceService.listOrgDevice(map);
			if (CollectionUtils.isNotEmpty(device)) {
				String[] time = DateUtils.getDayOfMonth(year, month);
				map.clear();
				map.put("devices", device);
				map.put("start", time[0]);
				map.put("end", time[1]);
				oc = behaviorService.findListForReduceMonth(map);
			}
			if (CollectionUtils.isNotEmpty(oc)) {
				return Result.success(oc.get(0));
			}
			return Result.success(new OrgCount());
		} catch (Exception e) {
			LOGGER.error("app/org/car/all", e);
			return Result.error();
		}

	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月27日 下午5:04:16
	 * @Description: TODO(当前机构以及子机构数据汇总)
	 * @param @param uid
	 * @param @param year
	 * @param @param month
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="四急和油耗数据统计（每个机构））" )
	@ApiOperation(value = "四急和油耗数据统计（每个机构）", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = OrgCount.class) })
	@RequestMapping(value = "app/org/count", method = RequestMethod.GET)
	@ResponseBody
	public Result countByOrg(@ApiParam(value = "用户id") Long uid, @ApiParam(value = "年份") Integer year, @ApiParam(value = "月份") Integer month) {
		try {
			if (uid == null || year == null || month == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			List<OrgCount> list = new ArrayList<OrgCount>();
			List<Long> child = organizationService.getChildren(org);
			child.add(org);
			Map<String, Object> map = new HashMap<>();
			String[] time = DateUtils.getDayOfMonth(year, month);
			for (Long ch : child) {
				map.clear();
				map.put("org", ch);
				Organization organ = organizationService.findById(ch);
				List<String> device = deviceService.listOrgDevice(map);
				if (CollectionUtils.isNotEmpty(device)) {
					map.put("devices", device);
					map.put("start", time[0]);
					map.put("end", time[1]);
					List<OrgCount> oc = behaviorService.findListForReduceMonth(map);
					if (CollectionUtils.isNotEmpty(oc)) {
						oc.get(0).setOrg(ch);
						oc.get(0).setOrgName(organ.getName());
						list.add(oc.get(0));
					}
				}
			}
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/org/count", e);
			return Result.error();
		}

	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月27日 下午5:04:39
	 * @Description: TODO(某机构下所有的车的数据)
	 * @param @param uid
	 * @param @param org
	 * @param @param year
	 * @param @param month
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="四急和油耗数据统计（每个机构下面的车）" )
	@ApiOperation(value = "四急和油耗数据统计（每个机构下面的车）", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = OrgCount.class) })
	@RequestMapping(value = "app/org/car/count", method = RequestMethod.GET)
	@ResponseBody
	public Result countByOrgCar(@ApiParam(value = "用户id") Long uid, @ApiParam(value = "机构id") Long org, @ApiParam(value = "年份") Integer year, @ApiParam(value = "月份") Integer month) {
		try {
			if (uid == null || year == null || month == null || org == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}

			List<OrgCount> list = new ArrayList<OrgCount>();
			Map<String, Object> map = new HashMap<>();
			String[] time = DateUtils.getDayOfMonth(year, month);
			map.put("org", org);
			List<String> device = deviceService.listOrgDevice(map);
			if (CollectionUtils.isNotEmpty(device)) {
				map.put("devices", device);
				map.put("start", time[0]);
				map.put("end", time[1]);
				list = behaviorService.findListForReduceDevice(map);
				for (OrgCount orgc : list) {
					String de = orgc.getDay();
					map.clear();
					map.put("status", Constant.ADD_STATUS);
					map.put("device", de);
					Device device_car = deviceService.findByMap(map);
					if (device_car != null) {
						Car ca = carService.findById(device_car.getCar_id());
						orgc.setCar_no(ca.getCar_no());
					}
				}
			}
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/org/car/count", e);
			return Result.error();
		}

	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月27日 下午5:05:00
	 * @Description: TODO(当前用户机构下的（子机构）报警统计)
	 * @param @param uid
	 * @param @param year
	 * @param @param month
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="车辆报警统计" )
	@ApiOperation(value = "车辆报警统计", httpMethod = "GET", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = AlmNotice.class) })
	@RequestMapping(value = "app/org/car/notice/count", method = RequestMethod.GET)
	@ResponseBody
	public Result almAllOrg(@ApiParam(value = "用户id") Long uid, @ApiParam(value = "年份") Integer year, @ApiParam(value = "月份") Integer month) {
		try {
			if (uid == null || year == null || month == null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			AlmNotice an = new AlmNotice();
			List<Long> child = organizationService.getChildren(org);
			child.add(org);
			Map<String, Object> map = new HashMap<>();
			map.put("list", child);
			List<String> device = deviceService.listOrgDevice(map);
			String[] time = DateUtils.getDayOfMonth(year, month);
			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put("list", child);
			maps.put("start", (time[0]));
			maps.put("end", (time[1]));
			List<CarAlmType> carAlm = infoCenterService.countByGroup(maps);
			if (CollectionUtils.isEmpty(carAlm)) {
				an.setBk(0);
				an.setOver(0);
				an.setSpeed(0);
				an.setOther(0);
				an.setOutage(0);
			} else {
				for (CarAlmType cat : carAlm) {
					String type = cat.getType();
					int num = cat.getNum();
					if ("0".equals(type)) {
						an.setBk(num);
					}
					if ("1".equals(type)) {
						an.setOver(num);
					}
					if ("2".equals(type)) {
						an.setSpeed(num);
					}
					if ("4".equals(type)) {
						an.setOutage(num);
					}
					if ("3".equals(type) || "5".equals(type) || "6".equals(type) || "7".equals(type)) {
						int other = num;
						if (an.getOther() != null) {
							other = other + an.getOther();
						}
						an.setOther(other);
					}

				}
			}
			an.setCar(device.size());
			return Result.success(an);
		} catch (Exception e) {
			LOGGER.error("app/org/car/notice/count", e);
			return Result.error();
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月27日 下午5:05:31
	 * @Description: TODO(每一类报警的统计)
	 * @param @param uid
	 * @param @param alm
	 * @param @param org
	 * @param @param year
	 * @param @param month
	 * @param @return 设定文件
	 * @return Result 返回类型
	 * @throws
	 */
	@LogAnnotation(description="车辆分类报警统计" )
	@ApiOperation(value = "车辆分类报警统计", httpMethod = "GET", notes = "返回的json中org 代表机构数据 detail代表报警的日统计数据", response = Result.class)
	@ApiResponses(value = { @ApiResponse(code = 10000, message = "操作成功", response = AlmDetail.class) })
	@RequestMapping(value = "app/org/car/alm", method = RequestMethod.GET)
	@ResponseBody
	public Result countOrgCarNotice(@ApiParam(value = "用户id") Long uid, @ApiParam(value = "报警类型 0违章，1越界，2超速，3超时，4断电") String alm,
			@ApiParam(value = "年份") Integer year, @ApiParam(value = "月份") Integer month) {
		try {
			if (uid == null || year == null || month == null || StringUtils.isBlank(alm) ) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			User user = userService.findById(uid);
			if (user == null) {
				return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
			}
			Long org = user.getOrg();
			if (org == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			Organization loginOrg = organizationService.findById(org);
			if (loginOrg == null) {
				return Result.instance(ResponseCode.no_org.getCode(), ResponseCode.no_org.getMsg());
			}
			Map<String, Object> maps = new HashMap<String, Object>();
			List<AlmDetail> list = new ArrayList<AlmDetail>();
			List<Alm> lists = new ArrayList<Alm>();
			String[] time = DateUtils.getDayOfMonth(year, month);
			maps.put("parent", org);
			maps.put("status", Constant.ADD_STATUS);
			List<Organization> parent = organizationService.listBy(maps);
			parent.add(loginOrg);
			maps.clear();
			List<Long> child = organizationService.getChildren(org);
			child.add(org);
			Map<String, Object> map = new HashMap<>();
			map.put("list", child);
			for (Organization ch : parent) {
				AlmDetail an = new AlmDetail();
				an.setOrgName("" + ch.getName());
				map.clear();
				//map.put("org", ch.getId());
				List<Long> childs1 = organizationService.getChildren(ch.getId());
				childs1.add(ch.getId());
				map.put("list", childs1);
				if(ch.getId() == org){
					map.clear();
					map.put("org", ch.getId());
				}
				List<String> device = deviceService.listOrgDevice(map);
				an.setCar(device.size());
				if (CollectionUtils.isNotEmpty(device)) {
					map.clear();
					List<Long> childs = organizationService.getChildren(ch.getId());
					childs.add(ch.getId());
					map.put("list", childs);
					if(ch.getId() == org){
						map.clear();
						map.put("org", ch.getId());
					}
					map.put("illegal_type", alm);
					map.put("start", time[0]);
					map.put("end", time[1]);
					int typeCount = infoCenterService.countCarAlm(map);
					an.setAlmCar(typeCount);
				} else {
					an.setCar(0);
					an.setAlm(0);
					an.setAlmCar(0);
				}
				list.add(an);
			}
			map.clear();
			if (CollectionUtils.isNotEmpty(child)) {
				map.put("list", child);
			}
			map.put("start", time[0]);
			map.put("end", time[1]);
			map.put("illegal_type", alm);
			lists = infoCenterService.countAlmByDay(map);
			maps.put("org", list);
			maps.put("detail", lists);
			return Result.success(maps);
		} catch (Exception e) {
			LOGGER.error("app/org/car/alm", e);
			return Result.error();
		}

	}
	
	
	/**
	 * ================================web
	 */
	
	@Resource
	private ReduceService reduceService;
	@Resource
	private XlsCodeExporter xlsCodeExporter;

	/**
	 * 
	 * @author mars
	 * @date 2017年5月23日 下午2:03:31
	 * @Description: TODO(统计机构下面的车辆四急和里程油耗)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping("count/org")
	public String orglist() {
		return "count/org";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月23日 下午2:04:22
	 * @Description: TODO(统计机构下面的汽车的四急，里程，油耗)
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	@RequestMapping("count/car")
	public String carlist() {
		return "count/car";
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月25日 上午9:16:50
	 * @Description: TODO(统计机构或者部门)
	 * @param @param org
	 * @param @param start
	 * @param @param end
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="统计机构或者部门" )
 	@ApiOperation(value = "统计机构或者部门", httpMethod = "POST", response = Result.class, notes = "必须的参数： org:机构idstart:查询开始时间end:查询结束时间")
	@RequestMapping(value = "web/count/all/org", method = RequestMethod.POST)
	@ResponseBody
	public Response countByOrgAll(Long org, String start, String end) {
		try {
			List<CountEntity> ceList = new ArrayList<CountEntity>();
			List<String> device = new ArrayList<String>();
			List<Long> child = new ArrayList<Long>();
			if (org == null || StringUtils.isBlank(start) || StringUtils.isBlank(end)) {
				return new FailedResponse("缺少参数！");
			}
			Organization parent = organizationService.findById(org);
			if (parent == null) {
				return new FailedResponse("查询不到这个机构或部门！");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			boolean common = DateUtilsWeb.isCommonMonth(start, end);
			long st = DateUtils.getDateStart(sdf.parse(start)).getTime();
			long en = DateUtils.getDateEnd(sdf.parse(end)).getTime();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("org", org);
			device = deviceService.listOrgDevice(map);
			if (CollectionUtils.isNotEmpty(device)) {
				if (common) {
					reduceService.reducebyId(device, st, en, ceList, parent.getName(), org);
				} else {
					reduceService.reduceAll(device, start, st, en, ceList, parent.getName(), org, false);
				}
			}
			map.clear();
			map.put("parent", org);
			map.put("status", Constant.ADD_STATUS);
			List<Organization> list = organizationService.listBy(map);
			if (CollectionUtils.isNotEmpty(list)) {
				for (Organization o : list) {
					child.clear();
					child = organizationService.getChildren(o.getId());
					child.add(o.getId());
					map.clear();
					map.put("list", child);
					device.clear();
					device = deviceService.listOrgDevice(map);
					if (CollectionUtils.isNotEmpty(device)) {
						if (common) {
							reduceService.reducebyId(device, st, en, ceList, o.getName(), o.getId());
						} else {
							reduceService.reduceAll(device, start, st, en, ceList, o.getName(), o.getId(), false);
						}
					} else {
						CountEntity ce = new CountEntity(o.getName(), o.getId());
						ceList.add(ce);
					}
				}
			}
			return new ListResponse<CountEntity>(ceList);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月25日 上午9:17:08
	 * @Description: TODO(导出数据筛选)
	 * @param @param org
	 * @param @param start
	 * @param @param query
	 * @param @param end
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@LogAnnotation(description="机构油耗里程等数据导出" )
 	@ApiOperation(value = "机构油耗里程等数据导出", httpMethod = "POST", response = Result.class, notes = "必须的参数： org:org[],start:查询开始时间end:查询结束时间query:")
	@RequestMapping(value = "web/count/org/export", method = RequestMethod.POST)
	@ResponseBody
	public Response exprt(@RequestParam(value = "org[]") Long[] org, String start, Long query, String end) {
		try {
			List<String> device = new ArrayList<String>();
			List<Long> child = new ArrayList<Long>();
			if (org.length == 0 || StringUtils.isBlank(start) || StringUtils.isBlank(end) || query == null) {
				return new FailedResponse("参数不匹配！");
			}
			boolean common = DateUtilsWeb.isCommonMonth(start, end);
			Map<String, List<CountEntity>> maps = new HashMap<String, List<CountEntity>>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long st = DateUtils.getDateStart(sdf.parse(start)).getTime();
			long en = DateUtils.getDateEnd(sdf.parse(end)).getTime();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < org.length; i++) {
				Organization organ = organizationService.findById(org[i]);
				if (organ != null) {
					List<CountEntity> ceList = new ArrayList<CountEntity>();
					map.clear();
					child.clear();
					device.clear();
					if (query != org[i]) {
						child = organizationService.getChildren(org[i]);
						child.add(org[i]);
						map.put("list", child);
						device = deviceService.listOrgDevice(map);
					} else {
						map.put("org", org[i]);
						device = deviceService.listOrgDevice(map);
					}
					if (CollectionUtils.isNotEmpty(device)) {
						if (common) {
							reduceService.reduceDetail(device, start, st, en, ceList, organ.getName());
						} else {
							reduceService.reduceAll(device, start, st, en, ceList, organ.getName(), org[i], true);
						}
					}
					if (CollectionUtils.isNotEmpty(ceList)) {
						maps.put(org[i].toString(), ceList);
					} else {
						ceList.add(new CountEntity(organ.getName(), 0.0f, 0.0f, 0, 0, 0, org[i]));
						maps.put(org[i].toString(), ceList);
					}
				}
			}
			String name = System.currentTimeMillis() + Constant.EXPORT_FILE_TYPE;
			if (common) {
				xlsCodeExporter.exportDetail(maps, name);
			} else {
				xlsCodeExporter.export(maps, name);
			}
			return new ObjectResponse<String>(name);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误！");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月25日 上午9:17:19
	 * @Description: TODO(文件下载)
	 * @param @param request
	 * @param @param response
	 * @param @param name 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@LogAnnotation(description="下载导出的excel" )
 	@ApiOperation(value = "下载导出的excel", httpMethod = "GET", response = Result.class, notes = "必须的参数：name")
	@RequestMapping(value = "web/download/excel", method = RequestMethod.GET)
 	public void downloadExcel(HttpServletRequest request, HttpServletResponse response, String name) {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			response.reset();
			response.setContentType("application/vnd.ms-excel");
			byte[] bytes = name.getBytes("UTF-8");
			name = new String(bytes, "ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=" + name);
			File file = new File("/home/export/" + name);
			response.addHeader("Content-Length", "" + file.length());
			fis = new BufferedInputStream(new FileInputStream(file));
			fos = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[102400];
			int byteRed = -1;
			while ((byteRed = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteRed);
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年5月25日 上午10:11:28
	 * @Description: TODO(加载机构下绑定obd的汽车)
	 * @param @param id
	 * @param @return 设定文件
	 * @return List<Selects> 返回类型
	 * @throws
	 */
	@LogAnnotation(description="获取机构下绑定obd的汽车" )
 	@ApiOperation(value = "获取机构下绑定obd的汽车", httpMethod = "GET", response = Result.class, notes = "必须的参数：{id:机构id,parent:机构父级id}")
	@RequestMapping(value="web/org/car/load/obd", method = RequestMethod.GET)
	@ResponseBody
	public List<Selects> getOrgCar(String id,String parent) {
		List<Selects> list = new ArrayList<Selects>();
		if (StringUtils.isBlank(id)) {
			return list;
		}
		Selects sl = new Selects();
		//sl.setValue("0");
		//sl.setText("全选");
		if(StringUtils.isBlank(parent)){			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("org", Long.parseLong(id));
			map.put("status", Constant.ADD_STATUS);
			map.put("car_status", Constant.ADD_STATUS);
			map.put("obd", Constant.BIND_OBD);
			list = carService.listOrg(map);
		}else{
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<Long> child = organizationService.getChildren(Long.parseLong(id));
			child.add(Long.parseLong(id));
			map.put("list", child);
			map.put("status", Constant.ADD_STATUS);
			map.put("car_status", Constant.ADD_STATUS);
			map.put("obd", Constant.BIND_OBD);
			list = carService.listOrg(map);
		}
		/*if(CollectionUtils.isNotEmpty(list)){
			list.add(0, sl);
		}*/
		return list;
	}
	/**
	 * @throws ParseException 
	 * 
	* @author mars   
	* @date 2017年5月25日 下午3:18:14 
	* @Description: (统计汽车数据) 
	* @param @param car
	* @param @param start
	* @param @param end
	* @param @return    设定文件 
	* @return Response    返回类型 
	* @throws
	 */
	@LogAnnotation(description="分页查询车辆油耗里程等数据统计信息" )
 	@ApiOperation(value = "分页查询车辆油耗里程等数据统计信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：  limit:20,pageIndex:0,starts:查询开始时间,end:查询结束时间,org:机构id,car_id:车辆ids//多台时，用号隔开")
	@RequestMapping(value = "web/count/by/car", method = RequestMethod.POST)
	@ResponseBody
	public Page countByCar(int limit, int pageIndex,Long org, String starts, String end,String car_id,HttpServletRequest request) throws ParseException {
		List<CountEntity> ceList = new ArrayList<CountEntity>();
		List<Selects> list = new ArrayList<Selects>();
		List<Long> cars = new ArrayList<Long>();
		/*if (car.length == 0 || StringUtils.isBlank(starts) || StringUtils.isBlank(end)) {
			return new Page<CountEntity>(ceList, 0, limit);
		}*/
		int recodes= 0;
		HashMap<String, Object> maps = new HashMap<String, Object>();
		if(StringUtils.isEmpty(car_id)){
			maps.put("id", "");
		}else{
			String ids[] = car_id.split(",");
			List<Object> idsList = new ArrayList<Object>();
			for (int i = 0; i < ids.length; i++) {
				idsList.add(Long.parseLong(ids[i]));
			}
			maps.put("list",  idsList);
		}
		//maps.put("org", org);
		if(org == null) {
			//User u = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
			User u1 =new User();
			String id1 = request.getParameter("keyid");
			u1.setId(Long.parseLong(id1));
			User u = userService.findBy(u1);
			Long u_org = u.getOrg();
			queryUtilsWeb.queryByOrg(u_org, maps);
		} else {
			queryUtilsWeb.queryByOrg(org, maps);
		}

		maps.put("status", Constant.ADD_STATUS);
		maps.put("car_status", Constant.ADD_STATUS);
		maps.put("obd", Constant.BIND_OBD);
		
		list = carService.listByOrg(maps);
		for(Selects sel : list){
			cars.add(Long.parseLong(sel.getValue()));
		}
			
			
		if(StringUtils.isEmpty(starts)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int month = calendar.get(Calendar.MONDAY) - 1;
			calendar.set(Calendar.MONDAY, month);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date date = calendar.getTime();
			starts = new SimpleDateFormat("yyyy-MM-dd").format(date);
		}
		
		if(StringUtils.isEmpty(end)) {
			end = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long st = DateUtils.getDateStart(sdf.parse(starts)).getTime();
		long en = DateUtils.getDateEnd(sdf.parse(end)).getTime();
		Map<String, Object> map = new HashMap<String, Object>();
		if(!CollectionUtils.isNotEmpty(list)){
			return new Page<CountEntity>(ceList, 0, limit);
		}else{
			map.put("list", cars);
			List<String> device = deviceService.listDevice(map);
			if (CollectionUtils.isNotEmpty(device)) {
				recodes = reduceService.reduceCar(device, st, en, pageIndex, limit, ceList);
			}
			return new Page<CountEntity>(ceList, recodes, limit);
		}
}
	/**
	 * 
	* @author mars   
	* @date 2017年5月25日 下午3:17:58 
	* @Description: TODO(汽车数据导出) 
	* @param @param car
	* @param @param start
	* @param @param end
	* @param @return    设定文件 
	* @return Response    返回类型 
	* @throws
	 */
	@LogAnnotation(description="车辆油耗里程等统计数据导出" )
 	@ApiOperation(value = "车辆油耗里程等统计数据导出", httpMethod = "POST", response = Result.class, notes = "必须的参数：  limit:20,pageIndex:0,starts:查询开始时间,end:查询结束时间,org:机构id,car_id:车辆ids//多台时，用号隔开")
	@RequestMapping(value = "web/count/by/car/export", method = RequestMethod.POST)
	@ResponseBody
	public Response exportByCar(@RequestParam(value = "car[]") Long[] car, String start, String end) {
		try {
			if (car.length == 0 || StringUtils.isBlank(start) || StringUtils.isBlank(end)) {
				return new FailedResponse("缺少参数！");
			}
			Map<String, List<CountEntity>> maps = new LinkedHashMap<String, List<CountEntity>>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			boolean common = DateUtilsWeb.isCommonMonth(start, end);
			long st = DateUtils.getDateStart(sdf.parse(start)).getTime();
			long en = DateUtils.getDateEnd(sdf.parse(end)).getTime();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", car);
			List<String> device = deviceService.listDevice(map);
			if (CollectionUtils.isNotEmpty(device)) {
				Map<String,Object> mapt = new HashMap<String,Object>();
				mapt.put("devices",device);
				mapt.put("start", DateUtils.getLong2Date(st));
				mapt.put("end", DateUtils.getLong2Date(en));
				List<CountEntity> tmpList = behaviorDao.findListForReduceDeviceWeb(mapt);
				for (CountEntity ce : tmpList) {
					List<CountEntity> ceList = new ArrayList<CountEntity>();
					reduceService.reduceCar(ce.getDay(), start, st, en, ceList, common);
					if (CollectionUtils.isNotEmpty(ceList)) {
						maps.put(ce.getDay(), ceList);
					}
				}
			}
			String name = System.currentTimeMillis() + Constant.EXPORT_FILE_TYPE;
			if (common) {
				xlsCodeExporter.exportDetailCar(maps, name);
			} else {
				xlsCodeExporter.exportCar(maps, name);
			}
			return new ObjectResponse<String>(name);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误");
		}
	}
	
	
	/**
	 * 
	 * @author zzb
	 * @date 2017年7月6日09:11:28 
	 * @Description: TODO(汽车数据根据条件全导出) 
	 * @param @param car_id
	 * @param @param start
	 * @param @param end
	 * @param @return    设定文件 
	 * @return Response    返回类型 
	 * @throws
	 */
	@LogAnnotation(description="车辆油耗里程统计数据全部导出" )
 	@ApiOperation(value = "车辆油耗里程统计数据全部导出", httpMethod = "POST", response = Result.class, notes = "必须的参数：  limit:20,pageIndex:0,starts:查询开始时间,end:查询结束时间,org:机构id,car_id:车辆ids//多台时，用号隔开")
	@RequestMapping(value = "web/count/by/car/exports", method = RequestMethod.POST)
	@ResponseBody
	public Response exportsByCar(Long org, String start, String end,String car_id) {
		try {
				if (StringUtils.isBlank(start) || StringUtils.isBlank(end)) {
					return new FailedResponse("缺少参数！");
				}
				Map<String, List<CountEntity>> maps = new LinkedHashMap<String, List<CountEntity>>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				boolean common = DateUtilsWeb.isCommonMonth(start, end);
				long st = DateUtils.getDateStart(sdf.parse(start)).getTime();
				long en = DateUtils.getDateEnd(sdf.parse(end)).getTime();
				List<Selects> list = new ArrayList<Selects>();
				List<Long> cars = new ArrayList<Long>();
				Map<String, Object> map = new HashMap<String, Object>();
				if("".equals(car_id)){
					map.put("id", car_id);
				}else{
					String ids[] = car_id.split(",");
					List<Object> idsList = new ArrayList<Object>();
					for (int i = 0; i < ids.length; i++) {
						idsList.add(Long.parseLong(ids[i]));
					}
					map.put("list",  idsList);
				}
				//map.put("org", org);
				queryUtilsWeb.queryByOrg(org, map);
				map.put("status", Constant.ADD_STATUS);
				map.put("car_status", Constant.ADD_STATUS);
				map.put("obd", Constant.BIND_OBD);
				list = carService.listByOrg(map);
				for(Selects sel : list){
					cars.add(Long.parseLong(sel.getValue()));
				}
				map.put("list", cars);
				List<String> device = deviceService.listDevice(map);
				if (CollectionUtils.isNotEmpty(device)) {
					Map<String,Object> mapt = new HashMap<String,Object>();
					mapt.put("devices",device);
					mapt.put("start", DateUtils.getLong2Date(st));
					mapt.put("end", DateUtils.getLong2Date(en));
					List<CountEntity> tmpList = behaviorDao.findListForReduceDeviceWeb(mapt);
					for (CountEntity ce : tmpList) {
						List<CountEntity> ceList = new ArrayList<CountEntity>();
						reduceService.reduceCar(ce.getDay(), start, st, en, ceList, common);
						if (CollectionUtils.isNotEmpty(ceList)) {
							maps.put(ce.getDay(), ceList);
						}
					}
					/*for (String de : device) {
						List<CountEntity> ceList = new ArrayList<CountEntity>();
						reduceService.reduceCar(de, start, st, en, ceList, common);
						if (CollectionUtils.isNotEmpty(ceList)) {
							maps.put(de, ceList);
						}
					}*/
				}
				String name = System.currentTimeMillis() + Constant.EXPORT_FILE_TYPE;
				if (common) {
					xlsCodeExporter.exportDetailCar(maps, name);
				} else {
					xlsCodeExporter.exportCar(maps, name);
				}
				return new ObjectResponse<String>(name);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器内部错误");
		}
	}
}
