package com.youxing.car.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component("config")
@Configuration
public class Config {
	
	@Value("#{CONFIG_PROPERTIES['baidu.ak']}") 
	private String ak;
	
	@Value("#{CONFIG_PROPERTIES['baidu.service.id']}")
	private String serviceId;

	@Value("#{CONFIG_PROPERTIES['baidu.proxy']}")
	private String proxy;

	@Value("#{CONFIG_PROPERTIES['baidu.proxy.port']}")
	private String port;

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAk() {
		return ak;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	
}
