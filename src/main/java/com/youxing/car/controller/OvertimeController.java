package com.youxing.car.controller;

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.entity.OverTime;
import com.youxing.car.response.FailedResponse;
import com.youxing.car.response.Response;
import com.youxing.car.response.SuccessResponse;
import com.youxing.car.service.OvertimeService;
import com.youxing.car.util.ConfigUtils;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.util.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(value = "overtime", description = "请求超时管理")
@Controller
public class OvertimeController {
	
	@Resource
	private OvertimeService otService;
	
	/**
	 * 
	 * @param ot
	 * @param request
	 * @return
	 */
 	@ApiOperation(value = "请求超时记录新增", httpMethod = "POST", response = Result.class)
 	@RequestMapping(value = "web/overtime/add", method = RequestMethod.POST)
	@ResponseBody
	public Response add(OverTime ot,HttpServletRequest request) {
		try {
	        String type = request.getHeader("user-agent");
	        Properties p = ConfigUtils.getConfig();
			String serverType = "";
	        if(StringUtils.isNotBlank(type) && type != null){
		        if(type.contains("Android")) {
		        	serverType = p.getProperty("server.type.android");
		        } else if(type.contains("iOS")) {
		        	serverType = p.getProperty("server.type.ios");
		        }else {
		        	serverType = p.getProperty("server.type.web");
		        }
	        }
			 String uid  = request.getParameter("keyid");
			 String ip =  HttpUtils.getIpAddress(request);
			 
			 if(StringUtils.isBlank(uid)){
				 uid = "0";
			 }
			 
			 if(StringUtils.isBlank(ip) || ot.getDuration()  < 0 ||
				StringUtils.isBlank(ot.getUrl())){
				 return new  FailedResponse("参数不也能为空!");
			 }else{
				 ot.setCreatetime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
				 ot.setIp(ip);
				 ot.setUid(uid);
				 ot.setExchange(serverType);
			 }
			 otService.add(ot);
			 return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}
	
	
	/**
	 * 
	 * @param ot
	 * @param request
	 * @return
	 */
 	@ApiOperation(value = "请求超时记录新增", httpMethod = "POST", response = Result.class)
 	@RequestMapping(value = "app/overtime/add", method = RequestMethod.POST)
	@ResponseBody
	public Response addAPP(OverTime ot,HttpServletRequest request) {
		try {
	        String type = request.getHeader("user-agent");
	        Properties p = ConfigUtils.getConfig();
			String serverType = "";
	        if(StringUtils.isNotBlank(type) && type != null){
		        if(type.contains("Android")) {
		        	serverType = p.getProperty("server.type.android");
		        } else if(type.contains("iOS")) {
		        	serverType = p.getProperty("server.type.ios");
		        }else {
		        	serverType = p.getProperty("server.type.web");
		        }
	        }
			 String uid  = request.getParameter("keyid");
			 String ip =  HttpUtils.getIpAddress(request);
			 
			 if(StringUtils.isBlank(uid)){
				 uid = "0";
			 }
			 
			 if(StringUtils.isBlank(ip) || ot.getDuration()  < 0 ||
				StringUtils.isBlank(ot.getUrl())){
				 return new  FailedResponse("参数不也能为空!");
			 }else{
				 ot.setCreatetime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
				 ot.setIp(ip);
				 ot.setUid(uid);
				 ot.setExchange(serverType);
			 }
			 otService.add(ot);
			 return new SuccessResponse();
		} catch (Exception e) {
			e.printStackTrace();
			return new FailedResponse("服务器错误");
		}
	}
}
