/**
 * Copyright (c) 2014, kentch@126.com All Rights Reserved. Date:2014年6月19日上午11:35:13
 * 
 */
package com.youxing.car.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.youxing.car.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.youxing.car.entity.User;


public class LoginInterceptor implements HandlerInterceptor {

    private static final String X_REQUEST_WITH = "X-Requested-With";
    private static final String AJAX_REQUEST = "XMLHttpRequest";

    /*
     * TODO
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest ,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute(Constant.SESSION_KEY);
        boolean result = true;
        if (null == sessionUser) {
            String redirectUrl = request.getContextPath() + "/login";
            if (isAjaxRequest(request)) {
                redirectUrl += "?ajax=true";
            }
            response.sendRedirect(redirectUrl);
            result = false;
        }
        return result;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestType = request.getHeader(X_REQUEST_WITH);
        if (StringUtils.isNotBlank(requestType) && requestType.equals(AJAX_REQUEST)) {
            return true;
        }
        return false;
    }

    /*
     * TODO
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    /*
     * TODO
     * 
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}
