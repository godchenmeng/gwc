package com.youxing.car.aop;

 
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.youxing.car.entity.SysLog;
import com.youxing.car.entity.User;
import com.youxing.car.service.LogService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.HttpUtils;  
  
/** 
 * 切点类 
 * @author tiangai 
 * @since 2014-08-05 Pm 20:35 
 * @version 1.0 
 */  
@Aspect  
@Component  
public class SystemLogAspect {  
    //注入Service用于把日志保存数据库  
    @Resource  
    private LogService logService; 
    @Resource  
    private UserService userService;
    //本地异常日志记录对象  
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);   
  
    //Controller层切点  
    @Pointcut("@annotation(com.youxing.car.aop.LogAnnotation)")  
    public void controllerAspect() {  
    }  
  
    /** 
     * 前置通知 用于拦截Controller层记录用户的操作 
     * 
     * @param joinPoint 切点 
     */  
	@Before("controllerAspect()")  
    public void doBefore(JoinPoint joinPoint) {  
  
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();    

        //请求的IP  
        SysLog log = new SysLog(); 
        String ip =  HttpUtils.getIpAddress(request);
        
    	String id = request.getParameter("keyid");
        if(StringUtils.isBlank(id)){
        	 String s1 = request.getHeader("user-agent");
             if(s1.contains("Android")) {
             s1="Android客户端";
             } else if(s1.contains("IOS")) {
             	s1="ios客户端";
             }else {
             	s1="web客户端";
             } 
        	  //方法参数
            String strMethodName = joinPoint.getSignature().getName();
    		String strClassName = joinPoint.getTarget().getClass().getName();
    		Object[] params = joinPoint.getArgs();
    		
    		StringBuffer bfParams = new StringBuffer();
    		Enumeration<String> paraNames = null;
    		HttpServletRequest request1 = null;
    		if (params != null && params.length > 0) {
    			request1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    			paraNames = request1.getParameterNames();
    			String key;
    			String value;
    			while (paraNames.hasMoreElements()) {
    				key = paraNames.nextElement();
    				value = request1.getParameter(key);
    				if(!key.contains("password")){					
    					bfParams.append(key).append("=").append(value).append("&");
    				}
    			
    			}
    			if (StringUtils.isBlank(bfParams)) {
    				bfParams.append(request.getQueryString());
    			}
    		}

    		String strMessage = String.format("[类名]:%s,[方法]:%s,[参数]:%s", strClassName, strMethodName, bfParams.toString());
    		   try {  
    	            //*========数据库日志=========*//  
    	            log.setDescription(getControllerMethodDescription(joinPoint));//方法描述  
    	            log.setClientIp(ip); //客户端ip
    	            log.setClientType(s1);//客户端类型
    	            log.setCreatedate(new Date());//调用时间
    	            log.setOptContent(strMessage);//方法参数
    	            
    	            //保存数据库  
    	            logService.add(log);  
    	        } catch (Exception e) {
    	            //记录本地异常日志  
    	            logger.error("==前置通知异常==");  
    	            logger.error("异常信息:{}", e.getMessage());  
    	        } 
        	
        }	
     // Long uid = Long.parseLong(request.getParameter("keyid"));
     // String loginName =request.getHeader("token");           
    //   Enumeration   typestr = request.getHeaderNames(); 
        
        if(!StringUtils.isBlank(id)){
        Long uid = Long.parseLong(request.getParameter("keyid"));
    	User user= userService.findById(uid);
    	String loginName = "";
    	if(user == null){
    		loginName = "用户不存在!";
    	}else{
    		loginName= user.getName();
    	}
    	
        String s1 = request.getHeader("user-agent");
        if(s1.contains("Android")) {
        s1="Android";
        } else if(s1.contains("IOS")) {
        	s1="ios";
        }else {
        	s1="web客户端";
        }
        //方法参数
        String strMethodName = joinPoint.getSignature().getName();
		String strClassName = joinPoint.getTarget().getClass().getName();
		Object[] params = joinPoint.getArgs();
		
		StringBuffer bfParams = new StringBuffer();
		Enumeration<String> paraNames = null;
		HttpServletRequest request1 = null;
		if (params != null && params.length > 0) {
			request1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			paraNames = request1.getParameterNames();
			String key;
			String value;
			while (paraNames.hasMoreElements()) {
				key = paraNames.nextElement();
				value = request1.getParameter(key);
				if(!key.contains("password")){					
					bfParams.append(key).append("=").append(value).append("&");
				}
			
			}
			if (StringUtils.isBlank(bfParams)) {
				bfParams.append(request.getQueryString());
			}
		}

		String strMessage = String.format("[类名]:%s,[方法]:%s,[参数]:%s", strClassName, strMethodName, bfParams.toString());
        
        try {  
            //*========数据库日志=========*//  
		    
        	log.setLoginName(loginName);
	        log.setUid(uid);
            log.setDescription(getControllerMethodDescription(joinPoint));//方法描述  
            log.setClientIp(ip); //客户端ip
            log.setClientType(s1);//客户端类型
            log.setCreatedate(new Date());//调用时间
            log.setOptContent(strMessage);//方法参数
            
            //保存数据库  
            logService.add(log);  
        } catch (Exception e) {
            //记录本地异常日志  
            logger.error("==前置通知异常==");  
            logger.error("异常信息:{}", e.getMessage());  
        } 
      }
        
   }  
  

	/** 
     * 获取注解中对方法的描述信息 用于Controller层注解 
     * 
     * @param joinPoint 切点 
     * @return 方法描述 
     * @throws Exception 
     */  
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {  
        String targetName = joinPoint.getTarget().getClass().getName();  
        String methodName = joinPoint.getSignature().getName();  
        Object[] arguments = joinPoint.getArgs();  
        Class targetClass = Class.forName(targetName);  
        Method[] methods = targetClass.getMethods();  
        String description = "";  
        for (Method method : methods) {  
            if (method.getName().equals(methodName)) {  
                Class[] clazzs = method.getParameterTypes();  
                if (clazzs.length == arguments.length) {  
                    description = method.getAnnotation(LogAnnotation.class).description();  
                    break;  
                }  
            }  
        }  
        return description;  
    } 
}    