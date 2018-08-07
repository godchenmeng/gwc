package com.youxing.car.aop.annotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.youxing.car.entity.Permission;
import com.youxing.car.entity.User;
import com.youxing.car.service.PermissionService;
import com.youxing.car.service.UserService;



/**
 * 权限检查切面
 * 根据用户原有的权限，与目标方法的权限配置进行匹配，
 * 如果目标方法需要的权限在用户原有的权限以内，则调用目标方法
 * 如果不匹配，则不调用目标方法
 *
 */
@Aspect  
@Component  
public class TranslationControlAspect {
	
	@Resource
	private PermissionService permissionService;
	@Resource
	private UserService userService;
	
	 /**
     * 用户本身的权限
     */
    private List<Permission> translations;

    public List<Permission> getTranslation() {
		return translations;
	}

	public void setTranslation(List<Permission> translations) {
		this.translations = translations;
	}
	
	@Pointcut(value="execution(* com.youxing.car.controller.*.*(..))")
	public void testController() {
	}
	
	
	/**
	 * 获取登录用户的权限列表translations
	 */
	public List<Permission> getTranslations(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();    
        String uid = request.getParameter("keyid");
        if(StringUtils.isNotBlank(uid)){
        	 User u =new User();
     		u.setId(Long.parseLong(uid));
     		User user = userService.findBy(u);
     		if(user == null){
     			return translations;
     		}
     		long rid = user.getRole();
     		HashMap<String, Object> map = new HashMap<String, Object>();
     		map.put("rid", rid);
     		translations = permissionService.getTranslations(map);
        }
		return translations;
	}



	/**
     * aop中的环绕通知
     * 在这个方法中检查用户的权限和目标方法的需要的权限是否匹配
     * 如果匹配则调用目标方法，不匹配则不调用
     * @param joinPoint　连接点
     * @throws Throwable
     */
	
	
	@Around("testController()")
    public Object isAccessMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    	
    	translations = getTranslations();
        /**
         * 1.获取访问目标方法应该具备的权限
         *  为解析目标方法的TranslationControl注解，根据我们定义的解析器，需要得到：目标类的class形式　方法的名称
         */
        //得到该方法的访问权限
        String methodAccess = getControllerMethodDescription(joinPoint);
        
        /*
         * 2.遍历用户的权限，看是否拥有目标方法对应的权限
         */
        boolean isAccessed = false;
        
        if(translations != null){
        	for (Permission tran : translations) {
                /*
                 * 如果目标方法没有使用TranslationControl注解，则解析出来的权限字符串就为空字符串
                 * 则默认用户拥有这个权限
                 */
                if ("".equals(methodAccess)) {
                    isAccessed = true;
                    break;
                }
                /*
                 * 用户原有权限列表中有的权限与目标方法上TranslationControl注解配置的权限进行匹配
                 */
                if (tran.getUrl() != null && 
                		tran.getUrl().equalsIgnoreCase(methodAccess)) {
                    isAccessed = true;
                    break;
                }
            }
        }
        
        
        if("codeOk".equals(methodAccess)){
        	 isAccessed = true;
        }
        
        /*
         * 3.如果用户拥有权限，则调用目标方法　，如果没有，则不调用目标方法，只给出提示
         */
        if (isAccessed) {
            Object result = joinPoint.proceed();//调用目标方法
            return result;
        } else {
        }
		return isAccessed;
    
    }
	
	
	
public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {  
	
    String targetName = joinPoint.getTarget().getClass().getName();  
    String methodName = joinPoint.getSignature().getName();  
    Object[] arguments = joinPoint.getArgs();  
    Class targetClass = Class.forName(targetName);  
    Method[] methods = targetClass.getMethods();  
    String description = "";  
    for (Method method : methods) {  
    	//首先判断访问类是否有该访问方法
    	if(method.getName().equals(methodName)){
    		TranslationControl control = method.getAnnotation(TranslationControl.class);
    		//判断方法是否有权限注解
    		if(control != null){
    			Class[] clazzs = method.getParameterTypes();  
    			//判断参数长度是否一致
    			if(clazzs.length == arguments.length){
    				description = method.getAnnotation(TranslationControl.class).value();
    				break;
    			}
    		}else{
    			return "codeOk";
    		}
    	}
    	}
    return description;  
} 

}
