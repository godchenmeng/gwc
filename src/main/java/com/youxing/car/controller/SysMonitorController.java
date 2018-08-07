package com.youxing.car.controller;

import com.youxing.car.entity.Device;
import com.youxing.car.entity.OverTime;
import com.youxing.car.service.DeviceService;
import com.youxing.car.service.OvertimeService;
import com.youxing.car.util.ConfigUtils;
import com.youxing.car.util.DateUtils;
import com.youxing.car.util.Result;
import io.swagger.annotations.*;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

@Api(value = "sysmonitor", description = "系统监控")
@Controller
public class SysMonitorController {
    @Resource
    private DeviceService deviceService;
    @Resource
    private OvertimeService otService;

    /**
     *
     * @author CM @date 2017年11月27日
     * @Description: TODO(监控系统车辆信息)
     * @return 监控信息集合
     * @return Result 返回类型
     * @throws
     */
    @ApiOperation(value = "监控系统车辆信息", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "app/sys/device", method = RequestMethod.GET)
    @ResponseBody
    public String sysdeviceinfo(String lastID) {
        JSONObject result = new JSONObject();
        try{
            Properties p = ConfigUtils.getConfig();
            String serverType = p.getProperty("server.type");
            List<Device> devices = deviceService.sysDeviceInfo();
            int onlineCount = 0;
            List<String> outThree = new ArrayList<String>();
            List<String> outSeven = new ArrayList<String>();
            List<String> outData = new ArrayList<String>();
            for(int i = 0; i < devices.size(); i++){
                Device device = devices.get(i);
                if(device.getCreatedate() == null){
                    outData.add(device.getDevice());
                }
                else if(DateUtils.pastDays(DateUtils.parseDate(device.getCreatedate())) >= 7){
                    outSeven.add(device.getDevice());
                }
                else if(DateUtils.pastDays(DateUtils.parseDate(device.getCreatedate())) >= 3 && DateUtils.pastDays(DateUtils.parseDate(device.getCreatedate())) < 7){
                    outThree.add(device.getDevice());
                }
                if(device.getUpdatedate() != null && (new Date().getTime() - DateUtils.parseDate(device.getUpdatedate()).getTime()) <= 1000 * 60 * 5 && device.getStatus().equals("1")){
                    onlineCount++;
                }
            }
            HashMap<String,String> query = new HashMap<String, String>();
            query.put("exchange",serverType);
            query.put("lastID",lastID);
            List<OverTime> overTimes = otService.listBy(query);
            List<Device> unknowDevices = deviceService.unknowDeviceInfo();
//            List<Long> otIDs = new ArrayList<Long>();
//            for(int i = 0; i < overTimes.size(); i++){
//                otIDs.add(overTimes.get(i).getId());
//            }
//            if(otIDs.size() > 0){
//                otService.setRead(otIDs);
//            }
            result.put("status",200);//返回状态 200-成功
            result.put("overTime",overTimes);//超时接口信息
            result.put("allCount",devices.size());//总设备数
            result.put("onlineCount",onlineCount);//在线设备数
            result.put("outThree",outThree);//三日以上无数据
            result.put("outSeven",outSeven);//七日以上无数据
            result.put("outData",outData);//一直无数据
            result.put("unknowDevice",unknowDevices);//未绑定数据
        }catch (Exception ex){
            result.put("status",404);//返回状态 404-失败
            result.put("msg",ex.getMessage());
        }finally {
            return result.toString();
        }
    }
}
