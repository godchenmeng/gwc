package com.youxing.car.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

/**
 * @author mars
 * @date 2017年5月5日 下午4:54:09
 */
public class LatUtils {
	private static final Logger logger = Logger.getLogger(LatUtils.class);
	private final static String URL = "http://api.map.baidu.com/geocoder/v2/?coordtype=wgs84ll&output=json&pois=1&ak=qxxcZD4IFZ4tR4Mw8ae6fonGt3tCUxiy&location=";
	private final static String URL_BATCH = "http://api.map.baidu.com/geocoder/v2/?coordtype=wgs84ll&batch=true&output=json&pois=1&ak=qxxcZD4IFZ4tR4Mw8ae6fonGt3tCUxiy&location=";
	// gps坐标转高德坐标
	private static final String GD_URL_GPS = "http://restapi.amap.com/v3/assistant/coordinate/convert?coordsys=gps&output=json&key=19a4d00d6069fba1ecb50d9703ab4728&locations=";
	// 高德坐标转地名
	private static final String GD_URL_ADDRESS = "http://restapi.amap.com/v3/geocode/regeo?output=json&batch=true&key=19a4d00d6069fba1ecb50d9703ab4728&radius=3000&location=";

	// 转百度坐标

	public static String getGDNames(String gps) {
		String values = getGDGPS(gps);
		if (StringUtils.isNotBlank(values)) {
			return getAddressNames(values.replace(";", "|"));
		}
		return null;

	}

	/**
	 * 
	 * @author mars
	 * @date 2017年6月13日 下午12:29:13
	 * @Description: 高德坐标转地名
	 * @param @param gps
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getAddressNames(String gps) {
	//	HttpClient client = new HttpClient();
		StringBuffer sb = new StringBuffer();
		GetMethod get = null;
		try {
			HttpUtils.methodGet(GD_URL_ADDRESS, gps);
			/*get = new GetMethod(GD_URL_ADDRESS + gps);
			client.getHostConfiguration().setProxy("10.0.165.62", 9997);
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			int statusCode = client.executeMethod(get);
			if (statusCode >= 200 && statusCode < 300) {
				String content = get.getResponseBodyAsString();
				if (StringUtils.isNotBlank(content)) {
					JSONObject jsonObject = JSONObject.fromObject(content);
					String status = jsonObject.getString("status");
					String info = jsonObject.getString("info");
					if (status.equals("1") && info.equals("OK")) {
						JSONArray regeocode = jsonObject.getJSONArray("regeocodes");
						for (int i = 0; i < regeocode.size(); i++) {
							JSONObject je = regeocode.getJSONObject(i);
							if (i < regeocode.size() - 1) {
								sb.append(je.getString("formatted_address") + ";");
							} else {
								sb.append(je.getString("formatted_address"));
							}
						}
						return sb.toString();
					}
				}
			}*/
		//	return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return null;
	}

	/**
	 * 获取高德坐标根据gps
	 * 
	 * @param gps
	 * @return
	 */
	public static String getGDGPS(String gps) {
		GetMethod get = null;
		try {
			String response ="";
			response = HttpUtils.methodGet(GD_URL_GPS, gps);
			if (StringUtils.isNotBlank(response)) {
				JSONObject jsonObject = JSONObject.fromObject(response);
				String status = jsonObject.getString("status");
				String info = jsonObject.getString("info");
				if (status.equals("1") && info.equals("ok")) {
					String loaction = jsonObject.getString("locations");
					return loaction;
				}
			}
			/*get = new GetMethod(GD_URL_GPS + gps);
			client.getHostConfiguration().setProxy("10.0.165.62", 9997);
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			int statusCode = client.executeMethod(get);
			if (statusCode >= 200 && statusCode < 300) {
				String content = get.getResponseBodyAsString();
				if (StringUtils.isNotBlank(content)) {
					JSONObject jsonObject = JSONObject.fromObject(content);
					String status = jsonObject.getString("status");
					String info = jsonObject.getString("info");
					if (status.equals("1") && info.equals("ok")) {
						String loaction = jsonObject.getString("locations");
						return loaction;
					}
				}
			}*/
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return null;

	}

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
		String response = "";
		String str = "";
		HttpClient client = new HttpClient();
		GetMethod get = null;
		try {
			 response = HttpUtils.methodGet(URL, location);
			 if (StringUtils.isNotBlank(response)) {
					JSONObject jsonObject = JSONObject.fromObject(response);
					String status = jsonObject.getString("status");
					if (status.equals("0")) {
						JSONObject address = jsonObject.getJSONObject("result");
						str = (address.getString("formatted_address"));
					}
					return str;
				}
			return str;
		} catch (Exception e) {
			logger.error("getAddress", e);
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
		return str;
	}

	public static String getAddressBatch(String location) {
		String str = "";
		String response = "";
		GetMethod get = null;
		try {
			response = HttpUtils.methodGet(URL_BATCH, location);
			
			if (StringUtils.isNotBlank(response)) {
				JSONObject jsonObject = JSONObject.fromObject(response);
				String status = jsonObject.getString("status");
				if (status.equals("0")) {
					JSONObject address = jsonObject.getJSONObject("result");
					str = (address.getString("formatted_address"));
				}
				return str;
			}
		} catch (Exception e) {
			logger.error("getAddress", e);
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
		return str;
	}



}
