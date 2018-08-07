package com.youxing.car.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

public class PasswordUtil {

	private final static String PUBLICSALT="youxingcarinfo";
	private final static String ALGORITHMNAME="MD5";
	/**
	 * 密码加密
	 * @param password
	 * @return
	 */
	public static Map<String, String> getScurityPassword(String password){
		HashMap<String, String> map = new HashMap<String, String>();
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
		DefaultHashService hashService = new DefaultHashService(); //默认算法SHA-512  
		hashService.setHashAlgorithmName("SHA-512");  
		hashService.setPrivateSalt(new SimpleByteSource(PUBLICSALT)); //私盐，默认无  
		hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false  
		hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就这个  
		hashService.setHashIterations(4); //生成Hash值的迭代次数  	  
		HashRequest request = new HashRequest.Builder()  
		            .setAlgorithmName(ALGORITHMNAME).setSource(ByteSource.Util.bytes(password))  
		            .setSalt(ByteSource.Util.bytes(salt)).setIterations(4).build();  
		String hex = hashService.computeHash(request).toString();
		map.put("salt", salt);
		map.put("password", hex);
		return map;
	}
	
	/**
	 * md5(md5(密码+username+salt4))）。
     * 使用随机盐加密密码(MD5且迭代4次)
     * @param password
     * @return Map{password:原密码, encodePassword:加密后的密码, salt:盐}
     */
    public static Map<String, String> encodePasswordWithRandomSalt(String password) {
        Map<String, String> hm = new HashMap<String, String>();
        String algorithmName = "md5";
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 4;
        SimpleHash hash = new SimpleHash(algorithmName, password, salt, hashIterations);
        hm.put("password", password);
        hm.put("encodePassword", hash.toHex());
        hm.put("salt", salt);
        return hm;
    }

    /**
     * 使用自定义盐加密密码（MD5且迭代4次）
     * @param password
     * @param salt
     * @return Map{password:原密码, encodePassword:加密后的密码, salt:盐}
     */
    public static Map<String, String> encodePasswordWithConstomSalt(String password, String salt) {
        Map<String, String> hm = new HashMap<String, String>();
        String algorithmName = "md5";
        int hashIterations = 4;
        SimpleHash hash = new SimpleHash(algorithmName, password, salt, hashIterations);
        hm.put("password", password);
        hm.put("encodePassword", hash.toHex());
        hm.put("salt", salt);
        return hm;
    }

}
