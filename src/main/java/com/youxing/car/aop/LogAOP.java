package com.youxing.car.aop;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Aspect
@Component
public class LogAOP {

	private static Logger LOGGER = LoggerFactory.getLogger(LogAOP.class);

	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void cutController() {
	}

	@Around("cutController()")
	public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {

		String strMethodName = point.getSignature().getName();
		String strClassName = point.getTarget().getClass().getName();
		Object[] params = point.getArgs();
		StringBuffer bfParams = new StringBuffer();
		Enumeration<String> paraNames = null;
		HttpServletRequest request = null;
		if (params != null && params.length > 0) {
			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			paraNames = request.getParameterNames();
			LOGGER.info(request.getHeader("keyid")+"-----------------"+request.getHeader("token"));
			String key;
			String value;
			while (paraNames.hasMoreElements()) {
				key = paraNames.nextElement();
				value = request.getParameter(key);
				if(!key.contains("password")){					
					bfParams.append(key).append("=").append(value).append("&");
				}
			}
			if (StringUtils.isBlank(bfParams)) {
				bfParams.append(request.getQueryString());
			}
		}

		String strMessage = String.format("[类名]:%s,[方法]:%s,[参数]:%s", strClassName, strMethodName, bfParams.toString());
		LOGGER.info(strMessage);
		if (isWriteLog(strMethodName)) {
			try {
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return point.proceed();
	}

	private boolean isWriteLog(String method) {
	//	String[] pattern = { "modifyPwd","add", "updateStatus", "delete", "addMemeber", "update", "modify", "check", "returnApply", "controlApply", "deleteDevice" };
		String[] pattern = { "userLogin"};
		for (String s : pattern) {
			if (method.indexOf(s) > -1) {
				return true;
			}
		}
		return false;
	}
}
