package com.youxing.car.utils.push;

import com.youxing.car.entity.CarNotice;
import com.youxing.car.entity.Gps;
import com.youxing.car.entity.Organization;
import com.youxing.car.service.CarService;
import com.youxing.car.service.GpsService;
import com.youxing.car.service.OrganizationService;
import com.youxing.car.util.DataUtils;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.GPSToBaiduGPS;
import com.youxing.car.util.GpsUtils;
import com.youxing.car.utils.redis.RedisUtils;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Component
public class GpsHandler extends TextWebSocketHandler {
    @Resource
    private CarService carService;
    @Resource
    private GpsService gpsService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private OrganizationService organizationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(null != session.getAttributes().get("err")){
            session.sendMessage(new TextMessage("402"));
        }else{
            session.sendMessage(new TextMessage("200"));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        WebSocketMessage wsm;
        List<Gps> result  = new ArrayList();
        try {
            if(null == session.getAttributes().get("uorg")) {
                session.sendMessage(new TextMessage("402"));
                return;
            }
            long org = Long.parseLong(session.getAttributes().get("uorg").toString());
            List<Map<String, Object>> info;
            List<Organization>  listOrg = organizationService.countChildService(org);
            List orgs = new ArrayList();
            for(Organization o:listOrg){
                long id=o.getId();
                orgs.add(id);
            }
            orgs.add(org);
            Map<String, Object> map = new HashMap();
            map.put("orgs", orgs);
            info = carService.findCarByDevice(map);
            Map<Object, Object> rgps = redisUtils.hmget("DEVICE.CUR.GPS");
            Map<Object, Object> rcarNotice = redisUtils.hmget("DEVICE.START.GPS");
            for(int i = 0; i < info.size(); i++) {
                Gps gps = null;
                if (null != rgps.get(info.get(i).get("device"))) {
                    String gpsVal = rgps.get(info.get(i).get("device")).toString();
                    String carParam[] = gpsVal.substring(gpsVal.indexOf("[") + 1, gpsVal.length() - 1).split(",", -1);
                    gps = DataUtils.getGps(carParam);
                }else{
                    gps = gpsService.findNewGPSInfo(info.get(i).get("device").toString());
                    if(gps != null) redisUtils.hset("DEVICE.CUR.GPS",gps.getDevice(),DataUtils.gpsToString(gps));
                }
                if(gps != null) {
                    double[] tmpGPS = GPSToBaiduGPS.wgs2bd(gps.getLatitude(), gps.getLongitude());
                    gps.setLatitude(tmpGPS[0]);
                    gps.setLongitude(tmpGPS[1]);
                    if (gps.getTurn_state().equals("3")) {
                        gps.setTurn_state("0");
                    }
                    String noticeVal = rcarNotice.get(gps.getDevice()) == null ? "" : rcarNotice.get(gps.getDevice()).toString();
                    if (!noticeVal.equals("")) {
                        String noticeParam[] = noticeVal.substring(noticeVal.indexOf("[") + 1, noticeVal.length() - 1).split(",", -1);
                        CarNotice carNotice = DataUtils.getCarNotice(noticeParam);
                        tmpGPS = GPSToBaiduGPS.wgs2bd(carNotice.getLatitude(), carNotice.getLongitude());
                        gps.setStart_latitude(tmpGPS[0]);
                        gps.setStart_longitude(tmpGPS[1]);
                        gps.setStartdate(DateUtils.parseDate(carNotice.getSenddate()));
                    } else {
                        gps.setStart_latitude(tmpGPS[0]);
                        gps.setStart_longitude(tmpGPS[1]);
                        gps.setStartdate(gps.getSenddate());
                    }
                    gps.setCar_type(info.get(i).get("car_type") == null ? "" : info.get(i).get("car_type").toString());
                    gps.setCar_id(Long.parseLong(info.get(i).get("car_id").toString()));
                    gps.setCar_no(info.get(i).get("car_no").toString());
                    gps.setOrg_id(Long.parseLong(info.get(i).get("org_id").toString()));
                    gps.setOrg(info.get(i).get("org").toString());
                    gps.setVideo_device(info.get(i).get("video_device") == null ? "" : info.get(i).get("video_device").toString());
                    gps.setTotal_oil(info.get(i).get("tank_vol") == null ? 0D : Double.parseDouble(info.get(i).get("tank_vol").toString()));
                    gps.setRemain_oil((double) Math.round(gps.getFuel_level() * gps.getTotal_oil() / (info.get(i).get("tank_height") == null ? 0D : Double.parseDouble(info.get(i).get("tank_height").toString()))));

                    gps.setAcc(GpsUtils.getCarStatusByACC(gps.getAcc(), gps.getCar_speed(), gps.getSenddate()));
                    result.add(gps);
                }
            }
            wsm = new TextMessage(JSONArray.fromObject(result).toString());
        } catch (Exception e) {
            wsm = new TextMessage("{\"err\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        try {
            session.sendMessage(wsm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
