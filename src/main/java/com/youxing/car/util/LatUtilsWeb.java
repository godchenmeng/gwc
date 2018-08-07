package com.youxing.car.util;

import org.apache.commons.httpclient.methods.GetMethod;


/**
 * @author mars
 * @date 2017年5月5日 下午4:54:09
 */
public class LatUtilsWeb {
	private final static String URL = "http://api.map.baidu.com/geocoder/v2/?output=json&pois=1&ak=W1Mvs7vqxdRKgxQtVFxHoekrzrGFDH5T&location=";
	private final static String URL_GPS = "http://api.map.baidu.com/geocoder/v2/?coordtype=wgs84ll&output=json&pois=1&ak=qxxcZD4IFZ4tR4Mw8ae6fonGt3tCUxiy&location=";

	/**
	 * 
	 * @author mars
	 * @date 2017年5月5日 下午5:02:15
	 * @Description: TODO(百度地图逆地理编码)
	 * @param @param location
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getAddress(String location) {
		String []gps = location.split(",");
		StringBuffer line = new StringBuffer();
		String str = "";
		double[] list = GPSToBaiduGPS.wgs2bd(Double.parseDouble(gps[0]), Double.parseDouble(gps[1]));
		if (list == null) {
			return line.toString();
		}
		for (int i = 0; i < list.length -1; i++) {
			line.append(Double.parseDouble(gps[1]) + "," + Double.parseDouble(gps[0]));
			if (i != list.length - 1) {
				line.append("|");
			}
		}
//		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(URL + line.toString());
		try {
			HttpUtils.methodGet(URL, line.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return str;
	}

	public static String getGpsAddress(String location) {
		String str = "";
		if (!location.contains(",")) {
			return str;
		}
		//HttpClient client = new HttpClient();
		GetMethod get = null;
		try {
			HttpUtils.methodGet(URL_GPS, location);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
		return str;
	}

}
