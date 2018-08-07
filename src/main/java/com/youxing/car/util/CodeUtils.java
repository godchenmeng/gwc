package com.youxing.car.util;

import javax.servlet.http.HttpServletRequest;

import com.octo.captcha.service.CaptchaServiceException;
import com.youxing.car.jcaptcha.JCaptcha;

/**   
 * @author mars   
 * @date 2017年4月5日 下午7:06:00 
 */
public class CodeUtils {

	/**
	 * 
	* @author mars   
	* @date 2017年4月5日 下午7:06:11 
	* @Description: TODO(验证码校验) 
	* @param @param request
	* @param @param identifier
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	public static boolean identifierValid(HttpServletRequest request, String identifier) {
		String sessionId = request.getSession().getId();
		try {
			return JCaptcha.validateResponse(sessionId, identifier);
		} catch (CaptchaServiceException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}


