package com.youxing.car.util;

import javax.servlet.http.HttpServletResponse;

public class CorsUtil {
	
	 public static HttpServletResponse SetHttpServletResponse(
		     HttpServletResponse response) {
		   response.setHeader("Access-Control-Allow-Origin", "*");
		   response.setHeader("Access-Control-Allow-Methods",
		       "POST, GET, OPTIONS, DELETE");
		   response.setHeader("Access-Control-Max-Age", "3600");
		   response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		   return response;
		 }

}
