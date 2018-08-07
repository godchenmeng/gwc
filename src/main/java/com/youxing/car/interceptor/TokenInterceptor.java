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
import com.youxing.car.util.ResponseCode;
import com.youxing.car.util.Result;
import com.youxing.car.utils.redis.RedisUtils;

public class TokenInterceptor implements HandlerInterceptor {
	private static final Logger logger = Logger.getLogger(TokenInterceptor.class);
	
	@Resource
	private RedisUtils redisUtils;

	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) {

	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) {

	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) {
		String token_key = request.getHeader("keyid");
		String token_value = request.getHeader("token");
		String udid = request.getHeader("udid");
//		String ip = HttpUtils.getIpAddress(request);
		boolean result = true;
		PrintWriter writer = null;
		Object obj = null;
		try {
			if (StringUtils.isNotBlank(token_key) && StringUtils.isNotBlank(token_value)) {

				obj = redisUtils.hget(Constant.REDIS_LOGIN_APP_INFO,token_key + "@" + udid);

				if(null == obj || !obj.toString().split("@")[0].equals(token_value)){
					result = false;
				}
			} else {
				result = false;
			}
			System.err.println("验证结果--udid=" + udid + " uid=" + token_key + "  token=" + token_value + "  缓存中的token=" + obj + " 结果：" + result);
			if (!result) {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/html;charset=UTF-8");
				response.setStatus(401);
				writer = response.getWriter();
				Result re = Result.instance(ResponseCode.login_error.getCode(), ResponseCode.login_error.getMsg());
				writer.write(JSON.toJSONString(re));
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
