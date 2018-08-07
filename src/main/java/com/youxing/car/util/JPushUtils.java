package com.youxing.car.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.common.connection.HttpProxy;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.IosAlert;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.push.model.notification.Notification.Builder;
import net.sf.jsqlparser.schema.Server;

/**
 * 
 * @author Administrator
 *
 */
public class JPushUtils {
	protected static final Logger LOG = LoggerFactory.getLogger(JPushUtils.class);
	// app推送的key
	public static final String appKey = "804d49d65db3c16ab7dd08a6";
	public static final String masterSecret = "bb3d36a40225e5188b3b9787";

	/**
	 * jpush配置
	 * 
	 * @return
	 */
	public static ClientConfig getConfig() {
		ClientConfig config = ClientConfig.getInstance();
		config.setApnsProduction(false); // product env
		config.setTimeToLive(0); // 0 表示该消息不保存离线。即：用户在线马上发出，当前不在线用户将不会收到此消息。
		return config;
	}

	/**
	 * app推送实例
	 * 
	 * @return
	 */
	public static JPushClient getJpush() {
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
        if(proxy == null || "".equals(proxy)){
        	return new JPushClient(masterSecret, appKey, null, getConfig());
        }else{
        	return new JPushClient(masterSecret, appKey, new HttpProxy(proxy, Integer.parseInt(port)), getConfig());
        }
	}
	public static void SendAlert(String title, String content, String plat, String... alias){
		if(plat.contains("android")){
			SendAndroidAlert(title, content, new HashMap<String, String>(), alias);
		}else if(plat.contains("iOS")){
			SendIosAlert(title, content, new HashMap<String, String>(), alias);
		}
	}
	/**
	 * ios通知
	 * 
	 * @param title
	 * @param content
	 * @param extras
	 * @param alias
	 */
	public static void SendIosAlert(String title, String content, HashMap<String, String> extras, String... alias) {
		JPushClient jpushClient = getJpush();
		PushPayload payload = null;
		if (jpushClient != null) {
			IosAlert alert = IosAlert.newBuilder().setTitleAndBody(title, content).build();
			Notification nt = new Builder().addPlatformNotification(IosNotification.newBuilder().setAlert(alert).setBadge(1).addExtras(extras).build()).build();
			payload = PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.alias(alias)).setNotification(nt).build();
		}
		try {
			if (payload != null) {
				PushResult result = jpushClient.sendPush(payload);
				LOG.info("Got result - " + result);
			}
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

	/**
	 * 安卓通知
	 * 
	 * @param title
	 * @param content
	 * @param extras
	 * @param alias
	 */
	public static void SendAndroidAlert(String title, String content, HashMap<String, String> extras, String... alias) {
		JPushClient jpushClient = getJpush();
		try {
			PushResult result = jpushClient.sendAndroidNotificationWithAlias(title, content, extras, alias);
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

	/**
	 * 安卓透传消息
	 * 
	 * @param aliases
	 * @param title
	 * @param content
	 * @param extras
	 */
	public static void SendMessageToAndroid(List<String> aliases, String title, String content, Map<String, String> extras) {
		JPushClient jpushClient = getJpush();
		PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.android_ios()).setAudience(Audience.alias(aliases))
				.setMessage(Message.newBuilder().setTitle(title).setMsgContent(content).addExtras(extras).build()).build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

	/**
	 * ios透传消息
	 * 
	 * @param aliases
	 * @param extras
	 * @param title
	 * @param content
	 */
	public static void SendMessageToIos(String aliases, Map<String, String> extras, String title, String content) {
		JPushClient jpushClient = getJpush();
		PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.ios()).setAudience(Audience.alias(aliases))
				.setMessage(Message.newBuilder().setTitle(title).setMsgContent(content).addExtras(extras).build()).build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("Got result - " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}
	
	
	public static void main(String[] args) {
		
		JPushUtils.SendAlert("test", "This is a test！",
				"iOS", "3cc6d40f_95e5_49dc_8234_67165c8fef44");
		
		
		
	}

}
