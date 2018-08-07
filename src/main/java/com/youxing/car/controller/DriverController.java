package com.youxing.car.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.annotation.TranslationControl;
import com.youxing.car.entity.Apply;
import com.youxing.car.entity.Driver;
import com.youxing.car.entity.Member;
import com.youxing.car.entity.Organization;
import com.youxing.car.entity.User;
import com.youxing.car.page.Page;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.ApplyService;
import com.youxing.car.service.ControlService;
import com.youxing.car.service.DriverService;
import com.youxing.car.service.MemberService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Constant;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.FileUtils;
import com.youxing.car.util.QueryUtils;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "driver", description = "驾驶员管理")
@Controller
public class DriverController {
	@Resource
	private DriverService driverService;
	@Resource
	private UserService userService;
	@Resource
	private QueryUtils queryUtils;
	@Resource
	private ApplyService applyService;
	@Resource
	private ControlService controlService;
	@Resource
	private OrganizationService organizationService;
	@Resource
	private MemberService memberService;


	private static Logger LOGGER = LoggerFactory.getLogger(DriverController.class);
	/**
	 *
	 * @author mars
	 * @date 2017年5月2日 下午2:07:17
	 * @Description: TODO(车辆列表)
	 * @param @param page
	 * @param @param size
	 * @param @param id
	 * @param @return 设定文件
	 * @return Result 返回类型   
	 * @throws
	 */
	@SuppressWarnings("unused")
	@ApiOperation(value = "驾驶员列表", httpMethod = "POST", response = Result.class)
	@ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = Driver.class) })
	@RequestMapping(value = "app/driver/list", method = RequestMethod.POST)
	@ResponseBody
	public Result list(@ApiParam(value = "哪一页") Integer page, @ApiParam(value = "每页大小") Integer size, 
			@ApiParam(value = "用户id") Long uid,@ApiParam(value = "用车申请id") Long apply,
			String start,String end) {
			
			if (uid == null||apply==null||page==null||size==null) {
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			
			try {
			List<Driver> list = new ArrayList<Driver>();
	
			User user = userService.findById(uid);
				if (user == null) {
					return Result.instance(ResponseCode.unknown_account.getCode(), ResponseCode.unknown_account.getMsg());
				}
				//long id=user.getOrg();
				//apply <0代表紧急调度  反之  根据start   和 end 筛选驾驶员
				if(apply > 0){
					Apply app = applyService.findById(apply);
					if (app != null) {
						String startDate = app.getPlan_time();
						String endDate = app.getPlan_return();
						if(StringUtils.isNotBlank(startDate)&&StringUtils.isNotBlank(endDate)){
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("driver_id", "1");
							map.put("start", startDate);
							map.put("end", endDate);
							List<String> ids = controlService.listByControl(map);
							map.clear();
							if (CollectionUtils.isNotEmpty(ids)) {
								map.put("lists", ids);
							}
							map.put("startIdx", page*size);
							map.put("limit", size);
							map.put("status", Constant.ADD_STATUS);
							
							queryUtils.queryByOrg(String.valueOf(uid), map);
							map.put("task_status", Constant.ADD_STATUS);
							list = driverService.pageDriver(map);
						}
					}
					
				}else{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("driver_id", "1");
					map.put("start", start);
					map.put("end", start);
					List<String> ids = controlService.listByControl(map);
					map.clear();
					if (CollectionUtils.isNotEmpty(ids)) {
						map.put("lists", ids);
					}
					map.put("startIdx", page*size);
					map.put("limit", size);
					map.put("status", Constant.ADD_STATUS);
					queryUtils.queryByOrg(String.valueOf(uid), map);
					map.put("task_status", Constant.ADD_STATUS);
					list = driverService.pageDriver(map);
					
				}
			
			return Result.success(list);
		} catch (Exception e) {
			LOGGER.error("app/driver/list",e);
			return Result.error();
		}
	}





	/**
	 * ============================web
	 */
	@RequiresPermissions(value = "driver/list")
	@RequestMapping("driver/list")
	public String list() {
		return "driver/list";
	}


	/**
	 *
	 * @author mars
	 * @date 2017年3月28日 上午10:51:28
	 * @Description: TODO(驾驶员信息分页显示)
	 * @param @param limit
	 * @param @param pageIndex
	 * @param @param request
	 * @param @return 设定文件
	 * @return Page 返回类型
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "驾驶员分页查询", httpMethod = "POST", response = Result.class, notes = "必须的参数：limit: 15,pageIndex: 0,driver_name:驾驶员姓名 driver_no:驾驶证号码 driver_type:“驾驶证类型Code")
	@RequestMapping(value="web/driver/page", method = RequestMethod.POST)
	@ResponseBody
	public Page driver(int limit, int pageIndex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("startIdx", pageIndex * limit);
		map.put("limit", limit);
		map.put("status", Constant.ADD_STATUS);
		String name = request.getParameter("driver_name");
		if (StringUtils.isNotBlank(name)) {
			map.put("driver_name", name);
		}
		String no = request.getParameter("driver_no");
		if (StringUtils.isNotBlank(no)) {
			map.put("driver_no", no);
		}
		String driver_type = request.getParameter("driver_type");
		if (StringUtils.isNotBlank(driver_type) && !"-1".equals(driver_type)) {
			map.put("driver_type", driver_type);
		}

		//User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);

		User u =new User();
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
		List<Driver> list = driverService.pageByWebService(map);
		int recodes = driverService.pageByWebCount(map);
		return new Page<Driver>(list, recodes, limit);
	}

	/**
	 *
	 * @author mars
	 * @date 2017年3月28日 下午2:13:54
	 * @Description: TODO()
	 * @param @param driver
	 * @param @param intimes
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@ApiOperation(value = "新增司机", httpMethod = "POST", response = Result.class, notes = "必须的参数：{driver:{驾驶员对象},birth_time:出生日期,arrive_time:上岗时间}")
	@TranslationControl(value = "driver/add")
	@RequestMapping(value="web/driver/add",method=RequestMethod.POST)
	@ResponseBody
	public Response add(Driver driver, HttpServletRequest request, String birth_time, String arrive_time) {
		Member me = new Member();
		if (driver == null || StringUtils.isBlank(birth_time) || StringUtils.isBlank(arrive_time)) {
			return new FailedResponse("参数错误！");
		}
		HashMap<String, String> maps1 = new HashMap<String, String>();
		maps1.put("driver_no", driver.getDriver_no());
		List<Driver> list1 = driverService.listBy(maps1);
		if (CollectionUtils.isNotEmpty(list1)) {
			return new FailedResponse("驾驶证号已经存在！");
		}

		HashMap<String, String> maps2 = new HashMap<String, String>();
		maps2.put("card", driver.getCard());
		List<Member> list2 = memberService.listBy(maps2);
		if (CollectionUtils.isNotEmpty(list2)) {
			return new FailedResponse("身份证号已经存在！");
		}

		try {
			// User user = (User) request.getSession().getAttribute(Constant.SESSION_KEY);
			User u =new User();
			String id = request.getParameter("keyid");
			u.setId(Long.parseLong(id));
			User user = userService.findBy(u);

			Date date = DateUtils.parseDate(birth_time);
			if (driver.getUid() != null) {
				me.setId(driver.getUid());
			} else {
				me.setStatus(Constant.ADD_STATUS);
				me.setOrg(user.getOrg());
			}
			me.setCreatedate(new Date());
			me.setCreateby(user.getId());
			me.setSex(driver.getSex());
			me.setDriver("1");
			me.setName(driver.getName());
			me.setCard(driver.getCard());
			me.setMobile(driver.getMobile());
			me.setBirth(date);
			driver.setArrive(arrive_time);
			driver.setCreatedate(DateUtils.getDateTime());
			driver.setCreatedby(user.getId());
			driver.setStatus(Constant.ADD_STATUS);
			driver.setD_status(Constant.DRIVER_STATE_1);
			driver.setP_image(FileUtils.checkUploadFile(driver.getP_image()));
			driver.setD_inmage_1(FileUtils.checkUploadFile(driver.getD_inmage_1()));
			driver.setD_inmage_2(FileUtils.checkUploadFile(driver.getD_inmage_2()));
			driverService.add(driver, me);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 *
	 * @author mars
	 * @date 2017年3月29日 下午5:04:36
	 * @Description: TODO(修改驾驶员信息)
	 * @param @param driver
	 * @param @param birth_time
	 * @param @param arrive_time
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@ApiOperation(value = "修改司机信息", httpMethod = "POST", response = Result.class, notes = "必须的参数：{driver:{驾驶员对象},birth_time:出生日期,arrive_time:上岗时间}")
	@TranslationControl(value = "driver/modify")
	@RequestMapping(value="web/driver/modify",method=RequestMethod.POST)
	@ResponseBody
	public Response modify(Driver driver, String birth_time, String arrive_time) {
		Member me = new Member();
		if (driver == null || StringUtils.isBlank(birth_time) || StringUtils.isBlank(arrive_time)) {
			return new FailedResponse("参数错误！");
		}
		try {
			Long id = driver.getId();
			if (id == null) {
				return new FailedResponse("参数错误！");
			}
			Driver dr = driverService.findById(id);
			if (dr == null) {
				return new FailedResponse("参数错误！");
			}
			Long uid = dr.getUid();
			if (uid == null) {
				return new FailedResponse("参数错误！");
			}

			me.setId(uid);
			Date date = DateUtils.parseDate(birth_time);
			me.setSex(driver.getSex());
			me.setName(driver.getName());
			me.setCard(driver.getCard());
			me.setMobile(driver.getMobile());
			me.setBirth(date);
			driver.setArrive(arrive_time);
			driver.setP_image(FileUtils.checkUploadFile(driver.getP_image()));
			driver.setD_inmage_1(FileUtils.checkUploadFile(driver.getD_inmage_1()));
			driver.setD_inmage_2(FileUtils.checkUploadFile(driver.getD_inmage_2()));
			driverService.modifyWebService(driver);
			memberService.modify(me);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 *
	 * @author mars
	 * @date 2017年3月30日 上午10:09:42
	 * @Description: TODO(更新司机的工作状态)
	 * @param @param id
	 * @param @param status
	 * @param @param texts
	 * @param @param start
	 * @param @param end
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */

	@ApiOperation(value = "驾驶员工作状态更新", httpMethod = "POST", response = Result.class, notes = "id:驾驶员Id,name:张三driver_no:1234org:优行科技d_status:1start:end:")
	@TranslationControl(value = "driver/work")
	@RequestMapping(value = "web/driver/work",method=RequestMethod.POST)
	@ResponseBody
	public Response updateStatus(String id, String d_status, String texts, String start, String end) {
		if (StringUtils.isBlank(id) || StringUtils.isBlank(d_status)) {
			return new FailedResponse("参数错误！");
		}
		try {
			Driver driver = driverService.findById(Long.parseLong(id));
			if (driver == null) {
				return new FailedResponse("查询不到这个司机信息！");
			}
			Driver dr = new Driver();
			dr.setId(driver.getId());
			dr.setTexts(texts);
			dr.setD_status(d_status);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(start != null && !"".equals(start)){
				Date start_date = sdf.parse(start);
				dr.setStart(sdf.format(start_date) + " 00:00:00");
			}
			if(end != null && !"".equals(end)){
				Date end_date = sdf.parse(end);
				dr.setEnd(sdf.format(end_date) + " 23:59:59");
			}
	     /* dr.setStart(start);
	      dr.setEnd(end);*/
			driverService.modifyWebService(dr);
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误！");
		}
	}

	/**
	 *
	 * @author: Administrator
	 * @time:2017年5月22日 上午8:58:32
	 * @title delete删除司机信息
	 * @Description: TODO(删除司机信息)   {ids:idsArray[驾驶员id数组]}
	 * @param ids
	 * @return
	 */

	@ApiOperation(value = "删除司机", httpMethod = "POST", response = Result.class, notes = "{ids:idsArray[驾驶员id数组]}")
	@RequiresPermissions(value = "web/driver/delete")
	@RequestMapping(value = "web/driver/delete", method = RequestMethod.POST)
	@ResponseBody
	public Response delete(@RequestParam(value = "ids[]") String[] ids) {
		if (ids.length == 0) {
			return new FailedResponse("参数错误");
		}
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("list", ids);
			List<Driver> list = driverService.listBy(map);
			List<Long> list1 = new ArrayList<Long>();
			for (Driver driver : list) {
				list1.add(driver.getUid());
			}
			map.clear();
			map.put("list", list1);
			List<String> list2 = controlService.listByDriver(map);
			if (!CollectionUtils.isEmpty(list2)) {
				return new FailedResponse("删除的驾驶员中有尚未完成任务的司机！");
			}
			for (String id : ids) {
				Driver mb = driverService.findById(Long.parseLong(id));
				if (mb != null) {
					mb.setStatus(Constant.REMOVE_STATUS);
					driverService.modify(mb);//逻辑删除
				}
			}
			return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}




}
