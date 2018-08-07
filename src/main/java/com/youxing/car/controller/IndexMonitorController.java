package com.youxing.car.controller;

import com.youxing.car.aop.LogAnnotation;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.IndexMonitorCar;
import com.youxing.car.entity.User;
import com.youxing.car.generators.StringUtil;
import com.youxing.car.service.CarService;
import com.youxing.car.service.IndexMonitorService;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.service.UserService;
import com.youxing.car.util.Constant;
import com.youxing.car.util.Result;
import com.youxing.car.utils.other.QueryUtilsWeb;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "indexMonitor", description = "首页监控")
@Controller
public class IndexMonitorController {
    @Resource
    private IndexMonitorService indexMonitorService;
    @Resource
    private QueryUtilsWeb queryUtilsWeb;
    @Resource
    private UserService userService;
    @Resource
    private InfoCenterService infoService;
    @Resource
    private CarService carService;

    private static Logger LOGGER = LoggerFactory.getLogger(CarController.class);

    /**
     *
     * @author CM
     * @date 2017年9月15日
     * @Description: TODO(首页监控数据)
     * @return Result    返回类型
     * @throws
     */
    @LogAnnotation(description="首页监控数据" )
    @ApiOperation(value = "首页监控数据", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "web/index/monitor", method = RequestMethod.GET)
    @ResponseBody
    public Result indexMonitor(HttpServletRequest request) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> result = new HashMap<String, Object>();
            User u1 = new User();
            String id1 = request.getParameter("keyid");
            u1.setId(Long.parseLong(id1));
            u1 = userService.findBy(u1);
            queryUtilsWeb.queryByOrgWeb(request, map);
            result.put("car_count", indexMonitorService.getIndexMonitorCarCount(map));
            result.put("task_count", indexMonitorService.getIndexMonitorTaskCount(map));
            result.put("monitor_car", indexMonitorService.getIndexMonitorCarByUid(u1.getId()));
            map.put("startIdx", 0);
            map.put("limit", 4);
            map.put("status", Constant.ADD_STATUS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //map.put("startDate", sdf.format(((new Date()).getTime() - 604800000)));
            //map.put("endDate", sdf.format(new Date())); //获取一个星期内的消息
            result.put("info_center", infoService.findIllegalInfoService(map));
            return Result.success(result);
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

    /**
     *
     * @author CM
     * @date 2017年9月17日
     * @Description: TODO(添加/删除监控车辆)
     * @return Result    返回类型
     * @throws
     */
    @LogAnnotation(description="添加/删除监控车辆" )
    @ApiOperation(value = "添加/删除监控车辆", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "web/op/index/monitor/car", method = RequestMethod.GET)
    @ResponseBody
    public Result opIndexMonitorCar(String opType,String carId,String carNo,HttpServletRequest request) {
        try {
            String uid = request.getParameter("keyid");
            IndexMonitorCar indexMonitorCar = new IndexMonitorCar();
            indexMonitorCar.setUser_id(Long.parseLong(uid));
            if(StringUtil.isEmpty(carId) && StringUtil.isNotEmpty(carNo)){
                HashMap<String, String> maps = new HashMap<String, String>();
                maps.put("car_no_like", carNo);
                maps.put("status", Constant.ADD_STATUS);
                maps.put("car_status", Constant.ADD_STATUS);
                maps.put("obd", Constant.BIND_OBD);
                List<Car> carList = carService.listBy(maps);
                if(carList.size() > 0){
                    carId = carList.get(0).getId().toString();
                }
                else{
                    return Result.error("没有该车辆信息，请确认车牌号是否正确！");
                }
            }
            indexMonitorCar.setCar_id(Long.parseLong(carId));
            //新增
            if (opType.equals("1")) {
                List<IndexMonitorCar> indexMonitorCars = indexMonitorService.getIndexMonitorCarByUid(indexMonitorCar.getUser_id());
                for(int i = 0; i < indexMonitorCars.size(); i++){
                    if(indexMonitorCars.get(i).getCar_id().equals(indexMonitorCar.getCar_id())){
                        return Result.error("您已添加过该车辆了！");
                    }
                }
                indexMonitorService.addMonitorCar(indexMonitorCar);
            } else {//删除
                indexMonitorService.removeMonitorCarById(indexMonitorCar);
            }
            return Result.success();
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }
}
