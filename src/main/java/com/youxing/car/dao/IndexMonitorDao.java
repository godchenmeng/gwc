package com.youxing.car.dao;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.IndexMonitorCar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IndexMonitorDao extends BaseDao<IndexMonitorCar> {
    public List<IndexMonitorCar> getIndexMonitorCarByUid(Long id);

    public List<HashMap<String,Object>> getIndexMonitorCarCount(Map<String,Object> map);

    public List<IndexMonitorCar> getIndexMonitorTaskCount(Map<String,Object> map);

    public void addMonitorCar(IndexMonitorCar indexMonitorCar);

    public void removeMonitorCarById(IndexMonitorCar indexMonitorCar);
}
