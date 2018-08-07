package com.youxing.car.service;

import com.youxing.car.entity.IndexMonitorCar;
import com.youxing.car.service.base.BaseService;

import java.util.List;
import java.util.Map;

public interface IndexMonitorService extends BaseService<IndexMonitorCar> {
    public List<IndexMonitorCar> getIndexMonitorCarByUid(Long id);
    public Map<String, Object> getIndexMonitorCarCount(Map<String,Object> map);
    public Map<String, Object> getIndexMonitorTaskCount(Map<String,Object> map);
    public void addMonitorCar(IndexMonitorCar indexMonitorCar);
    public void removeMonitorCarById(IndexMonitorCar indexMonitorCar);
}
