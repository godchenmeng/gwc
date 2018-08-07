package com.youxing.car.utils.push;

import com.youxing.car.entity.SysLog;
import com.youxing.car.entity.User;
import com.youxing.car.service.LogService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Constant;
import com.youxing.car.util.HttpUtils;
import com.youxing.car.utils.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    //本地异常日志记录对象
    private static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    @Resource
    private LogService logService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) {
        boolean isLogin = false;
        if(request instanceof ServletServerHttpRequest) {
            isLogin = checkUser(((ServletServerHttpRequest) request).getServletRequest(), map);
            saveLog(((ServletServerHttpRequest) request).getServletRequest());
        }
        if(!isLogin){
            map.put("err","login failed");
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }

    public boolean checkUser(HttpServletRequest request,Map<String, Object> map){
        String token_key = request.getParameter("keyid");
        String token_value = request.getParameter("token");
        String ip = HttpUtils.getIpAddress(request);
        boolean result = false;
        try {
            if (StringUtils.isNotBlank(token_key) && StringUtils.isNotBlank(token_value)) {

                Object obj = redisUtils.hget(Constant.REDIS_LOGIN_WEB_INFO,token_key);
                if (obj != null && obj.toString().split("@")[0].equals(token_value)) {
                    map.put("keyid",token_key);
                    map.put("token",token_value);
                    map.put("uorg",obj.toString().split("@")[1]);
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveLog(HttpServletRequest request){
        //请求的IP
        SysLog log = new SysLog();
        String ip =  HttpUtils.getIpAddress(request);

        String id = request.getParameter("keyid");

        String s1 = "WebSocket";
        //方法参数
        String strMethodName = request.getRequestURI();
        String strClassName = "WebSocket";

        StringBuffer bfParams = new StringBuffer();
        Enumeration<String> paraNames = request.getParameterNames();
        if (paraNames != null) {
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

        String strMessage = String.format("%s:%s,[参数]:%s", strClassName, strMethodName, bfParams.toString());
        try {
            //*========数据库日志=========*//
            User user= userService.findById(Long.parseLong(id));
            if(user != null) {
                log.setLoginName(user.getName());
                log.setUid(user.getId());
            }
            log.setDescription("websocket请求");//方法描述
            log.setClientIp(ip); //客户端ip
            log.setClientType(s1);//客户端类型
            log.setCreatedate(new Date());//调用时间
            log.setOptContent(strMessage);//方法参数

            //保存数据库
            logService.add(log);
        } catch (Exception e) {
            //记录本地异常日志
            logger.error("websocket异常信息:{}", e.getMessage());
        }
    }
}