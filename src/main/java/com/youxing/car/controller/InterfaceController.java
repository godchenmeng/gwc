package com.youxing.car.controller;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.entity.CarMessage;
import com.youxing.car.entity.Organization;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.ListResponse;
import com.youxing.car.response.Response;
import com.youxing.car.service.CarService;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.OrganizationService;

@Api(value = "interface", description = "interface")
@Controller
public class InterfaceController {
	@Resource
	private CarService carService;
	@Resource
	private DeviceService deviceService;
	@Resource
	private GpsService gpsService;
	@Resource
	private OrganizationService organizationService;

	/**
	 * 
	 * @author mars
	 * @date 2017年7月1日 上午9:57:57
	 * @Description: 根据车牌号码查询该车的最新位置信息(支持多个查询分割符为|)
	 * @param @param car
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@RequestMapping(value = "web/get/gps/by/carNo", method = RequestMethod.GET)
	@ResponseBody
	public Response getCarGpsCurrent(String car) {
		try {
			List<CarMessage> result = new ArrayList<CarMessage>();
			if (StringUtils.isBlank(car)) {
				return new FailedResponse("车牌号为空！");
			}
			Map<String, Object> map = new HashMap<String, Object>();

			String[] car_no = car.split(",");
			map.put("list", car_no);
			List<String> device = deviceService.listDeviceByCar(map);
			if (CollectionUtils.isNotEmpty(device)) {
				map.put("list", device);
				result = gpsService.findGpsCurrentByDevice(map);
			}
			return new ListResponse<CarMessage>(result);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器异常！");
		}
	}

	/**
	 * 
	 * @author mars
	 * @date 2017年7月1日 上午9:55:37
	 * @Description: 根据机构查找所有的消防车 type =1 代表查询所有的消防车 2-查询某个机构下的消防车
	 *               区分消防车的车牌号以WJ开头以X结尾
	 * @param @param org
	 * @param @return 设定文件
	 * @return Response 返回类型
	 * @throws
	 */
	@RequestMapping(value = "web/get/gps/all/car", method = RequestMethod.GET)
	@ResponseBody
	public Response getAllCarGpsCurrent(String org, String type) {
		try {
			List<CarMessage> result = new ArrayList<CarMessage>();
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> device = new ArrayList<String>();
			if ("1".equals(type)) {
				map.put("yc", "\"WJ%X\"");
				device = deviceService.listOrgDevice(map);
			} else if ("2".equals(type)) {
				map.put("name", org);
				List<Organization> list = organizationService.listBy(map);
				if (list.size() == 1) {
					Long oid = list.get(0).getId();
					List<Long> org_list = organizationService.getOrganizationUser(list.get(0), oid);
					org_list.add(oid);
					map.put("list", org_list);
					map.put("yc", "\"WJ%X\"");
					device = deviceService.listOrgDevice(map);
				}
			}
			if (CollectionUtils.isNotEmpty(device)) {
				map.clear();
				map.put("list", device);
				result = gpsService.findGpsCurrentByDevice(map);
			}
			return new ListResponse<CarMessage>(result);
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器异常！");
		}
	}
}
