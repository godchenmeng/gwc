package com.youxing.car.utils.other;

import com.youxing.car.entity.Car;
import com.youxing.car.entity.InfoCenter;
import com.youxing.car.service.CarService;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.util.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component("EventUtils")
public class EventUtils {
    @Resource
    CarService carService;
    @Resource
    InfoCenterService infoCenterService;

    public void checkEvent(){
        List<Car> inspections = carService.getInspectionOut();
        for(int i = 0; i < inspections.size(); i++){
            Car car = inspections.get(i);
            InfoCenter infoCenter = new InfoCenter();
            infoCenter.setCar_id(car.getId().toString());
            infoCenter.setCar_name(car.getCar_no());
            infoCenter.setOrg(car.getOrg().toString());
            infoCenter.setHappen_time(DateUtils.formatDateTime(new Date()));
            infoCenter.setIllegal_type("6");
            infoCenterService.add(infoCenter);
        }
        List<Car> insurance = carService.getInsuranceOut();
        for(int i = 0; i < insurance.size(); i++){
            Car car = insurance.get(i);
            InfoCenter infoCenter = new InfoCenter();
            infoCenter.setCar_id(car.getId().toString());
            infoCenter.setCar_name(car.getCar_no());
            infoCenter.setOrg(car.getOrg().toString());
            infoCenter.setHappen_time(DateUtils.formatDateTime(new Date()));
            infoCenter.setIllegal_type("5");
            infoCenterService.add(infoCenter);
        }
    }
}
