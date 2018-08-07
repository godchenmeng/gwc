package com.youxing.car.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Advice;
import com.youxing.car.service.AdviceService;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;

/**   
 * @author mars   
 * @date 2017年5月18日 下午2:06:46 
 */
@Api(value = "advice", description = "意见反馈")
@Controller
public class AdviceController {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AdviceController.class);
	@Resource
	private AdviceService adviceService;
	
	@LogAnnotation(description="意见反馈" )
	@ApiOperation(value = "意见反馈", httpMethod = "POST", response = Result.class)
    @RequestMapping(value="app/advice/submit",method=RequestMethod.POST)
	@ResponseBody
	public Result submitAdvice(@ApiParam(required = true,value = "意见内容200字以内")String content,@ApiParam(required = true,value = "单选框的值")String radio,@ApiParam(required = true,value = "用户id")Long uid){
		try {
			if(StringUtils.isBlank(content)||StringUtils.isBlank(radio)||uid == null){
				return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
			}
			if(content.trim().length() > 50){
				return Result.instance(ResponseCode.param_format_error.getCode(), ResponseCode.param_format_error.getMsg());
			}
			Advice advice = new Advice();
			advice.setContent(content);
			advice.setCreatedby(uid);
			advice.setRadio(radio);
			adviceService.add(advice);
			return Result.success();
		} catch (Exception e) {
			LOGGER.error("app/advice/submi", e);
			
			return Result.error();
		}
	}
}


