package com.youxing.car.util;

import java.util.Properties;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:applicationContext-datasource.xml" })
public class PropertiesKey {

	public static String readValue(String key) {
		Properties properties = new Properties();
		try {
			properties.load(PropertiesKey.class.getClassLoader()
					.getResourceAsStream("error.properties"));
			String value = properties.getProperty(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
