package com.youxing.car.util;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author mars
 * @date 2017年5月2日 下午2:48:32
 */
public class IdUtil {
	/**
	 * 
	* @author mars   
	* @date 2017年5月2日 下午4:52:58 
	* @Description: TODO(生成验证码的key) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String generateID() {
		// 创建GUID对象
		UUID uuid = UUID.randomUUID();
		// 得到对象产生的ID
		String strUUID = uuid.toString();
		return strUUID.toUpperCase().replaceAll("-", "");
	}
	/**
	 * 
	* @author mars   
	* @date 2017年5月2日 下午4:53:11 
	* @Description: TODO(生成app接口访问的token) 
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getAppToken() {
		String id = generateID() + System.currentTimeMillis() + "yxcladmin";
		return DigestUtils.md5Hex(id);
	}
}
