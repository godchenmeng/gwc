package com.youxing.car.jcaptcha;

import javax.servlet.http.HttpServletRequest;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;

/**
 * 提供相应的API来验证当前请求输入的验证码是否正确
 * 
 * @author Administrator
 *
 */
public class JCaptcha {
	public static final MyManageableImageCaptchaService captchaService = new MyManageableImageCaptchaService(new FastHashMapCaptchaStore(), new GMailEngine(), 180, 100000, 75000);

	public static boolean validateResponse(String id, String userCaptchaResponse) {
		boolean validated = false;
		try {
			validated = captchaService.validateResponseForID(id, userCaptchaResponse).booleanValue();
		} catch (CaptchaServiceException e) {
			e.printStackTrace();
		}
		return validated;
	}

	public static boolean hasCaptcha(HttpServletRequest request, String userCaptchaResponse) {
		if (request.getSession(false) == null)
			return false;
		boolean validated = false;
		try {
			String id = request.getSession().getId();
			validated = captchaService.hasCapcha(id, userCaptchaResponse);
		} catch (CaptchaServiceException e) {
			e.printStackTrace();
		}
		return validated;
	}
}
