package com.youxing.car.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxing.car.generators.StringUtil;

import net.sf.jsqlparser.schema.Server;


/**
 * @author CM
 * @date 2017年5月26日
 *
 */
public class HttpUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);
    /**
     * @author CM
     * @date 2017年6月1日
     * @Description 向指定URL发送POST方法的请求
     * @param url 发送请求的URL
     * @param data 请求参数
     * @return URL 所代表远程资源的响应结果
     * @throws 
     */
    public static String methodPost(String url,NameValuePair[] data){
        Properties p=new Properties();
        try{
            InputStream config = Server.class.getResourceAsStream("/config.properties");
            p.load(config);
            config.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        String proxy = p.getProperty("baidu.proxy");
        String port = p.getProperty("baidu.proxy.port");
        String response= "";//要返回的response信息
        HttpClient httpClient = new HttpClient();
        if(!StringUtil.isEmpty(proxy) && !StringUtil.isEmpty(port)){
            httpClient.getHostConfiguration().setProxy(proxy, Integer.parseInt(port));
        }
        PostMethod postMethod = new PostMethod(url);   
        // 将表单的值放入postMethod中   
        postMethod.setRequestBody(data);   
        // 执行postMethod   
        int statusCode = 0;   
        try {   
            statusCode = httpClient.executeMethod(postMethod);   
        } catch (HttpException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发   
        // 301或者302   
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY   
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {   
            // 从头中取出转向的地址   
            Header locationHeader = postMethod.getResponseHeader("location");   
            String location = null;   
            if (locationHeader != null) {   
                location = locationHeader.getValue();   
                response= methodPost(location,data);//用跳转后的页面重新请求。
            } else {   
                System.err.println("Location field value is null.");   
            }   
        } else {   

            try {   
                response= postMethod.getResponseBodyAsString();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
            postMethod.releaseConnection();   
        }   
        return response;   
    }
    
    /**
     * @author CM
     * @date 2017年6月1日
     * @Description 向指定URL发送POST方法的请求
     * @param url 发送请求的URL
     * @param param 请求参数
     * @return URL 所代表远程资源的响应结果
     * @throws 
     */
    public static String methodGet(String url,String param){
        Properties p=new Properties();
        try{
            InputStream config = Server.class.getResourceAsStream("/config.properties");
            p.load(config);
            config.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        String proxy = p.getProperty("baidu.proxy");
        String port = p.getProperty("baidu.proxy.port");
        String response= "";//要返回的response信息   
        HttpClient httpClient = new HttpClient();  
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
        if(!StringUtil.isEmpty(proxy) && !StringUtil.isEmpty(port)){
            httpClient.getHostConfiguration().setProxy(proxy, Integer.parseInt(port));
        }
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);
        GetMethod getMethod = new GetMethod(url+param); 
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 执行getMethod   
        int statusCode = 0;   
        try {   
            statusCode = httpClient.executeMethod(getMethod);   
        } catch (HttpException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发   
        // 301或者302   
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY   
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {   
            // 从头中取出转向的地址   
            Header locationHeader = getMethod.getResponseHeader("location");   
            String location = null;   
            if (locationHeader != null) {   
                location = locationHeader.getValue();   
                response= methodGet(location,param);//用跳转后的页面重新请求。
            } else {   
                System.err.println("Location field value is null.");   
            }   
        } else {

            try {   
            	response= getMethod.getResponseBodyAsString();               } catch (IOException e) {
                e.printStackTrace();   
            }   
            getMethod.releaseConnection();   
        }   
        return response;   
    }
    
    /**
     * @author CM
     * @date 2017年5月27日
     * @Description 封装COOKIE
     * @param request 
     * @return Map<String,Cookie>
     * @throws 
     */
    public static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
    
    /** 
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址; 
     *  
     * @param request 
     * @return 
     * @throws IOException 
     */  
    public static String getIpAddress(HttpServletRequest request)  {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址 
    	String ip = request.getHeader("X-Forwarded-For");  
    	try{

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("Proxy-Client-IP");  
	            }
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("WL-Proxy-Client-IP");  
	            }
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("HTTP_CLIENT_IP");  
	            }
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
	            }
	            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	                ip = request.getRemoteAddr();  
	            }
	        } else if (ip.length() > 15) {  
	            String[] ips = ip.split(",");  
	            for (int index = 0; index < ips.length; index++) {  
	                String strIp = (String) ips[index];  
	                if (!("unknown".equalsIgnoreCase(strIp))) {  
	                    ip = strIp;  
	                    break;  
	                }  
	            }  
	        }  

	  	      File file =new File("javaio-appendfile.txt");
	
	  	      //if file doesnt exists, then create it
	  	      if(!file.exists()){
	  	       file.createNewFile();
	  	      }
	
	  	      //true = append file
	  	      FileWriter fileWritter = new FileWriter(file.getName(),true);
	  	             BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	  	             SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  	             bufferWritter.write("[" + df.format(new Date()) + "]errIP:" + ip);
	  	             bufferWritter.newLine();
	  	             bufferWritter.close();
    	}catch(Exception ex){
    		return "";
    	}
        String[] ipPort = ip.split(":");
    	if(ipPort.length > 1) return  ipPort[0];
        return ip;
    }  

}
