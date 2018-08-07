package com.youxing.car.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.youxing.car.entity.CarNotice;

/**
 * @author mars
 * @date 2017年4月21日 下午3:12:03
 */
public class GpsUtils {

    /**
     * 
    * @author cm
    * @date 2018年5月21日 下午3:15:22
    * @Description: TODO(获取acc状态，0-熄火(acc为1超过5分钟未运动或者Acc为0)，1-运行，2-静止(acc为1切时间小于5分钟，但是没有速度))
    * @param @param acc
    * @param @param speed
	 * @param @param time
    * @param @return    acc
    * @return String    返回类型
    * @throws
     */
	public static String getCarStatusByACC(String acc, int speed, Date time) {
		if (DateUtils.pastMini(time) > 5) {
			return "0";
		} else if (acc.equals("1") && DateUtils.pastMini(time) <= 5 && speed == 0) {
			return "2";
		}
		return acc;
	}
    /**
     * 
    * @author mars   
    * @date 2017年4月21日 下午3:15:38 
    * @Description: TODO(gps转百度坐标) 
    * @param @param list
    * @param @return    设定文件 
    * @return List<Gps>    返回类型 
    * @throws
     */
	public static List<CarNotice> getBaiduLine(String list) {
		List<CarNotice> line = new ArrayList<CarNotice>();
		String [] gps = list.split(",");
		double[] bdGPS = GPSToBaiduGPS.wgs2bd(Double.parseDouble(gps[1]),Double.parseDouble(gps[0]));
		CarNotice model = new CarNotice();
		model.setLatitude(bdGPS[0]);
		model.setLongitude(bdGPS[1]);
		line.add(model);
		return line;
	}
}
