package com.youxing.car.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component 
public class MyCorsFilter implements Filter { 
	@Override 
	public void init(FilterConfig filterConfig) throws ServletException { 
		
	} 
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse; 
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;  

		String origin = (String) servletRequest.getRemoteHost()+":"+servletRequest.getRemotePort(); 
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "origin,Content-type,x-requested-with,Authorization");
		response.setHeader("Access-Control-Allow-Credentials","true");
		/*if(!"OPTIONS".equals(httpServletRequest.getMethod())){//OPTIONS方法不要拦截，不然跨域设置不成功  
	          return;
	    }*/  

		filterChain.doFilter(servletRequest, servletResponse);
}
	
	@Override 
	public void destroy() {
		
	}
	
}

