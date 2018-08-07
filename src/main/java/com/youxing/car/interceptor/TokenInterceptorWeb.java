package com.youxing.car.interceptor;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.youxing.car.util.Constant;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;
import com.youxing.car.utils.redis.RedisUtils;

public class TokenInterceptorWeb implements HandlerInterceptor {
	private static final Logger logger = Logger.getLogger(TokenInterceptor.class);

	@Resource
	private RedisUtils redisUtils;

	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) {

	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) {

	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) {
		String token_key = request.getParameter("keyid");
		String token_value = request.getParameter("token");
		String ip = HttpUtils.getIpAddress(request);
		boolean result = true;
		PrintWriter writer = null;
		try {
			if (StringUtils.isNotBlank(token_key) && StringUtils.isNotBlank(token_value)) {
				Object obj = redisUtils.hget(Constant.REDIS_LOGIN_WEB_INFO,token_key);

				if(null == obj){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=UTF-8");
					response.setStatus(401);
					writer = response.getWriter();
					Result re = Result.instance(ResponseCode.token_error.getCode(), ResponseCode.token_error.getMsg());
					writer.write(JSON.toJSONString(re));
					result = false;
				}else if(!obj.toString().split("@")[0].equals(token_value)){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=UTF-8");
					response.setStatus(401);
					writer = response.getWriter();
					Result re = Result.instance(ResponseCode.login_error.getCode(), ResponseCode.login_error.getMsg());
					writer.write(JSON.toJSONString(re));
					result = false;
				}else if(!obj.toString().split("@")[2].equals(ip)){
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html;charset=UTF-8");
					response.setStatus(401);
					writer = response.getWriter();
					Result re = Result.instance(ResponseCode.login_error.getCode(), ResponseCode.login_error.getMsg());
					writer.write(JSON.toJSONString(re));
					result = false;
				}
				logger.info("验证结果--uid=" + token_key + "  token=" + token_value + "  缓存中的token=" + obj);
			}else{
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=UTF-8");
				writer = response.getWriter();
				Result re = Result.instance(ResponseCode.forbidden_ip.getCode(), ResponseCode.forbidden_ip.getMsg());
				writer.write(JSON.toJSONString(re));
				result = false;
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
			writer.write(e.getMessage());
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
		return result;
	}

}
