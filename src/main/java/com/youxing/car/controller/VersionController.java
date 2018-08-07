package com.youxing.car.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Version;
import com.youxing.car.service.VersionService;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;

/**   
 * @author mars   
 * @date 2017年5月18日 下午1:20:41 
 */
@Api(value = "version", description = "版本检测管理")
@Controller
public class VersionController {

	@Resource
	private VersionService versionService;
	private static Logger LOGGER = LoggerFactory.getLogger(VersionController.class);
		/**
		 * 
		* @author mars   
		* @date 2017年5月18日 下午1:27:33 
		* @Description: TODO(检测更新接口) 
		* @param @param type
		* @param @return    设定文件 
		* @return Result    返回类型 
		* @throws
		 */
	    @LogAnnotation(description="版本检测" )
		@ApiOperation(value = "版本检测", httpMethod = "GET", response = Result.class)
	    @RequestMapping(value="app/version/check",method=RequestMethod.GET)
	    @ApiResponses(value = {@ApiResponse(code = 10000, message = "操作成功", response = Version.class) })
	    @ResponseBody
		public Result checkVersion(@ApiParam(required = true,value = "app类型1-安卓 2-ios")String type){
			try {
				if(StringUtils.isBlank(type)){
					return Result.instance(ResponseCode.missing_parameter.getCode(), ResponseCode.missing_parameter.getMsg());
				}
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("type", type);
				map.put("startIdx", 0);
				map.put("limit", 1);
				List<Version> list = versionService.pageBy(map);
				if(CollectionUtils.isNotEmpty(list)){
					return Result.success(list.get(0));
				}
				return Result.success();
			} catch (Exception e) {
				LOGGER.error("app/version/check", e);
				return Result.error();
			}	
		}
}


